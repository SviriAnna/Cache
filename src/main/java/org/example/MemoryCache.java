package org.example;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache<K extends Serializable, V extends Serializable> implements Cache<K,V>{

    private final Map<K, V> cache;

    private final int capacity;

    public MemoryCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<K, V>(capacity);
    }

    public Map<K, V> getCache() {
        return cache;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int getSize() {
        return cache.size();
    }
}
