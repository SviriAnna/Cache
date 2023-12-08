package org.example.removalstrategies;

import java.util.*;

// LRU - вытеснение самого давно использованного элемента

public class LRU<K> extends RemovalStrategy<K>{

    public LRU(int capacity) {
        super(capacity);
    }

    public void put(K key) {
        if(! getSet().contains(key)) {//key not present in cache
            if(getDq().size() == getCapacity()) {
                getSet().remove(getDq().removeLast());
            }
            getDq().addFirst(key);
            getSet().add(key);
        }else {//key present in cache
            //get key location
            if((getDq()).getFirst() != key) {
                Iterator<K> it = getDq().iterator();
                while(it.hasNext()) {
                    if(it.next() == key) {
                        it.remove();
                        break;
                    }
                }
                //dq.remove(index);//not removing that element from list - since this is DQ not array
                getDq().addFirst(key);
                //getSet().add(key);
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
