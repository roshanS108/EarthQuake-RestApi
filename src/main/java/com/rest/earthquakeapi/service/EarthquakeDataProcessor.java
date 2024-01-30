package com.rest.earthquakeapi.service;

import com.rest.earthquakeapi.model.QuakeEntry;

import java.util.ArrayList;

/**
 * IEarthquakeDataProcessor interface defines methods
 * for processing and filtering earthquake data.
 */
public interface EarthquakeDataProcessor {

    ArrayList<QuakeEntry> filterByMagnitude(ArrayList<QuakeEntry> quakeData, double minMag, double maxMag);

}
