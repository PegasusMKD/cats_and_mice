package com.cellular_automata.cats_and_mice.models.simulation;

import java.util.List;

public class Utils {

    public static int getIdxOfSmallest(int[] arr, int size){
        int tmp = Integer.MAX_VALUE;
        int idx = 0;

        for(int i=0;i<size;i++) {
            if(tmp > Math.abs(arr[i])){
                tmp = arr[i];
                idx = i;
            }
        }

        return idx;
    }

    public static Integer[] getIdxOfSmallestPair(List<Integer[]> pairs){
        int tmp = Integer.MAX_VALUE;
        Integer[] ret = new Integer[2];
        for(Integer[] pair: pairs){
            if(tmp > Math.abs(pair[1])){
                tmp = pair[1];
                ret = pair;
            }
        }
        return ret;
    }

    public static int getIdxOfSmallestDistance(List<Double> distances) {
        double tmp = Double.MAX_VALUE;
        int idx = 0;
        for(int i=0;i<distances.size(); i++){
            if(tmp < distances.get(i)){
                tmp = distances.get(i);
                idx = i;
            }
        }
        return idx;
    }
}
