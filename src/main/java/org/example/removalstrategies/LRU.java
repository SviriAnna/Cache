package org.example.removalstrategies;

import java.util.*;

// LRU - вытеснение самого давно использованного элемента

public class LRU<K> extends RemovalStrategy<K>{

    public LRU(int capacity) {
        super(capacity);
    }

    public void put(K key) {
        if(! getSet().contains(key)) {
            if(getDq().size() == getCapacity()) {
                getSet().remove(getDq().removeLast());
            }
            getDq().addFirst(key);
            getSet().add(key);
        }else {
            if(getDq().getFirst() != key) {
                getDq().remove(key);
                getDq().addFirst(key);
            }
        }
    }

    @Override
    public void print() {
        Iterator<K> it = getDq().iterator();
        System.out.print("LRU cache : ");
        while(it.hasNext()) {
            System.out.print(it.next()+" ");
        }
        System.out.println();
    }

}
