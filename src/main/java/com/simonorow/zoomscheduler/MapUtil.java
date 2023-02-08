package com.simonorow.zoomscheduler;

import java.util.*;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unSortedMap) {

     System.out.println("Unsorted Map : " + unSortedMap);

//LinkedHashMap preserve the ordering of elements in which they are inserted
        LinkedHashMap<K, V> sortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        return sortedMap;

    }
}