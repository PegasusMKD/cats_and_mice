package com.cellular_automata.cats_and_mice.models.simulation.universe;

import com.cellular_automata.cats_and_mice.models.simulation.cat.Cat;
import com.cellular_automata.cats_and_mice.models.simulation.mouse.Mouse;
import lombok.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Universe implements Runnable {
    private List<Cat> cats;
    private List<Mouse> mice;
    private int[] size;
    private int time;
    private AtomicBoolean startFlag;
    private AtomicInteger modelsFinished;
    private boolean firstIteration;
    private Thread universeThread;

    public void step() throws InterruptedException {
        int sumValue = cats.size() + mice.size();
        modelsFinished.set(0);
        startFlag.set(true);

        for(Cat cat: cats){
            cat.setWait(false);
            if(firstIteration){
                cat.setUniverse(this);
                cat.start();
            }
        }

        for(Mouse mouse: mice){
            mouse.step(this);
        }

        startFlag.set(false);

        while(modelsFinished.get() != sumValue){
            Thread.sleep(100);
        }
        firstIteration = false;
        step();
    }

    void start() { universeThread.start(); }

    @SneakyThrows
    @Override
    public void run() {
        firstIteration = true;
        step();
    }
}
