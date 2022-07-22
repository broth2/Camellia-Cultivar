package com.camellia.controllers;

import javax.validation.Valid;

import com.camellia.mappers.IdentificationRequestMapper;
import com.camellia.mappers.SpecimenMapper;
import com.camellia.models.requests.IdentificationRequest;
import com.camellia.models.requests.IdentificationRequestDTO;
import com.camellia.models.requests.IdentificationRequestView;
import com.camellia.models.requests.ReportRequestDTO;
import com.camellia.models.specimens.Specimen;
import com.camellia.models.specimens.SpecimenDto;
import com.camellia.models.users.User;
import com.camellia.services.requests.IdentificationRequestService;
import com.camellia.services.specimens.SpecimenService;
import com.camellia.services.users.UserService;

import twitter4j.TwitterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.camellia.models.requests.CultivarRequestDTO;
import com.camellia.services.requests.CultivarRequestService;
import com.camellia.services.requests.ReportRequestService;

import java.io.IOException;
import java.net.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    
    @Autowired
    ReportRequestService reportRequestService;

    @Autowired
    CultivarRequestService cultivarRequestService;

    @Autowired
    IdentificationRequestService identificationRequestService;

    @Autowired
    UserService userService;

    @Autowired
    SpecimenService specimenService;

    @Autowired
    RestTemplate restTemplate;

    @PostMapping(value="/report", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> createReportRequest(@Valid @RequestBody ReportRequestDTO report){
        if(checkRoleRegistered()){
            reportRequestService.createReportRequest(report);
            return ResponseEntity.status(HttpStatus.CREATED).body("");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @PostMapping("/cultivar")
    public ResponseEntity<String> createCultivarRequest(@RequestBody CultivarRequestDTO cultivarSuggestion){
        if(checkRoleRegistered())
            return cultivarRequestService.createCultivarRequest(cultivarSuggestion);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    @PostMapping("/identification")
    public ResponseEntity<IdentificationRequestDTO> createSpecimen(
            @RequestBody SpecimenDto specimenDto
    ) throws TwitterException {
        boolean requesterHasAutoApproval;
        if(!photoRecognition(specimenDto.getPhotoUrl())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        try {
            requesterHasAutoApproval = userService.requesterHasAutoApproval();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Specimen newSpecimen = specimenService.saveSpecimen(
                requesterHasAutoApproval ?
                        SpecimenMapper.MAPPER.specimenDTOtoToIdentifySpecimen(specimenDto)
                        : SpecimenMapper.MAPPER.specimenDTOToForApprovalSpecimen(specimenDto)
        );

        IdentificationRequest newIdentificationRequest =
                identificationRequestService.createNewIdentificationRequestFromSpecimen(newSpecimen);

        return ResponseEntity.ok(IdentificationRequestMapper.MAPPER.identificationRequestToIdentificationRequestDTO(
                newIdentificationRequest
        ));
    }

    @GetMapping("/identification")
    public ResponseEntity<List<IdentificationRequestView>> getIdentificationRequestsOfUser(Authentication authentication){
        User user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(identificationRequestService.getAllIdentificationRequestsForUser(user));
    }

    public boolean checkRoleRegistered(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User u = userService.getUserByEmail(auth.getName());

        return u != null && (u.getRolesList().contains("REGISTERED") || u.getRolesList().contains("MOD") || u.getRolesList().contains("ADMIN"));
    }

    private boolean photoRecognition(String photo_url){
        String b_url = "http://192.168.160.226:5000/predict?url=" + photo_url;
        try {
            URL url = new URL(b_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int rCode = conn.getResponseCode();
            // code 201 means absolute certainty of the photo request
            // code 200 means it could be better
            if (200 == rCode || 201 == rCode) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
