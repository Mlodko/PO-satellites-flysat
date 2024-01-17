package ParentClasses;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Random;

public class Satellite {
    public Satellite() {
        names = new String[] {};
        orbital_position = 0f;
        satellite_position = 0f;
        norad = 0;
        declination = 0f;
        operator = "";
        lifespan = 0;
        date_of_arrival = null; // !!!
        launch_site = "";
        model_name = "";
        producer = "";
        transponders = new Transponder[] {};
    }
    public String[] names;
    public float orbital_position;
    public float satellite_position;
    public int norad;
    public float declination;
    public String operator;
    public int lifespan; // In years
    public Date date_of_arrival;
    public String launch_site;
    public String model_name;
    public String producer;
    public Transponder[] transponders;

    // This method merges different Satellite objects *describing the same satellite* (from different websites) into one
    // Call this only once you've coupled them up
    public static Satellite mergeSatellites(Satellite[] satellites) {
        if (satellites.length == 1) {
            return satellites[0];
        }

        Satellite mergedSatellite = new Satellite();

        for (Satellite satellite : satellites) {
            mergedSatellite = mergeTwoSatellites(mergedSatellite, satellite);
        }

        return mergedSatellite;
    }

    private static Satellite mergeTwoSatellites(Satellite one, Satellite other) {
        Satellite merged = new Satellite();
        merged.names = TypeMerger.merge(one.names, other.names);
        merged.orbital_position = TypeMerger.merge(one.orbital_position, other.orbital_position);
        merged.satellite_position = TypeMerger.merge(one.satellite_position, other.satellite_position);
        merged.norad = TypeMerger.merge(one.norad, other.norad);
        merged.declination = TypeMerger.merge(one.declination, other.declination);
        merged.operator = TypeMerger.merge(one.operator, other.operator);
        merged.lifespan = TypeMerger.merge(one.lifespan, other.lifespan);
        merged.date_of_arrival = TypeMerger.merge(one.date_of_arrival, other.date_of_arrival);
        merged.launch_site = TypeMerger.merge(one.launch_site, other.launch_site);
        merged.model_name = TypeMerger.merge(one.model_name, other.model_name);
        merged.producer = TypeMerger.merge(one.producer, other.producer);
        merged.transponders = Transponder.mergeAll(one.transponders, other.transponders);
        return merged;
    }
}
