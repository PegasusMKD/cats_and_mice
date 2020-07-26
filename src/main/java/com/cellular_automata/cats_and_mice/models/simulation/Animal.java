package com.cellular_automata.cats_and_mice.models.simulation;

import com.cellular_automata.cats_and_mice.models.simulation.universe.Universe;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Data
@Getter
@Setter
abstract public class Animal {
    protected String id;
    protected int[] pos;
    protected int speed;
    protected Universe universe;
    protected boolean wait;
    protected boolean stop;
    protected Thread currentThread;
    protected AlgorithmType algorithm;
    protected BehaviourTypes behaviour;

}
