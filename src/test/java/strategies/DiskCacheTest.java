package strategies;

import org.example.DiskCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class DiskCacheTest {
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    private DiskCache<Integer, String> diskCache;

    @Before
    public void init() throws IOException {
        diskCache = new DiskCache<>();
    }

    @After
    public void clearCache() throws IOException {
        diskCache.clear();
    }

    @Test
    public void shouldPutGetAndRemoveObjectTest() throws IOException {
        diskCache.put(0, VALUE1);
        assertEquals(VALUE1, diskCache.get(0));
        assertEquals(1, diskCache.getSize());

        diskCache.remove(0);
        assertNull(diskCache.get(0));
    }

    @Test
    public void shouldNotGetObjectFromCacheIfNotExistsTest() throws IOException {
        diskCache.put(0, VALUE1);
        assertEquals(VALUE1, diskCache.get(0));
        assertNull(diskCache.get(111));
    }

    @Test
    public void shouldNotRemoveObjectFromCacheIfNotExistsTest() throws IOException {
        diskCache.put(0, VALUE1);
        assertEquals(VALUE1, diskCache.get(0));
        assertEquals(1, diskCache.getSize());

        diskCache.remove(5);
        assertEquals(VALUE1, diskCache.get(0));
    }

    @Test
    public void shouldGetCacheSizeTest() throws IOException {
        diskCache.put(0, VALUE1);
        assertEquals(1, diskCache.getSize());

        diskCache.put(1, VALUE2);
        assertEquals(2, diskCache.getSize());
    }

    @Test
    public void isObjectPresentTest() throws IOException {
        assertFalse(diskCache.getCache().containsKey(0));

        diskCache.put(0, VALUE1);
        assertTrue(diskCache.getCache().containsKey(0));
    }

    @Test
    public void isEmptyPlaceTest() throws IOException {
        diskCache = new DiskCache<>(5);

        IntStream.range(0, 4).forEach(i -> {
            try {
                diskCache.put(i, "String " + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(diskCache.getCapacity()>diskCache.getSize());
        diskCache.put(5, "String");
        assertFalse(diskCache.getCapacity()>diskCache.getSize());
    }

    @Test
    public void shouldClearCacheTest() throws IOException {
        IntStream.range(0, 3).forEach(i -> {
            try {
                diskCache.put(i, "String " + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(3, diskCache.getSize());
        diskCache.clear();
        assertEquals(0, diskCache.getSize());
    }
}
