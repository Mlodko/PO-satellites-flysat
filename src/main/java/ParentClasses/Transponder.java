package ParentClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Transponder {


    public Transponder() {
        names = new String[] {};
        frequency = 0f;
        polarization = POLARIZATION.NULL;
        standard = "";
        encoding = "";
        sr = 0;
        fec = "";
    }

    public String[] names; // (Like "2C")
    public float frequency;
    public POLARIZATION polarization;
    public String standard; // (Like "DVB-S2")
    public String encoding; // (Like "16APSK")
    public int sr;
    public String fec; // Should it be string? Some of them are like "3/4" ad some are just "Auto"


    // This returns a complete, merged array of Transponders to be added to the Satellite object.
    // Arguments are arrays of Transponders for *the same satellite* taken from two different websites
    public static Transponder[] mergeAll(Transponder[] one, Transponder[] other) {
        Transponder[][] pairs = findPairs(one, other);
        return mergeEachPair(pairs);
    }


    private static Transponder[][] findPairs(Transponder[] one, Transponder[] other) {
        /* TODO Given two arrays of Transponders find pairs that correspond to each other -
                that describe the same Transponder
        I have no clue how to even start with this, maybe comparing names?
        The final result should look like:
        {
        {Transponder1_flysat, Transponder1_kingofsat},
        {Transponder2_flysat, Transponder2_kingofsat},
        ...
        }
         */
        return new Transponder[][] {}; // FIXME delete this after implementing this method
    }


    private static Transponder[] mergeEachPair(Transponder[][] pairs) {
        ArrayList<Transponder> mergedList = new ArrayList<>();
        for(Transponder[] pair : pairs) {
            if(pair.length != 2) {
                throw new RuntimeException("You tried to merge a pair when it's length is " + pair.length);
            }
            mergedList.add(mergeTwo(pair[0], pair[1]));
        }
        return mergedList.toArray(Transponder[]::new);
    }


    private static Transponder mergeTwo(Transponder one, Transponder other) {
        Transponder merged = new Transponder();
        merged.names = TypeMerger.merge(one.names, other.names);
        merged.frequency = TypeMerger.merge(one.frequency, other.frequency);
        merged.polarization = TypeMerger.merge(one.polarization, other.polarization);
        merged.standard = TypeMerger.merge(one.standard, other.standard);
        merged.encoding = TypeMerger.merge(one.encoding, other.encoding);
        merged.sr = TypeMerger.merge(one.sr, other.sr);
        merged.fec = TypeMerger.merge(one.fec, other.fec);
        return merged;
    }
}
