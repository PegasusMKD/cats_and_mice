package com.cellular_automata.cats_and_mice.models.simulation.cat;

import com.cellular_automata.cats_and_mice.models.simulation.AlgorithmType;
import com.cellular_automata.cats_and_mice.models.simulation.Animal;
import com.cellular_automata.cats_and_mice.models.simulation.BehaviourTypes;
import com.cellular_automata.cats_and_mice.models.simulation.Utils;
import com.cellular_automata.cats_and_mice.models.simulation.mouse.Mouse;
import lombok.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
@Getter
@Setter
public class Cat extends Animal implements Runnable {

    private int detectionDistance;
    private CatTypes type;
    private int eatingTime;
    private int eatingTimeToElapse;

    public Cat(int[] pos, int detectionDistance, int speed, CatTypes type, BehaviourTypes behaviour){
        this.wait = true;
        this.stop = false;
        this.eatingTimeToElapse = 0;
        this.id = UUID.randomUUID().toString();
        this.pos = pos;
        this.detectionDistance = detectionDistance;
        this.algorithm = AlgorithmType.RANDOM; // TODO: Add posibility to change this
        this.speed = speed;
        this.type = type;
        this.behaviour = behaviour;
        debuffing();
        determineEatingTime();
        this.currentThread = new Thread(this, this.id);
    }

    public void kill(){
        stop = true;
    }

    public void start() {
        currentThread.start();
    }

    @SneakyThrows
    @Override
    public void run() {
        step();
    }

    private void step() throws InterruptedException {
        // Thread and step control
        if(stop) {
            return;
        }

        while(universe.getStartFlag().get() || wait){
            Thread.sleep(100);
        }

        if(eatingTimeToElapse > 0) {
            eatingTimeToElapse--;
            finish();
        }

        // Main logic
        List<Mouse> closePrey = checkForPrey();
        move(closePrey);

        // Ending part, mostly used for cleaning up variables and such
        finish();
    }

    private void debuffing(){
        switch (this.type) {
            case FAT_CAT:
                fatCatDebufs();
                break;
            case OLD_CAT:
                oldCatDebufs();
                break;
        }
    }

    private void determineEatingTime() {
        switch (type) {
            case CAT:
                eatingTime = 2;
                break;
            case FAT_CAT:
                eatingTime = 1;
                break;
            case OLD_CAT:
                eatingTime = 4;
                break;
        }
    }

    private void fatCatDebufs() {
        this.speed -= 2;
    }

    private void oldCatDebufs(){
        this.detectionDistance -= 1;
    }

    private boolean checkForPreyOnCurrentPosition(List<Mouse> closePrey) {
        List<Mouse> mice = closePrey.stream().filter(mouse -> mouse.getPos()[0] == pos[0] && mouse.getPos()[1] == pos[1]).collect(Collectors.toList());
        if(!mice.isEmpty()){
            eatingTimeToElapse = eatingTime;
            // TODO: Remove those mice from the list in the universe
            mice.forEach(Mouse::beingEaten);
            return true;
        }
        return false;
    }

    private void move(List<Mouse> closePrey) throws InterruptedException {
        switch(algorithm){
            case RANDOM:
                moveRandom(closePrey);
                break;
            case GREEDY:
                if(closePrey.isEmpty()){
                    moveRandom(closePrey);
                } else {
                    reworkedSemiOptimalGreedyMovement(closePrey);
                }
                break;
        }
    }

    /**
     * Too many problems to write down :(
     * <p>
     *  Some of them:
     *  <ul>
     *     <li>Restricted diagonal movement</li>
     *     <li>Bad movement logic</li>
     *     <li>Inconsistent movement</li>
     *  </ul>
     * </p>
     * @deprecated
     * @param closePrey
     */
    @Deprecated
    private void moveGreedy(List<Mouse> closePrey) {
        // TODO: Not really optimal, since it might see that one of it's coordinates is a distance of
        List<Integer[]> distancesAndIdx = new ArrayList<>();
        for(Mouse mouse: closePrey){
            int[] mousePosition = mouse.getPos();
            long diagonalDistance = Math.round(Point2D.distance(pos[0], pos[1], mousePosition[0], mousePosition[1]));

            int[] distances = {pos[0] - mousePosition[0], pos[1] - mousePosition[1], (int) diagonalDistance};

            int idx = Utils.getIdxOfSmallest(distances, 3);
            distancesAndIdx.add(new Integer[] {idx, distances[idx]});
        }

        Integer[] idx = Utils.getIdxOfSmallestPair(distancesAndIdx);
        if(speed < idx[1]){
            if(idx[0] == 2) {
                pos[0] += speed * (idx[1] < 0 ? -1 : 1);
                pos[1] += speed * (idx[1] < 0 ? -1 : 1);
            } else {
                pos[idx[0]] += speed * (idx[1] < 0 ? -1 : 1);
            }
        } else {
            if(idx[0] == 2) {
                pos[0] += idx[1];
                pos[1] += idx[1];
            } else {
                pos[idx[0]] += idx[1];
            }
        }
        if(pos[0] > universe.getSize()[0]){
            pos[0] = universe.getSize()[0];
        } else if(pos[1] > universe.getSize()[1]){
            pos[1] = universe.getSize()[1];
        }
    }

