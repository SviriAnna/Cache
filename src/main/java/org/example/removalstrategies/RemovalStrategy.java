package org.example.removalstrategies;

import java.util.*;

public abstract class RemovalStrategy<K> {

    private final Deque<K> dq ;//очередь из ключей
    private final HashSet<K> set;//для отслеживания ключа в dq
    private final int capacity;

    public RemovalStrategy(int capacity) {
        this.dq = new LinkedList<>();
        this.set = new HashSet<>();
        this.capacity = capacity;
    }

    public abstract void put(K key);

    public Deque<K> getDq() {
        return dq;
    }

    public HashSet<K> getSet() {
        return set;
    }

    public int getCapacity() {
        return capacity;
    }

    public abstract void print();
}
