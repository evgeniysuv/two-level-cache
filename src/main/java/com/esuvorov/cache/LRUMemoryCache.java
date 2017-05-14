package com.esuvorov.cache;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by esuvorov on 5/13/17.
 */
public class LRUMemoryCache<K, V> implements Cache<K, V> {
    private final static Logger LOGGER = Logger.getLogger(LRUMemoryCache.class);

    private final static int DEFAULT_MAX_CACHE_SIZE = 10;
    private Map<K, V> lruCache;

    public LRUMemoryCache(int maxCacheSize) {
        lruCache = Collections.synchronizedMap(new LinkedHashMap<K, V>(maxCacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxCacheSize;
            }
        });
    }

    public LRUMemoryCache() {
        this(DEFAULT_MAX_CACHE_SIZE);
    }

    @Override
    public void cache(K key, V value) {
        lruCache.put(key, value);
        LOGGER.info("Object with key = " + key + " cached.");
    }

    @Override
    public V retrieve(K key) {
        return lruCache.get(key);
    }

    @Override
    public void remove(K key) {
        lruCache.remove(key);
    }

    @Override
    public void clear() {
        lruCache.clear();
    }

    @Override
    public int size() {
        return lruCache.size();
    }
}