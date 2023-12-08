package org.example.removalstrategies;

// MRU - вытеснение последнего использованного элемента

import java.util.Iterator;

public class MRU<K> extends RemovalStrategy<K>{

    public MRU(int capacity) {
        super(capacity);
    }

    public void put(K key) {
        if(! getSet().contains(key)) {
            if(getDq().size() == getCapacity()) {
                getSet().remove(getDq().removeFirst());
            }
            getDq().addFirst(key);
            getSet().add(key);
        } else {
            if(getDq().getFirst() != key) {
                getDq().remove(key);
                getDq().addFirst(key);
            }
        }
    }

    public void print() {
        Iterator<K> it = getDq().iterator();
        System.out.print("MRU cache : ");
        while(it.hasNext()) {
            System.out.print(it.next()+" ");
        }
        System.out.println();
    }

}
