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



}
