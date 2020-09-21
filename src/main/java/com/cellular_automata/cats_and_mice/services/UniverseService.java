package com.cellular_automata.cats_and_mice.services;

import com.cellular_automata.cats_and_mice.dtos.UniverseDto;
import com.cellular_automata.cats_and_mice.models.simulation.cat.Cat;
import com.cellular_automata.cats_and_mice.models.simulation.mouse.Mouse;
import com.cellular_automata.cats_and_mice.models.simulation.universe.Universe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UniverseService {
    public int start(UniverseDto universeDto) throws InterruptedException {
        ArrayList<Cat> catsList = new ArrayList<>();
        ArrayList<Mouse> miceList = new ArrayList<>();
        universeDto.getCats().forEach(dto -> catsList.add(new Cat(dto.pos, dto.detectionDistance, dto.speed, dto.eatingTime, dto.type, dto.behaviour, dto.algorithm)));
        universeDto.getMice().forEach(dto -> miceList.add(new Mouse(dto.pos, dto.speed, dto.afraidDistance, dto.behaviour, dto.type, dto.algorithm)));
        Universe universe = new Universe(universeDto.name, catsList, miceList, universeDto.size);
        universe.start();

        universe.join();
        return universe.getTime();
    }
}
