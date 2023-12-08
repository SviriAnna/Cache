package org.example;

import org.example.removalstrategies.*;

import java.io.IOException;
import java.io.Serializable;

public class TwoLevelCache<K extends Serializable, V extends Serializable> implements Cache<K, V>{

    private final MemoryCache<K , V> firstCacheLevel;
    private final DiskCache<K, V> secondCacheLevel;
    private final RemovalStrategy<K> removalStrategy;

    public TwoLevelCache(final int memoryCapacity, final int diskCapacity, RemovalStrategy<K> removalStrategy) throws IOException {
        this.firstCacheLevel = new MemoryCache<>(memoryCapacity);
        this.secondCacheLevel = new DiskCache<>(diskCapacity);
        this.removalStrategy = removalStrategy;
    }

    public TwoLevelCache(final int memoryCapacity, final int diskCapacity) throws IOException {
        this.firstCacheLevel = new MemoryCache<>(memoryCapacity);
        this.secondCacheLevel = new DiskCache<>(diskCapacity);
        this.removalStrategy = new LRU<>(memoryCapacity+diskCapacity);
    }

    @Override
    public void put(K key, V value) throws IOException {
        if(firstCacheLevel.getCache().containsKey(key) || (firstCacheLevel.getSize()<firstCacheLevel.getCapacity())){
            firstCacheLevel.put(key, value);
            if (secondCacheLevel.getCache().containsKey(key)) {
                secondCacheLevel.remove(key);
            }
        }
        else if (secondCacheLevel.getCache().containsKey(key) || (secondCacheLevel.getSize()<firstCacheLevel.getCapacity())) {
            secondCacheLevel.put(key, value);
        }
        else {
            replaceInfo(key, value);
        }
        removalStrategy.put(key);
    }

    @Override
    public V get(K key) {
        if(firstCacheLevel.getCache().containsKey(key)){
            removalStrategy.put(key);
            return firstCacheLevel.get(key);
        } else if (secondCacheLevel.getCache().containsKey(key)) {
            removalStrategy.put(key);
            return secondCacheLevel.get(key);
        } else {
            return null;
        }
    }

    @Override
    public void remove(K key) {
        if(firstCacheLevel.getCache().containsKey(key)){
            firstCacheLevel.remove(key);
        }
        if (secondCacheLevel.getCache().containsKey(key)){
            secondCacheLevel.remove(key);
        }
        removalStrategy.getDq().remove(key);
    }

    @Override
    public void clear() throws IOException {
        firstCacheLevel.clear();
        secondCacheLevel.clear();
        removalStrategy.getDq().clear();
    }

    @Override
    public int getSize() {
        return firstCacheLevel.getSize()+secondCacheLevel.getSize();
    }

    private void replaceInfo(K newKey, V newValue) throws IOException {
        K replaceKey = null;
        if(removalStrategy.getClass().equals(LRU.class) || removalStrategy.getClass().equals(LFU.class)){
            replaceKey = removalStrategy.getDq().getLast();
        } else if (removalStrategy.getClass().equals(MRU.class)) {
            replaceKey = removalStrategy.getDq().getFirst();
        }

        if(firstCacheLevel.getCache().containsKey(replaceKey)){
            firstCacheLevel.remove(replaceKey);
            firstCacheLevel.put(newKey, newValue);
        } else if (secondCacheLevel.getCache().containsKey(replaceKey)) {
            secondCacheLevel.remove(replaceKey);
            secondCacheLevel.put(newKey, newValue);
        }
        removalStrategy.getDq().remove(replaceKey);
        removalStrategy.put(newKey);
    }

    public MemoryCache<K, V> getFirstCacheLevel() {
        return firstCacheLevel;
    }

    public RemovalStrategy<K> getRemovalStrategy() {
        return removalStrategy;
    }

    public DiskCache<K, V> getSecondCacheLevel() {
        return secondCacheLevel;
    }

    public static final class Builder<K extends Serializable, V extends Serializable> {
        private int memoryCapacity = 20;
        private int diskCapacity = 20;
        private final StrategyType strategyType;

        public Builder(StrategyType strategyType) {
            this.strategyType = strategyType;
        }

        public Builder setMemoryCapacity(int memoryCapacity) {
            this.memoryCapacity = memoryCapacity;
            return this;
        }

        public Builder setDiskCapacity(int diskCapacity) {
            this.diskCapacity = diskCapacity;
            return this;
        }

        public TwoLevelCache<K, V> build() throws IOException {
            return new TwoLevelCache<K, V>(memoryCapacity, diskCapacity, getStrategy(strategyType));
        }

        private RemovalStrategy<K> getStrategy(StrategyType strategyType) {
            int capacity = memoryCapacity+diskCapacity;
            switch (strategyType) {
                case LRU:
                    return new LRU<>(capacity);
                case MRU:
                    return new MRU<>(capacity);
                case LFU:
                default:
                    return new LFU<>(capacity);
            }
        }
    }
}
