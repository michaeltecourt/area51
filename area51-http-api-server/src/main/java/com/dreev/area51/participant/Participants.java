package com.dreev.area51.participant;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Participants {

    private final List<Participant> participants;
    
}
