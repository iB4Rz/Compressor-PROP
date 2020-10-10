package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary {

    // Maximum size of the dictionary
    private int maxSize;

    private Map <ByteArray, Integer> m;
    private List <ByteArray> l; 

    /**
     * Constructor that creates a hasmap and an array list and 
     * defines the maximum size of the dictionary.
     * @param maxSize the maximum size of the dictionary.
     */
    public Dictionary (int maxSize) {
        this.maxSize = maxSize;
        m = new HashMap<>();
        l = new ArrayList<>();
    }

    /**
     * Adds an element into the dictionary.
     * @param s ByteArray to add to the dictionary.
     */
    public void add (ByteArray s) {
        if (size() < maxSize) {
            m.put(s, Integer.valueOf(l.size()));
            l.add(s);
        }
    }

    /**
     * Gets the number for the given byte array.
     * @param s the byte array to deal with to get his number.
     * @return an Integer for the given byte array, if the byte array does not exist, retun -1.
     */
    public int getNumStr (ByteArray s) {
        return (m.containsKey(s) ? 
                ((Integer)m.get(s)).intValue() : -1);
    }

    /**
     * Gets the byte array for the given number.
     * @param n the number to deal with to get his byte array.
     * @return the ByteArray for the given number, if the number does not exist, return null.
     */
    public ByteArray getStrNum (int n) {
        return (n < l.size() ? 
                (ByteArray) l.get(n) : null);
    }

    /**
     * Returns the size of the byte array list.
     * @return the size of the byte array list.
     */
    public int size () {
        return l.size();
    }
}