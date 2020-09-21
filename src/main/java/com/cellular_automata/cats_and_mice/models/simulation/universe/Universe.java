package com.cellular_automata.cats_and_mice.models.simulation.universe;

import com.cellular_automata.cats_and_mice.models.simulation.cat.Cat;
import com.cellular_automata.cats_and_mice.models.simulation.mouse.Mouse;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Universe implements Runnable {
    private String id;
    private String name;
    private CopyOnWriteArrayList<Cat> cats;
    private CopyOnWriteArrayList<Mouse> mice;
    private int[] size;
    private int time;
    private boolean running;
    private AtomicBoolean startFlag;
    private AtomicInteger modelsFinished;
    private boolean firstIteration;
    private Thread universeThread;

    public Universe(String name, List<Cat> cats, List<Mouse> mice, int[] size) {
        this.id = UUID.randomUUID().toString();
        this.name = name.isEmpty() ? this.id : name;
        this.cats = new CopyOnWriteArrayList<>(cats);
        this.mice = new CopyOnWriteArrayList<>(mice);
        this.size = size;
        this.time = 0;
        this.startFlag = new AtomicBoolean();
        this.startFlag.set(true);
        this.modelsFinished = new AtomicInteger();
        this.modelsFinished.set(0);
        this.firstIteration = true;
        this.universeThread = new Thread(this, this.id);
    }


    public void step() throws InterruptedException {
        int sumValue = cats.size() + mice.size();
        if (mice.isEmpty() || time > 1000) {
            running = false;
            return;
        }
        modelsFinished.set(0);
        startFlag.set(true);

        for (Cat cat : cats) {
            cat.setWait(false);
            if (firstIteration) {
                cat.setUniverse(this);
                cat.start();
            }
        }

        for(Mouse mouse: mice){
            mouse.setWait(false);
            if(firstIteration){
                mouse.setUniverse(this);
                mouse.start();
            }
        }

        startFlag.set(false);

        while (modelsFinished.get() < sumValue) {
            Thread.sleep(100);
        }
        time++;
        firstIteration = false;
        updateFrontEndState();
        step();
    }

    private void updateFrontEndState() {
        // TODO: Use this to update the visuals on the front-end (this would be easier and more efficient compared to sending a request on each change)
    }

    public void start() {
        running = true;
        this.universeThread.start();
    }

    public void join() throws InterruptedException {
        this.universeThread.join();
    }

    @SneakyThrows
    @Override
    public void run() {
        firstIteration = true;
        step();
    }
}
