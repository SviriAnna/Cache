package org.example;

import java.io.IOException;

public interface Cache<K, V> {

    /** положить данные с присвоением ему ключа */
    void put(K key, V value) throws IOException;

    /** получить данные по ключу */
    V get(K key);

    /**
     * удалить данные по ключу
     */
    void remove(K key);

    /** очистить кэш */
    void clear() throws IOException;

    /** получить размер данного уровня кэша */
    int getSize();
}
