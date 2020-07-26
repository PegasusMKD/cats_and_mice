package com.cellular_automata.cats_and_mice.dtos;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UniverseDto {
    public String id;
    public String name;
    public List<CatDto> cats;
    public List<MouseDto> mice;
    public int[] size;
}
