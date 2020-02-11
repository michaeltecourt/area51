package com.dreev.area51.participant;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService service;

    @PostMapping("/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public void trigger(@AuthenticationPrincipal Authentication auth, @RequestBody Participant participant) {
        log.info("POST /participant - Authenticated user : {}", auth);
        service.register(participant);
    }

    @GetMapping("/participants")
    public Participants all(@AuthenticationPrincipal Authentication auth) {
        log.info("GET /participant - Authenticated user : {}", auth);
        return service.findAll();
    }

}
