package com.cellular_automata.cats_and_mice.dtos;

import com.cellular_automata.cats_and_mice.models.simulation.AlgorithmType;
import com.cellular_automata.cats_and_mice.models.simulation.BehaviourTypes;
import com.cellular_automata.cats_and_mice.models.simulation.mouse.Hole;
import com.cellular_automata.cats_and_mice.models.simulation.mouse.MouseType;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MouseDto {
    public int[] pos;
    public int speed;
    public AlgorithmType algorithm;
    public BehaviourTypes behaviour;
    public MouseType type;
    public int afraidDistance;

    // TODO: Yet to be implemented
    public List<Hole> knownSafeHoles;
    public int holeDetectionDistance;
}
