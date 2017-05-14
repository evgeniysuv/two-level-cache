package com.esuvorov.cache;

/**
 * Created by esuvorov on 5/12/17.
 */
public interface Cache<K, V> {
    void cache(K key, V value);

    V retrieve(K key);

    void remove(K key);

    void clear();

    int size();
}
