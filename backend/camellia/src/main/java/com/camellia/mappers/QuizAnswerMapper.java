package com.camellia.mappers;

import com.camellia.models.QuizAnswer;
import com.camellia.models.QuizAnswerDTO;
import com.camellia.models.cultivars.Cultivar;
import com.camellia.models.specimens.Specimen;
import com.camellia.models.users.User;
import com.camellia.services.cultivars.CultivarService;
import com.camellia.services.specimens.SpecimenService;
import org.hibernate.MappingException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper( componentModel = "spring" )
public abstract class QuizAnswerMapper {
    @Autowired
    protected SpecimenService specimenService;
    @Autowired
    protected CultivarService cultivarService;

    public QuizAnswer quizAnswerDTOToQuizAnswer(QuizAnswerDTO quizAnswerDTO, @Context User user) {
        if (user == null) throw new MappingException("Cannot map: passed User is null");
        if (!quizAnswerDTO.isValid()) throw new MappingException("Cannot map: null fields on QuizAnswerDTO");

        Specimen quizzedSpecimen = specimenService.getSpecimenById(quizAnswerDTO.getSpecimenId());
        if (quizzedSpecimen == null) throw new MappingException(String.format("Cannot map: Specimen[%d]", quizAnswerDTO.getSpecimenId()));
        
        Cultivar answeredCultivar = cultivarService.getCultivarById(quizAnswerDTO.getAnswer());
        if (answeredCultivar == null) throw new MappingException(String.format("Cannot map: Cultivar[%d]", quizAnswerDTO.getAnswer()));
        //vai buscar o specimen da resposta certa, a partir do id que vem no json, depois ve no objeto specimen a que cultivar ele se refere
        //vai buscar a cultivar da resposta do user a partir do id que vem no json
        QuizAnswer quizAnswer = new QuizAnswer();
        quizAnswer.setCultivar(answeredCultivar);
        quizAnswer.setSpecimen(quizzedSpecimen);
        quizAnswer.setUser(user);
        quizAnswer.setSpecimenType(quizzedSpecimen.getSpecimenType());

        if (quizzedSpecimen.isReference()) {
            Cultivar actualCultivar = quizzedSpecimen.getCultivar();
            System.out.println("\n\n\nWe are in quizanswermapper:\n" + answeredCultivar + "\n" + actualCultivar + "\n" + answeredCultivar.equals(actualCultivar));
            quizAnswer.setCorrect(answeredCultivar.equals(actualCultivar));
            return quizAnswer;
        }

        quizAnswer.setCorrect(false);
        return quizAnswer;
    }

    public abstract List<QuizAnswer> quizAnswerDTOsToQuizAnswers(List<QuizAnswerDTO> quizAnswersDTOs, @Context User user);
}

//recebe pares de numeros, o primeiro é o specimen id que depois de transformado para cultivar id é a resposta esperada
// o segundo é o answer cultivar id que é ja uma das milhares de cultivares
// neste momento todos os specimens dao correspondencia com a cultivar com o id 1
