package com.camellia.services;

import com.camellia.mail.*;
import com.camellia.mappers.QuizAnswerMapper;
import com.camellia.repositories.QuizRepository;
import com.camellia.services.specimens.SpecimenService;
import com.camellia.services.specimens.ToIdentifySpecimenService;
import com.camellia.services.users.UserService;
import com.camellia.models.QuizAnswer;
import com.camellia.models.QuizAnswerDTO;
import com.camellia.models.cultivars.Cultivar;
import com.camellia.models.specimens.Specimen;
import com.camellia.models.specimens.SpecimenQuizDTO;
import com.camellia.models.users.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

@Service
public class QuizService {
    @Autowired
    private QuizRepository repository;

    @Autowired
    private QuizParametersService quizParametersService;

    @Autowired
    private SpecimenService specimenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ToIdentifySpecimenService toIdentifySpecimenService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuizAnswerMapper mapper;

    public Page<QuizAnswer> getQuizzes(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<QuizAnswer> getQuizzes() {
        return repository.findAll();
    }

    public QuizAnswer getQuizById(long id) {
        return repository.findById(id);
    }

    public List<SpecimenQuizDTO> generateQuiz(User user){
        int noToIdentifySpecimens = quizParametersService.getToIdentifyNo();
        int noReferenceSpecimens = quizParametersService.getReferenceNo();

        List<SpecimenQuizDTO> quiz;

        Set<QuizAnswer> answers = repository.findByUser(user);
        Iterator<QuizAnswer> iterator = answers.iterator();

        Set<Long> answeredQuizzesIds = new HashSet<>();

        while(iterator.hasNext()) {
            QuizAnswer f = iterator.next();
            answeredQuizzesIds.add(f.getSpecimen().getSpecimenId());
        }

        quiz = specimenService.getQuizSpecimens(answeredQuizzesIds, noReferenceSpecimens, noToIdentifySpecimens);

        return quiz;
    }


    public ResponseEntity<String> saveQuizAnswers(User user, List<QuizAnswerDTO> quizAnswers) {

        if(user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user");
        }
        
        List<QuizAnswer> answers = repository.saveAllAndFlush(mapper.quizAnswerDTOsToQuizAnswers(quizAnswers, user));

        System.out.println("------>\n" + answers);
        answers.stream().filter(QuizAnswer::isToIdentify).forEach(this::calculateSpecimenProbabilities);

        long numRefSpecimen=answers.stream().filter(QuizAnswer::isReference).count();
        long numCorrectRefSpecimenAnswers = answers.stream().filter(QuizAnswer::isReference).filter(QuizAnswer::getCorrect).count();

        calculateNewReputation(user, numRefSpecimen, numCorrectRefSpecimenAnswers);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user.getReputation() + "");
    }

    public void calculateNewReputation(User user, long refCount, long correctRef){
        if (correctRef==0) return;
        System.out.println("User got " + correctRef + " out of " + refCount  + " answers");
        System.out.println("therefore, new rep is: " + Math.pow(correctRef, 1.49)/1.5);
        System.out.println();

        
        userService.saveReputation(user, user.getReputation() + Math.pow(correctRef, 1.49)/1.5);
    }

