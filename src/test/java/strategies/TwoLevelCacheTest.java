package strategies;

import org.example.TwoLevelCache;
import org.example.removalstrategies.LRU;
import org.example.removalstrategies.MRU;
import org.example.StrategyType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class TwoLevelCacheTest {
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    private TwoLevelCache<Integer, String> twoLevelCache;

    @Before
    public void init() throws IOException {
        twoLevelCache = new TwoLevelCache.Builder(StrategyType.LRU)
                .setMemoryCapacity(1)
                .setDiskCapacity(1)
                .build();
    }

    @After
    public void clearCache() throws IOException {
        twoLevelCache.clear();
    }

    @Test
    public void shouldPutGetAndRemoveObjectTest() throws IOException {
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(1, twoLevelCache.getSize());

        twoLevelCache.remove(0);
        assertNull(twoLevelCache.get(0));
    }

    @Test
    public void shouldRemoveObjectFromFirstLevelTest() throws IOException {
        twoLevelCache.put(0, VALUE1);
        twoLevelCache.put(1, VALUE2);

        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertEquals(VALUE2, twoLevelCache.getSecondCacheLevel().get(1));

        twoLevelCache.remove(0);

        assertNull(twoLevelCache.getFirstCacheLevel().get(0));
        assertEquals(VALUE2, twoLevelCache.getSecondCacheLevel().get(1));
    }

    @Test
    public void shouldRemoveObjectFromSecondLevelTest() throws IOException {
        twoLevelCache.put(0, VALUE1);
        twoLevelCache.put(1, VALUE2);

        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertEquals(VALUE2, twoLevelCache.getSecondCacheLevel().get(1));

        twoLevelCache.remove(1);

        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertNull(twoLevelCache.getSecondCacheLevel().get(1));
    }

    @Test
    public void shouldNotGetObjectFromCacheIfNotExistsTest() throws IOException {
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertNull(twoLevelCache.get(111));
    }

    @Test
    public void shouldRemoveDuplicatedObjectFromSecondLevelWhenFirstLevelHasEmptyPlaceTest() throws IOException {
        assertTrue(twoLevelCache.getFirstCacheLevel().getSize()<twoLevelCache.getFirstCacheLevel().getCapacity());

        twoLevelCache.getSecondCacheLevel().put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.getSecondCacheLevel().get(0));

        twoLevelCache.put(0, VALUE1);

        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertFalse(twoLevelCache.getSecondCacheLevel().getCache().containsKey(0));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenFirstLevelHasEmptyPlaceTest() throws IOException {
        assertTrue(twoLevelCache.getFirstCacheLevel().getSize()<twoLevelCache.getFirstCacheLevel().getCapacity());
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertFalse(twoLevelCache.getSecondCacheLevel().getCache().containsKey(0));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectExistsInFirstLevelCacheTest() throws IOException {
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertEquals(1, twoLevelCache.getFirstCacheLevel().getSize());

        // положить тот же ключ с другим значением
        twoLevelCache.put(0, VALUE2);

        assertEquals(VALUE2, twoLevelCache.get(0));
        assertEquals(VALUE2, twoLevelCache.getFirstCacheLevel().get(0));
        assertEquals(1, twoLevelCache.getFirstCacheLevel().getSize());
    }

    @Test
    public void shouldPutObjectIntoCacheWhenSecondLevelHasEmptyPlaceTest() throws IOException {
        IntStream.range(0, 1).forEach(i -> {
            try {
                twoLevelCache.put(i, "String " + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertFalse(twoLevelCache.getFirstCacheLevel().getSize()<twoLevelCache.getFirstCacheLevel().getCapacity());
        assertTrue(twoLevelCache.getSecondCacheLevel().getSize()<twoLevelCache.getSecondCacheLevel().getCapacity());

        twoLevelCache.put(2, VALUE2);

        assertEquals(VALUE2, twoLevelCache.get(2));
        assertEquals(VALUE2, twoLevelCache.getSecondCacheLevel().get(2));
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectExistsInSecondLevelTest() throws IOException {
        IntStream.range(0, 1).forEach(i -> {
            try {
                twoLevelCache.put(i, "String " + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertFalse(twoLevelCache.getFirstCacheLevel().getSize()<twoLevelCache.getFirstCacheLevel().getCapacity());

        twoLevelCache.put(2, VALUE2);

        assertEquals(VALUE2, twoLevelCache.get(2));
        assertEquals(VALUE2, twoLevelCache.getSecondCacheLevel().get(2));
        assertEquals(1, twoLevelCache.getSecondCacheLevel().getSize());

        // вставка того же ключа с другим значением
        twoLevelCache.put(2, VALUE3);

        assertEquals(VALUE3, twoLevelCache.get(2));
        assertEquals(VALUE3, twoLevelCache.getSecondCacheLevel().get(2));
        assertEquals(1, twoLevelCache.getSecondCacheLevel().getSize());
    }

    @Test
    public void shouldPutObjectIntoCacheWhenObjectShouldBeReplacedTest() throws IOException {
        IntStream.range(0, 2).forEach(i -> {
            try {
                twoLevelCache.put(i, "String " + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        assertFalse((twoLevelCache.getFirstCacheLevel().getSize()<twoLevelCache.getFirstCacheLevel().getCapacity() || twoLevelCache.getSecondCacheLevel().getSize()<twoLevelCache.getSecondCacheLevel().getCapacity()));
        assertFalse(twoLevelCache.getRemovalStrategy().getDq().contains(3));

        twoLevelCache.put(3, VALUE3);

        assertEquals(twoLevelCache.get(3), VALUE3);
        assertTrue(twoLevelCache.getRemovalStrategy().getDq().contains(3));
        assertTrue(twoLevelCache.getFirstCacheLevel().getCache().containsKey(3));
        assertFalse(twoLevelCache.getSecondCacheLevel().getCache().containsKey(3));
    }

    @Test
    public void shouldGetCacheSizeTest() throws IOException {
        twoLevelCache.put(0, VALUE1);
        assertEquals(1, twoLevelCache.getSize());

        twoLevelCache.put(1, VALUE2);
        assertEquals(2, twoLevelCache.getSize());
    }

    @Test
    public void isObjectPresentTest() throws IOException {
        assertFalse(twoLevelCache.get(0)!=null);

        twoLevelCache.put(0, VALUE1);
        assertTrue(twoLevelCache.get(0)!=null);
    }

    @Test
    public void isEmptyPlaceTest() throws IOException {
        assertFalse(twoLevelCache.get(0)!=null);
        twoLevelCache.put(0, VALUE1);
        assertTrue((twoLevelCache.getFirstCacheLevel().getSize()<twoLevelCache.getFirstCacheLevel().getCapacity() || twoLevelCache.getSecondCacheLevel().getSize()<twoLevelCache.getSecondCacheLevel().getCapacity()));

        twoLevelCache.put(1, VALUE2);
        assertFalse((twoLevelCache.getFirstCacheLevel().getSize()<twoLevelCache.getFirstCacheLevel().getCapacity() || twoLevelCache.getSecondCacheLevel().getSize()<twoLevelCache.getSecondCacheLevel().getCapacity()));
    }

    @Test
    public void shouldClearCacheTest() throws IOException {
        twoLevelCache.put(0, VALUE1);
        twoLevelCache.put(1, VALUE2);

        assertEquals(2, twoLevelCache.getSize());
        assertTrue(twoLevelCache.getRemovalStrategy().getDq().contains(0));
        assertTrue(twoLevelCache.getRemovalStrategy().getDq().contains(1));

        twoLevelCache.clear();

        assertEquals(0, twoLevelCache.getSize());
        assertFalse(twoLevelCache.getRemovalStrategy().getDq().contains(0));
        assertFalse(twoLevelCache.getRemovalStrategy().getDq().contains(1));
    }

    @Test
    public void shouldUseLRUStrategyTest() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1, new LRU<>(999));
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertFalse(twoLevelCache.getSecondCacheLevel().get(0)!=null);
    }

    @Test
    public void shouldUseMRUStrategyTest() throws IOException {
        twoLevelCache = new TwoLevelCache<>(1, 1, new MRU<>(999));
        twoLevelCache.put(0, VALUE1);
        assertEquals(VALUE1, twoLevelCache.get(0));
        assertEquals(VALUE1, twoLevelCache.getFirstCacheLevel().get(0));
        assertFalse(twoLevelCache.getSecondCacheLevel().get(0)!=null);
    }
}