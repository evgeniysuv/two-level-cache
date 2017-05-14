package com.esuvorov.cache;

import com.esuvorov.strategy.StrategyType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by esuvorov on 5/14/17.
 */
public class CacheTest {
    private StrategyType strategyType;
    private Cache<String, String> cache;

    @Before
    public void init() throws IOException {
        Properties prop = new Properties();
        String propFileName = "cache.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
        String strategy = prop.getProperty("strategy");
        strategyType = StrategyType.valueOf(strategy);

        int size = Integer.parseInt(prop.getProperty("cache_size"));
        cache = initCache(size);
    }

    @Test
    public void testCache() throws Exception {
        int pairCount = 10;

        fillCache(cache, pairCount);

        assertCacheSize(cache, pairCount);
        assertValue(cache, "key1", "value1");
        assertCache();
        assertRetrieve();
        assertRemove();
        assertClear();
    }

    private void assertCacheSize(Cache<String, String> cache, int pairCount) {
        Assert.assertEquals(cache.size(), pairCount);
    }

    private void assertValue(Cache<String, String> cache, String key, String value) {
        Assert.assertEquals(cache.retrieve(key), value);
    }

    private void fillCache(Cache<String, String> cache, int pairCount) {
        for (int i = 1; i <= pairCount; i++) {
            cache.cache("key" + i, "value" + i);
        }
    }

    private Cache<String, String> initCache(int cacheSize) {
        switch (strategyType) {
            case MEMORY:
                return new LRUMemoryCache<>(cacheSize);
            case FILE_SYSTEM:
                return new LRUFileSystemCache<>(cacheSize);
            default:
                return new TwoLevelCache<>(cacheSize);
        }
    }

    private void assertCache() throws Exception {
        cache.cache("key100", "value100");
        Assert.assertEquals("value100", cache.retrieve("key100"));
    }

    private void assertRetrieve() throws Exception {
        Assert.assertEquals("value3", cache.retrieve("key3"));
    }

    private void assertRemove() throws Exception {
        cache.remove("key5");
        Assert.assertEquals(null, cache.retrieve("key5"));
    }

    private void assertClear() throws Exception {
        cache.clear();
        Assert.assertEquals(0, cache.size());
    }
}
