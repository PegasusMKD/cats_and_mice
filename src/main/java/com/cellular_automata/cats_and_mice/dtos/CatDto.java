package com.cellular_automata.cats_and_mice.dtos;

import com.cellular_automata.cats_and_mice.models.simulation.AlgorithmType;
import com.cellular_automata.cats_and_mice.models.simulation.BehaviourTypes;
import com.cellular_automata.cats_and_mice.models.simulation.cat.CatTypes;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CatDto {
    public int detectionDistance;
    public CatTypes type;
    public int eatingTime;
    public int[] pos;
    public int speed;
    public AlgorithmType algorithm;
    public BehaviourTypes behaviour;
}
