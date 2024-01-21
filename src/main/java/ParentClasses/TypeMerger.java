package ParentClasses;

import java.util.Date;
import java.util.Random;

public abstract class TypeMerger {
    /*
        Basic merging rules:
        -   If one instance has data and the other doesn't, we take the first
        -   If one instance has a longer array of data (like more names) than the second we try to merge the two arrays
            into one, while removing duplicates
        -   If the data from two satellites contradicts, we choose at random

        Be free to add/change these rules, just make sure it's well-thought-out and that your implementation works
        as intended
    */
    private static final Random random = new Random();
    public static int merge(int one, int other) {
        // SR
        if(one == other || other == 0) {
            return one;
        }
        else {
            return other;
        }
    }
    
    public static float merge(float one, float other) {
        if(one == other) {
            return one; // Why not, they're the same anyway
        }
        else if(other == 0f) {
            return one;
        }
        else {
            return other;
        }
    }
    
    public static String[] merge(String[] one, String[] other) {
        if(one.length > other.length) {
            // TODO merge arrays, don't return the longer one
            return one;
        }
        else if(other.length > one.length) {
            return other;
        }
        else {
            int chosen = random.nextInt(2); // Either 0 or 1
            switch (chosen) {
                case 0 -> {
                    return one;
                }
                case 1 -> {
                    return other;
                }
                default -> {
                    return one;
                }
            }
        }
    }

    public static String merge(String one, String other) {
        if(one.equals(other) || other.isEmpty()) {
            return one;
        }
        else {
            return other;
        }
    }

    public static Polarization merge(Polarization one, Polarization other) {
        if(one == other) {
            int chosen = random.nextInt(2); // Either 0 or 1
            switch (chosen) {
                case 0 -> {
                    return one;
                }
                case 1 -> {
                    return other;
                }
            }
        }
        else if(other == Polarization.NULL) {
            return one;
        }
        else {
            return other;
        }
        return one; // It shouldn't ever reach this, I only added this so the compiler is happy
    }

    public static Date merge(Date one, Date other) {
        if(one.equals(other)) {
            int chosen = random.nextInt(2); // Either 0 or 1
            switch (chosen) {
                case 0 -> {
                    return one;
                }
                case 1 -> {
                    return other;
                }
            }
        }
        else if(other == null) {
            return one;
        }
        else {
            return other;
        }
        return one; // It shouldn't ever reach this, I only added this so the compiler is happy
    }




}