    /**
     * Function for moving the cat in a somewhat optimal way
     *
     * <p>
     *     Couple of steps to the algorithm:
     *     <ol>
     *         <li>Treats the mice as points, and finds the distances from the cat to those mice</li>
     *         <li>Finds the index of the mouse with the minimal distance</li>
     *         <li>Uses a variable named availableMovements as a counter to how many moves it can do(Counts a diagonal move as one)</li>
     *         <li>Then, while it has available moves, it does this:</li>
     *         <ul>
     *             <li>Checks whether y or x distance between the cat and mouse is 0, and if it is it moves on the opposite axis</li>
     *             <li>If it is, then it also checks whether the distance is smaller than the available movements (so that it doesn't go over the mouse)</li>
     *             <li>If it isn't in the same row/column as the mouse, it moves diagonally towards it (since it basically does 2 moves as one)</li>
     *             <li>And it finally checks whether it's landed on prey</li>
     *         </ul>
     *     </ol>
     * </p>
     * @param closePrey
     */
    private void reworkedSemiOptimalGreedyMovement(List<Mouse> closePrey) throws InterruptedException {
        List<Double> distances = new ArrayList<>();
        closePrey.forEach(mouse -> distances.add(Point2D.distanceSq(pos[0], pos[1], mouse.getPos()[0], mouse.getPos()[1])));
        int idx = Utils.getIdxOfSmallestDistance(distances);
        Mouse closestMouse = closePrey.get(idx);
        int availableMovements = speed;
        while(availableMovements != 0){
            int xDistance = pos[0] - closestMouse.getPos()[0];
            int yDistance = pos[1] - closestMouse.getPos()[1];
            if(xDistance == 0) {
                if(Math.abs(yDistance) < availableMovements){
                    availableMovements = Math.abs(yDistance);
                }
                pos[1] += availableMovements * (yDistance < 0 ? -1 : 1);
                availableMovements = 0;
            } else if(yDistance == 0){
                if(Math.abs(xDistance) < availableMovements){
                    availableMovements = Math.abs(xDistance);
                }
                pos[0] += availableMovements * (xDistance < 0 ? -1 : 1);
                availableMovements = 0;
            } else {
                pos[0] += xDistance < 0 ? -1 : 1;
                pos[1] += yDistance < 0 ? -1 : 1;
                availableMovements--;
            }
            if(checkForPreyOnCurrentPosition(closePrey)) {
                finish();
            }
        }
    }

    private void moveRandom(List<Mouse> closePrey) throws InterruptedException {
        int distanceToMove = ThreadLocalRandom.current().nextInt(speed);
        int[][] possibilities = {{0,0}, {0, -distanceToMove}, {0, distanceToMove}, {-distanceToMove, 0}, {distanceToMove, 0},
                {distanceToMove, distanceToMove}, {distanceToMove, -distanceToMove}, {-distanceToMove, distanceToMove}, {-distanceToMove, -distanceToMove}};
        int[] chosen = possibilities[ThreadLocalRandom.current().nextInt(9)];
        pos[0] += chosen[0];
        pos[1] += chosen[1];
        if(checkForPreyOnCurrentPosition(closePrey)) {
            finish();
        }
    }

    private void finish() throws InterruptedException {
        wait = true;
        universe.getModelsFinished().incrementAndGet();
        step();
    }

    private List<Mouse> checkForPrey(){
        List<Mouse> mouseList = new ArrayList<>();
        for(Mouse mouse: universe.getMice()){
            int[] mousePosition = mouse.getPos();
            if(Math.abs(pos[0] - mousePosition[0]) <= detectionDistance ||
            Math.abs(pos[1] - mousePosition[1]) <= detectionDistance ||
            Math.abs(Point2D.distance(pos[0], pos[1], mousePosition[0], mousePosition[1])) <= detectionDistance){
                mouseList.add(mouse);
            }
        }
        return mouseList;
    }


}
