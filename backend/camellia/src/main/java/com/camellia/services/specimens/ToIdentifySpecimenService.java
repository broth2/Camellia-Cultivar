package com.camellia.services.specimens;

import com.camellia.mail.*;
import com.camellia.mappers.SpecimenMapper;
import com.camellia.models.characteristics.CharacteristicValue;
import com.camellia.models.cultivars.Cultivar;
import com.camellia.models.specimens.Specimen;
import com.camellia.models.specimens.SpecimenDto;
import com.camellia.models.users.User;
import com.camellia.repositories.specimens.SpecimenRepository;
import com.camellia.services.users.UserService;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.mail.MessagingException;

@Service
public class ToIdentifySpecimenService {
    @Autowired
    private SpecimenRepository specimenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    public Page<Specimen> getToIdentifySpecimens(Pageable pageable) {
        return specimenRepository.findAllToIdentify(pageable);
    }

    public List<Specimen> getToIdentifySpecimens() {
        return specimenRepository.findAllToIdentify();
    }

    public Specimen getToIdentifySpecimenById(long id) {
        return specimenRepository.findToIdentifyById(id);
    }

    public void updateVotes(Specimen specimen, Map<Cultivar, Double> votes){
        specimen.setCultivarProbabilities(votes);
    }

    public List<Specimen> getRecentlyUploaded() {
        return specimenRepository.findAllToIdentifyBy(
                PageRequest.of(0, 10, Sort.by("specimenId").descending())
        );
    }

    public SpecimenDto promoteToReferenceFromId(long id, Cultivar c) throws UnsupportedEncodingException, MessagingException, TwitterException {
        Specimen promotingSpecimen = this.getToIdentifySpecimenById(id);
        return promoteToReference(promotingSpecimen, c);
    }


    public SpecimenDto promoteToReference(Specimen promotingSpecimen, Cultivar c) throws UnsupportedEncodingException, MessagingException, TwitterException {
        if (promotingSpecimen == null)
            return null;

        promotingSpecimen.promoteToReference();
        promotingSpecimen.setCultivar(c);

        if(c.getCharacteristicValues().isEmpty()){
            Iterator<CharacteristicValue> it =  promotingSpecimen.getCharacteristicValues().iterator();
            List<CharacteristicValue> list = new ArrayList<>();
            while(it.hasNext())
                list.add(it.next());
            System.out.println(list);
            c.setCharacteristicValues(list);
        }
        specimenRepository.saveAndFlush(promotingSpecimen);


        sendSpecimenIdentificationNotifications(promotingSpecimen);

        return SpecimenMapper.MAPPER.specimenToSpecimenDTO(promotingSpecimen);
    }



    private void sendSpecimenIdentificationNotifications(Specimen s)
        throws UnsupportedEncodingException, MessagingException, TwitterException {
       
        User user = userService.getUserById( specimenRepository.findUserById(s.getSpecimenId()) );

        String toAddress = user.getEmail();
        String subject = "Specimen Identified";
        String content = "Dear user,"
                + "One of your specimens (ID:" + s.getSpecimenId() + ") has been identified as "+s.getCultivar()+". Get back on the app to check it.<br>"
                + "Thank you,<br>"
                + "Cammelia Cultivar";
        
        //mailSender.send(message);
        Email e = new Email(EmailConsts.OUR_EMAIL , toAddress);
        e.setSubject(subject);
        e.setText(content);

        if(emailService.send(e)){
            System.out.printf("\nMail Sent with success!\n");
        }else{
            System.out.println("ERROR sending mail!");
        }

        // sends a tweet when system considers specimen to be identified
        TwitterFactory tf = new TwitterFactory();
        Twitter twitter = tf.getInstance();

        twitter.updateStatus("Specimen with ID " + s.getSpecimenId() + " has been identified by the community as " + s.getCultivar());
        
    }
}
