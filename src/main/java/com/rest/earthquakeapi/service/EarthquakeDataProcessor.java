package com.rest.earthquakeapi.service;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * EarthquakeDataProcessor interface defines methods
 * for processing and filtering earthquake data.
 */
public interface EarthquakeDataProcessor {

    ArrayList<QuakeEntry> filterByMagnitude(ArrayList<QuakeEntry> quakeData, double minMag);

    List<QuakeEntry> bigQuakes();


    //gets all the earthquake that are less than @disMax from Location @from
    List<QuakeEntry> earthQuakesNearMe(double distMax, Location from);

    List<QuakeEntry> quakesOfDepth(double minDepth, double maxDepth);

    List<QuakeEntry> findClosestEarthQuakes(Location current, int howMany);

    List<QuakeEntry> findEarthQuakesByPhrase(String phrase, String where);

    List<QuakeEntry> getFilteredQuakes(double minMagnitude, double maxMagnitude, double minDepth, double maxDepth);

    List<QuakeEntry> filterPossibleAllEarthquakeData(Double minMagnitude, Double maxMagnitude,
                                                     Double minDepth, Double maxDepth,
                                                     Location location, Double maxDistance, String phrase, String where);

    List<QuakeEntry> findLargestEarthQuakes(int howMany);

    List<String> getCountryNameFromEarthquakeData();


}
