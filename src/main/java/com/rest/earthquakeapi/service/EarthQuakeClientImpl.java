package com.rest.earthquakeapi.service;
import com.rest.earthquakeapi.ParserManager.EarthQuakeParser;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code EarthQuakeClient} class provides methods to parse and process earthquake data.
 * It currently utilizes the {@code EarthQuakeParser} class to read earthquake data from a specified source,
 * such as a file or a URL, and stores the data as a list of {@code QuakeEntry} objects. Each {@code QuakeEntry}
 * represents an individual earthquake event with details like location, magnitude, depth, and additional information.
 * <p>
 * Main functionalities include:
 * - Filtering earthquake data based on magnitude, depth, and distance criteria.
 * - Printing details of earthquakes that match specific filtering criteria.
 * - Generating a CSV file from the earthquake data for analysis and reporting.
 * <p>
 * <p>
 * Usage examples:
 * - Creating an instance of {@code EarthQuakeClient} and filtering earthquakes by depth:
 * <p>
 * <p>

 */
@Service
public class EarthQuakeClientImpl implements EarthquakeDataProcessor, EarthQuakeDataExporter {
    private static final Logger logger = LoggerFactory.getLogger(EarthQuakeClientImpl.class);

    public EarthQuakeClientImpl(){

    }
    @Override
    public ArrayList<QuakeEntry> filterByMagnitude(ArrayList<QuakeEntry> quakeData,
                                                   double magMin) {
        if(quakeData == null){
            logger.error("Quake data is null.");
            throw new IllegalArgumentException("quakeData cannot be null");
        }
        if(magMin < 0){
            logger.error("Negative magnitude minimum: {}", magMin);
            throw new IllegalArgumentException("magMin must be non-negative");
        }
        ArrayList<QuakeEntry> filteredList = quakeData.stream()
                .filter(quakeEntry -> quakeEntry.getMagnitude() > magMin)
                .collect(Collectors.toCollection(ArrayList::new));
        logger.info("Filtered earthquakes by magnitude greater than {}. Number of quakes: {}", magMin, filteredList.size());
        return filteredList;
    }
    @Override
    public void dumpCSV(ArrayList<QuakeEntry> list) {
        System.out.println("Latitude,Longitude,Magnitude,Info");
        for (QuakeEntry qe : list) {
            System.out.printf("%4.2f,%4.2f,%4.2f,%s\n",
                    qe.getLocation().getLatitude(),
                    qe.getLocation().getLongitude(),
                    qe.getMagnitude(),
                    qe.getInfo());
        }
    }
    @Override
    public List<QuakeEntry> bigQuakes() {
        try {
            EarthQuakeParser parser = new EarthQuakeParser();
            String source = "data/nov20quakedatasmall.atom";
            ArrayList<QuakeEntry> list = parser.read(source);
            ArrayList<QuakeEntry> largeQuakes = filterByMagnitude(list, 5.0);

            // Logging earthquakes
            for (QuakeEntry qe : largeQuakes) {
                logger.info("Magnitude: {}, Location: {}, Info: {}",
                        qe.getMagnitude(),
                        qe.getLocation(),
                        qe.getInfo());
            }
            long count = largeQuakes.stream().count();
            logger.info("Number of earthquakes with magnitude greater than 5.0: {}", count);
            logger.info("Read data for {} quakes", list.size());
            return largeQuakes;
        } catch (Exception e) {
            logger.error("An error occurred while processing earthquake data.", e);
            throw new RuntimeException("Error processing earthquake data.", e);
        }
    }

    @Override
    public List<QuakeEntry> earthQuakesNearMe(double distMax, Location from) {
        EarthQuakeParser parser = new EarthQuakeParser();
//        String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list = parser.read(source);
        System.out.println("read data for " + list.size() + " quakes");

        // This location is Durham, NC
//        Location city = new Location(35.988, -78.907);

        // This location is Bridgeport, CA
        Location city = new Location(38.17, -118.82);

        List<QuakeEntry>closeEarthQuakes = filterByDistanceFrom(list, distMax, from);
        closeEarthQuakes.forEach(quakeEntry ->
                System.out.println((quakeEntry.getLocation().distanceTo(city) / 1000) + " " + quakeEntry.getInfo()));

        System.out.println("Found " + closeEarthQuakes.size() + " that match that criteria");

        return closeEarthQuakes;
    }

    /**
     * Prints details of all earthquakes from a specified data source whose depth is within a given range.
     * The method reads earthquake data, filters it based on the depth criteria, and prints each qualifying earthquake.
     * It also prints the total number of earthquakes found that match the depth criteria.
     */
    public List<QuakeEntry> quakesOfDepth(double minDepth, double maxDepth) {
        EarthQuakeParser parser = new EarthQuakeParser();
        //String source = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list = parser.read(source);

//        double minDepth = -10000.0;
//        double maxDepth = -5000.0;

        List<QuakeEntry> filtersDepth = filterByDepth(list, minDepth, maxDepth);

        System.out.println("Find quakes with depth between " + minDepth + " and" + maxDepth);

        for (QuakeEntry quakeEntry : filtersDepth) {
            System.out.println(quakeEntry);
        }
        System.out.println("Found " + filtersDepth.size() + " quakes that match that criteria\n");

        return filtersDepth;
    }
    public List<QuakeEntry> filterByDepth(ArrayList<QuakeEntry> quakeData,
                                          double minDepth, double maxDepth) {
        return quakeData.stream()
                .filter(quakeEntry -> quakeEntry.getDepth() > minDepth && quakeEntry.getDepth() < maxDepth)
                .collect(Collectors.toList());
    }
    /**
     * Filters and returns a list of earthquake entries (QuakeEntry) that are within a specified maximum distance
     * from a given location.
     *
     * @param quakeData An ArrayList of QuakeEntry objects, each representing an earthquake with its own location.
     @param distMax   The maximum distance (in kilometers) within which to find earthquakes from the specified location.
     It defines the radius of interest, indicating, "I'm only interested in earthquakes that are
     within this many kilometers from my specified location."
      * @param from      The reference Location object from which the distance to each earthquake is calculated.
     */
    public List<QuakeEntry> filterByDistanceFrom(ArrayList<QuakeEntry> quakeData,
                                                 double distMax, Location from) {
        return quakeData.stream()
                // Calculate the distance from 'from' to 'quakeEntry.getLocation()'
                .filter(quakeEntry -> from.distanceTo(quakeEntry.getLocation()) < distMax)
                .collect(Collectors.toCollection(ArrayList::new));
    }






}
