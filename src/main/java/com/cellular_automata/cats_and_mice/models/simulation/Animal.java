package com.cellular_automata.cats_and_mice.models.simulation;

import com.cellular_automata.cats_and_mice.models.simulation.universe.Universe;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public abstract class Animal {
    protected String id;
    protected int[] pos;
    protected int speed;
    protected Universe universe;
    protected boolean wait;
    protected boolean stop;
    protected Thread currentThread;
    protected AlgorithmType algorithm;
    protected BehaviourTypes behaviour;

    protected void checkAndResetBounds() {
        if (pos[0] > universe.getSize()[0]) {
            pos[0] = universe.getSize()[0];
        } else if (pos[0] < 0) {
            pos[0] = 0;
        }
        if (pos[1] > universe.getSize()[1]) {
            pos[1] = universe.getSize()[1];
        } else if (pos[1] < 0) {
            pos[1] = 0;
        }
    }

}
