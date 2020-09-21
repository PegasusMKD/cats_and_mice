package com.cellular_automata.cats_and_mice.controllers;

import com.cellular_automata.cats_and_mice.dtos.UniverseDto;
import com.cellular_automata.cats_and_mice.services.UniverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UniverseController {

    @Autowired
    private UniverseService universeService;

    @PostMapping("/")
    public ResponseEntity<Integer> createAndGetResults(@RequestBody UniverseDto universeDto) throws InterruptedException {
        return ResponseEntity.ok(universeService.start(universeDto));
    }
}
