package org.example;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;

public class DiskCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {
    private final Map<K, V> cache;
    private final Path tempDir;
    private int capacity;

    public DiskCache() throws IOException {
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
        this.cache = new LinkedHashMap<>();
        tempDir.toAbsolutePath();
    }

    public DiskCache(int capacity) throws IOException {
        this.tempDir = Files.createTempDirectory("cache");
        this.tempDir.toFile().deleteOnExit();
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity);
    }

    @Override
    public V get(K key) {
        if (cache.containsKey(key)) {
            V fileName = cache.get(key);
            try (FileInputStream fileInputStream = new FileInputStream(tempDir + File.separator + fileName);
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (V) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                System.out.println((format("Невозможно прочитать файл. %s: %s", fileName, e.getMessage())));
            }
        }
        System.out.println((format("Объекта с ключом '%s' не существует в памяти на диске", key)));
        return null;
    }

    @Override
    public void put(K key, V value) throws IOException {
        File tmpFile = Files.createTempFile(tempDir, "", "").toFile();

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tmpFile))) {
            outputStream.writeObject(value);
            outputStream.flush();
            cache.put(key, (V) tmpFile.getName());
        } catch (IOException e) {
            System.out.println(("Невозможно записать объект на диск " + tmpFile.getName() + ": " + e.getMessage()));
        }
    }

    @Override
    public void remove(K key) {
        V fileName = cache.get(key);
        File deletedFile = new File(tempDir + File.separator + fileName);
        if (deletedFile.delete()) {
            System.out.println((format("Файл '%s' был удален из памяти на диске", fileName)));
        } else {
            System.out.println((format("Невозможно удалить файл %s из памяти на диске", fileName)));
        }
        cache.remove(key);
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    public Map<K, V> getCache() {
        return cache;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void clear() throws IOException {
        try (Stream<Path> pathStream = Files.walk(tempDir)) {
            pathStream
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (file.delete()) {
                            System.out.println((format("Файл '%s' был удален из памяти на диске", file)));
                        } else {
                            System.out.println((format("Невозможно удалить файл %s из памяти на диске", file)));
                        }
                    });
            cache.clear();
        }
    }
}