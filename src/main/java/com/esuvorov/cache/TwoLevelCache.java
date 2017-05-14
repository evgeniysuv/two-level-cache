package com.esuvorov.cache;

import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * Created by esuvorov on 5/14/17.
 */
@AllArgsConstructor
public class TwoLevelCache<K, V extends Serializable> implements Cache<K, V> {
    private Cache<K, V> memoryCache;
    private Cache<K, V> fileSystemCache;

    public TwoLevelCache() {
        memoryCache = new LRUMemoryCache<>();
        fileSystemCache = new LRUFileSystemCache<>();
    }

    public TwoLevelCache(int cacheSize) {
        memoryCache = new LRUMemoryCache<>(cacheSize);
        fileSystemCache = new LRUFileSystemCache<>(cacheSize);
    }

    @Override
    public void cache(K key, V value) {
        memoryCache.cache(key, value);
        fileSystemCache.cache(key, value);
    }

    @Override
    public V retrieve(K key) {
        V value = memoryCache.retrieve(key);
        if (value != null) {
            return value;
        }
        return fileSystemCache.retrieve(key);
    }

    @Override
    public void remove(K key) {
        memoryCache.remove(key);
        fileSystemCache.remove(key);
    }

    @Override
    public void clear() {
        memoryCache.clear();
        fileSystemCache.clear();
    }

    @Override
    public int size() {
        if (memoryCache.size() >= fileSystemCache.size()) {
            return memoryCache.size();
        }
        return fileSystemCache.size();
    }
}
