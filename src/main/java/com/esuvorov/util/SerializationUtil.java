package com.esuvorov.util;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by esuvorov on 5/13/17.
 */
public class SerializationUtil {
    private final static Logger LOGGER = Logger.getLogger(SerializationUtil.class);

    public static <V> void serialize(V value, String fileDir) {
        try (FileOutputStream fos = new FileOutputStream(fileDir);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V deserialize(String fileDir) {
        V obj;

        try (FileInputStream fis = new FileInputStream(fileDir);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            obj = (V) ois.readObject();

        } catch (ClassNotFoundException | IOException ex) {
            LOGGER.error("Deserialize file to " + fileDir + " failed");
            return null;
        }
        return obj;
    }
}
