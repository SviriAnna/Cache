package org.example.removalstrategies;

// LFU - вытеснение наименее часто используемого элемента

import java.util.*;

public class LFU<K> extends RemovalStrategy<K>{

    private Map<K, Integer> map = new TreeMap<>();

    private List list;

    public LFU(int capacity) {
        super(capacity);
    }

    @Override
    public void put(K key) {
        map.merge(key, 1, Integer::sum);
        list = map.entrySet().stream()
                .sorted(Map.Entry.<K, Integer>comparingByValue().reversed())
                .limit(getCapacity())
                .toList();
        for (Object o : list) {
            getDq().add((K) o);
        }
    }

    @Override
    public void print() {
        System.out.println("LFU cache : ");
        System.out.println(list);
    }

}