    private void calculateSpecimenProbabilities(QuizAnswer quizAnswer) {
        //associa no mapa interno do specimen, a cultivar respondida com a prob 0
        //vai buscar todas as cultivares que os users ja responderam

        Specimen specimen = quizAnswer.getSpecimen(); //expected(actual cultivar)
        Cultivar cultivar = quizAnswer.getCultivar(); //answered(answered cultivar)
        System.out.println("We are in specimen id: " + specimen.getSpecimenId());
        
        specimen.addCultivarProb(cultivar, 0);

        System.out.println("these are the votes: "+ specimen.getCultivarProbabilities());

        Map<Cultivar, Double> probableCultivars = specimen.getProbableCultivarStream()
                .collect(Collectors.toMap(
                        probableCultivar ->  probableCultivar,
                        probableCultivar ->  calculateProbabilityOfSpecimenBeingCultivar(specimen, probableCultivar)
                ));

        specimen.setCultivarProbabilities(probableCultivars);

        System.out.println("->"+probableCultivars);
        specimenService.saveSpecimen(specimen);

        Optional<Cultivar> highProbabilityCultivar = probableCultivars.entrySet().stream()
                .filter(cultivarDoubleEntry -> cultivarDoubleEntry.getValue() > 40.0)
                .map(Map.Entry::getKey)
                .findAny();

        if (highProbabilityCultivar.isPresent()) {
            try {
                //maybe promote to approval instead
                toIdentifySpecimenService.promoteToReference(specimen, highProbabilityCultivar.get());
                //after or instead of mod approval, improve and decrease rep acordingly
                List<User> correctUsers = repository.getUsersFromCultivar(specimen, highProbabilityCultivar.get());
                for(User quizzUser :repository.getUsersFromSpecimen(specimen)){
                    //notify
                    if (correctUsers.contains(quizzUser)){
                        updateUserRep(quizzUser, probableCultivars.get(highProbabilityCultivar.get()));
                        notifyUser(quizzUser, specimen,highProbabilityCultivar.get() );
                    }else{
                        removeUserRep(quizzUser, probableCultivars.get(highProbabilityCultivar.get()));
                    }
                }
            } catch (UnsupportedEncodingException | MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        specimenService.saveSpecimen(specimen);
    }

    private double calculateProbabilityOfSpecimenBeingCultivar(Specimen specimen, Cultivar cultivar) {
        double reputationAvg = sumUserReputationThatAnsweredCultivarForSpecimen(cultivar, specimen);
        int specificVotes = repository.getUsersFromCultivar(specimen, cultivar).size();
        int totalVotes = repository.getTotalVotesForSpecimen(specimen.getSpecimenId());

        double prob = Math.pow(reputationAvg, specificVotes/totalVotes) * reputationAvg/10;
        return prob;
    }

    private double sumUserReputationThatAnsweredCultivarForSpecimen(Cultivar cultivar, Specimen specimen) {
        List<User> lst = repository.getUsersFromCultivar(specimen, cultivar);
        System.out.println("Users that gave this combination of specimen/cultivar answer: " + 
                                lst);
        return repository.getUsersFromCultivar(specimen, cultivar).stream()
                        .map(User::getReputation)
                        .reduce(Double::sum)
                        .orElse(0.0) / lst.size();
    }

    private void updateUserRep(User user, Double cultivarCertanty){
        double currentRep = user.getReputation();
        double e = 2.72;
        double baseAmmount = Math.pow(e, -currentRep/12)*15 + 3;
        //popularity penalty ammount
        double penalty = cultivarCertanty/40;
        double rep = baseAmmount/penalty;
        System.out.println("final rep:" +rep);
        userService.saveReputation(user, user.getReputation() + rep);

    }

    private void removeUserRep(User user, Double cultivarCertanty){
        double currentRep = user.getReputation();
        double e = 2.72;
        double baseAmmount = Math.pow(e, -currentRep/12)*15 + 3;
        //popularity penalty ammount
        double penalty = cultivarCertanty/40;
        double rep = baseAmmount/penalty;
        System.out.println("final rep:" +rep);
        userService.saveReputation(user, user.getReputation() - rep/5);
    }

    private void notifyUser(User usr, Specimen spcmn, Cultivar cltvr){
        String content = "Dear [[name]],<br>"
                + "Your identification of the specimen "+ spcmn.getSpecimenId() +" with the cultivar "+ cltvr +" has been deemed correct by the community, congratulations!<br>"
                + "Your Reputation will change accordingly, keep up the good work."
                + "Thank you,<br>"
                + "Cammelia Cultivar";
        content = content.replace("[[name]]", usr.getFirstName().concat(" ").concat(usr.getLastName()));
        Email e = new Email(EmailConsts.OUR_EMAIL , usr.getEmail());
        e.setSubject("A specimen you voted on was identified!");
        e.setText(content);

        if(emailService.send(e)){
            System.out.printf("\nMail Sent with success!\n");
        }else{
            System.out.println("ERROR sending mail!");
        }
    }
}


//esquema da reputacao
//para um dado numero de camelias referencia(digamos 6)
//por cada quizz certo ganhas exponencialmente mais rep
//entre 0 a 6 quizzes certos e entre 0 a 10 rep
//nao perde rep por errar num quizz de referencia(mas tb nao o repete)
//FORMULA: (x^1.49)/1.03, sendo x o numero de respostas certas
//
//para as que nao sao referencia, a rep so e atualizada quando fica confirmada
//perde ou ganha % de rep, baseado na streak de certas
//ha patamares de reputacao