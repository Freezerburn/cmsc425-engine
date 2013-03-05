package stuff;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: freezerburn
 * Date: 1/13/13
 * Time: 7:26 PM
 */
public class Preferences {
    public static final String CMDLINE_FILENAME = "game.prefs.file";
    protected static final String defaultFilename = "res/preferences.ser";

    // We assign empty HashMaps here due to the possibility of a .ser file not existing.
    protected static Map<String, Integer> intMap = new HashMap<String, Integer>();
    protected static Map<String, Float> floatMap = new HashMap<String, Float>();
    protected static Map<String, String> stringMap = new HashMap<String, String>();
    protected static Map<String, Serializable> objectMap = new HashMap<String, Serializable>();

    public static int getInt(String key, int defaultValue) {
        if(intMap.containsKey(key)) {
            return intMap.get(key);
        }
        return defaultValue;
    }

    public static float getFloat(String key, float defaultValue) {
        if(floatMap.containsKey(key)) {
            return floatMap.get(key);
        }
        return defaultValue;
    }

    public static String getString(String key, String defaultValue) {
        if(stringMap.containsKey(key)) {
            return stringMap.get(key);
        }
        return defaultValue;
    }

    public static Object getObject(String key, Object defaultValue) {
        if(objectMap.containsKey(key)) {
            return objectMap.get(key);
        }
        return defaultValue;
    }

    public static void setInt(String key, int value) {
        intMap.put(key, value);
    }

    public static void setFloat(String key, float value) {
        floatMap.put(key, value);
    }

    public static void setString(String key, String value) {
        stringMap.put(key, value);
    }

    public static void setObject(String key, Serializable value) {
        objectMap.put(key, value);
    }

    public static void saveAll(String filename) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(intMap);
            out.writeObject(floatMap);
            out.writeObject(stringMap);
            out.writeObject(objectMap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveAll() {
        saveAll(defaultFilename);
    }

    @SuppressWarnings("unchecked")
    public static void restoreAll(String filename) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filename));
            HashMap<String, Integer> tempInt = (HashMap<String, Integer>) in.readObject();
            HashMap<String, Float> tempFloat = (HashMap<String, Float>) in.readObject();
            HashMap<String, String> tempString = (HashMap<String, String>) in.readObject();
            HashMap<String, Serializable> tempObject = (HashMap<String, Serializable>) in.readObject();
            if(tempInt != null) {
                intMap = tempInt;
            }
            if(tempFloat != null) {
                floatMap = tempFloat;
            }
            if(tempString != null) {
                stringMap = tempString;
            }
            if(tempObject != null) {
                objectMap = tempObject;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void restoreAll() {
        restoreAll(defaultFilename);
    }
}
