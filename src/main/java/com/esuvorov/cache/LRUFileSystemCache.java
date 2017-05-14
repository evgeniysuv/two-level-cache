package com.esuvorov.cache;

import com.esuvorov.util.SerializationUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by esuvorov on 5/13/17.
 */
public class LRUFileSystemCache<K, V extends Serializable> implements Cache<K, V> {
    private final static Logger LOGGER = Logger.getLogger(LRUFileSystemCache.class);

    private static final String CACHE_DIR = "cache";
    private static final int DEFAULT_MAX_CACHE_SIZE = 10;

    private Map<K, String> fileSystemCache;

    public LRUFileSystemCache(int maxCacheSize) {
        fileSystemCache = Collections.synchronizedMap(new LinkedHashMap<K, String>(maxCacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, String> eldest) {
                boolean isExceeded = size() > maxCacheSize;
                if (isExceeded) {
                    String fileDir = getFileDir(eldest.getValue());
                    removeFile(fileDir);
                }
                return isExceeded;
            }
        });
    }

    public LRUFileSystemCache() {
        this(DEFAULT_MAX_CACHE_SIZE);
    }

    @Override
    public void cache(K key, V value) {
        String id = getUUID();
        fileSystemCache.put(key, id);
        String fileDir = getFileDir(id);
        createCacheDirectory();
        SerializationUtil.serialize(value, fileDir);
        LOGGER.info("Object with key = " + key + " cached.");
    }

    private void createCacheDirectory() {
        File cacheDir = new File(CACHE_DIR);
        if (!cacheDir.exists()) {
            if (cacheDir.mkdirs()) {
                LOGGER.info("Directory " + CACHE_DIR + " created.");
            }
        }
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public V retrieve(K key) {
        String id = fileSystemCache.get(key);
        String fileDir = getFileDir(id);
        return SerializationUtil.deserialize(fileDir);
    }

    @Override
    public void remove(K key) {
        String id = fileSystemCache.remove(key);
        String fileDir = getFileDir(id);
        removeFile(fileDir);
    }

    private void removeFile(String fileDir) {
        File file = new File(fileDir);
        if (file.delete()) {
            LOGGER.info(file.getName() + " is deleted.");
        } else {
            LOGGER.error("Delete operation is failed.");
        }
    }

    @Override
    public void clear() {
        fileSystemCache.clear();
        try {
            FileUtils.deleteDirectory(new File(CACHE_DIR + File.separator));
        } catch (IOException e) {
            LOGGER.error("Failed to delete directory");
            e.printStackTrace();
        }
    }

    @Override
    public int size() {
        return fileSystemCache.size();
    }

    private String getFileDir(String id) {
        return CACHE_DIR + File.separator + id;
    }
}
