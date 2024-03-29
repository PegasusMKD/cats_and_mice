package com.cellular_automata.cats_and_mice.models.simulation.mouse;

import com.cellular_automata.cats_and_mice.models.simulation.AlgorithmType;
import com.cellular_automata.cats_and_mice.models.simulation.Animal;
import com.cellular_automata.cats_and_mice.models.simulation.BehaviourTypes;
import com.cellular_automata.cats_and_mice.models.simulation.cat.Cat;
import lombok.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Mouse extends Animal implements Runnable {

    private final MouseType type;
    private int afraidDistance;
    private List<Hole> knownSafeHoles;
    private int holeDetectionDistance;
    private final List<int[]> possibleMovements = new ArrayList<>();


    public Mouse(int[] pos, int speed, int afraidDistance, BehaviourTypes behaviour, MouseType type, AlgorithmType algorithm) {
        this.wait = true;
        this.stop = false;
        this.id = UUID.randomUUID().toString();
        this.pos = pos;
        this.afraidDistance = afraidDistance;
        this.algorithm = algorithm;
        this.speed = speed;
        this.type = type;
        this.behaviour = behaviour;
        debuffing();
        generateMovements();
        this.currentThread = new Thread(this, this.id);
    }

    private void generateMovements() {
        possibleMovements.add(new int[]{0, -speed});
        possibleMovements.add(new int[]{0, speed});
        possibleMovements.add(new int[]{-speed, 0});
        possibleMovements.add(new int[]{speed, 0});
        possibleMovements.add(new int[]{speed, -speed});
        possibleMovements.add(new int[]{-speed, speed});
        possibleMovements.add(new int[]{speed, speed});
        possibleMovements.add(new int[]{-speed, -speed});
    }

    private void debuffing() {
        if (type == MouseType.FAT_MOUSE) {
            this.speed -= 2;
            if (this.speed < 0) {
                this.speed = 0;
            }
        } else if (type == MouseType.OLD_MOUSE) {
            this.afraidDistance -= 1;
            if (this.afraidDistance < 0) {
                this.afraidDistance = 0;
            }
        }
    }

    public void beingEaten() {
        // TODO: We can add a run away mechanic later on
        this.kill();
    }

    public void kill() {
        universe.getMice().remove(this);
        stop = true;
    }

    public void step() throws InterruptedException {
        if(stop) {
            universe.getModelsFinished().incrementAndGet();
            return;
        }

        while (universe.getStartFlag().get() || wait) {
            Thread.sleep(100);
        }

        List<Cat> predators = checkForPredators();
        move(predators);
        finish();
    }

    private void move(List<Cat> predators) {
        if (behaviour == BehaviourTypes.PESSIMISTIC && !predators.isEmpty()) {
            return;
        }
        decideDefaultMovement(predators);
    }

    private void decideDefaultMovement(List<Cat> predators) {
        if (algorithm == AlgorithmType.GREEDY) {
            if (predators.isEmpty()) {
                moveToHole();
            } else {
                moveScared(predators);
            }
        } else if (algorithm == AlgorithmType.RANDOM) {
            moveRandom();
        }
    }

    private void moveToHole() {
        // TODO: Change when holes get introduced
        moveRandom();
    }

    private void moveRandom() {
        int distanceToMove = ThreadLocalRandom.current().nextInt(speed);
        int[][] possibilities = {{0,0}, {0, -distanceToMove}, {0, distanceToMove}, {-distanceToMove, 0}, {distanceToMove, 0},
                {distanceToMove, distanceToMove}, {distanceToMove, -distanceToMove}, {-distanceToMove, distanceToMove}, {-distanceToMove, -distanceToMove}};
        int[] chosen = possibilities[ThreadLocalRandom.current().nextInt(9)];
        pos[0] += chosen[0];
        pos[1] += chosen[1];
        checkAndResetBounds();
        // TODO: Add check for whether a hole has been reached
    }


    private void moveScared(List<Cat> predators){
        List<int[]> currentPossibleMoves = new ArrayList<>(possibleMovements);
        for(Cat cat: predators) {
            List<int[]> movesToRemove = new ArrayList<>();
            double distanceFromMouseToCat = Point2D.distance(pos[0], pos[1], cat.getPos()[0], cat.getPos()[1]);
            for(int[] move: currentPossibleMoves){
                if(Point2D.distance(move[0] + pos[0], move[1] + pos[1], cat.getPos()[0], cat.getPos()[1]) < distanceFromMouseToCat){
                    movesToRemove.add(move);
                }
            }
            currentPossibleMoves.removeAll(movesToRemove);
        }

        double tmpDistance = Double.MIN_VALUE;
        int[] finalMove = new int[2];
        for (int[] move : currentPossibleMoves) {
            double mouseToPoint = Point2D.distance(pos[0], pos[1], move[0] + pos[0], move[1] + pos[1]);
            if (tmpDistance < mouseToPoint) {
                tmpDistance = mouseToPoint;
                finalMove = move;
            }
        }

        pos[0] += finalMove[0];
        pos[1] += finalMove[1];
        checkAndResetBounds();
    }

    private List<Cat> checkForPredators() {
        List<Cat> catsList = new ArrayList<>();
        for(Cat cat: universe.getCats()){
            int[] mousePosition = cat.getPos();
            if(Math.abs(pos[0] - mousePosition[0]) <= afraidDistance ||
                    Math.abs(pos[1] - mousePosition[1]) <= afraidDistance ||
                    Math.abs(Math.round(Point2D.distance(pos[0], pos[1], mousePosition[0], mousePosition[1]))) <= afraidDistance){
                catsList.add(cat);
            }
        }
        return catsList;
    }


    private void finish() throws InterruptedException {
        wait = true;
        universe.getModelsFinished().incrementAndGet();
        step();
    }

    @SneakyThrows
    @Override
    public void run() {
        step();
    }

    public void start() {
        this.currentThread.start();
    }
}
