package com.rest.earthquakeapi.service;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.csv.EarthQuakeParser;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.springframework.stereotype.Service;

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
 * <pre>{@code
 *   EarthQuakeClient client = new EarthQuakeClient();
 *   client.quakesOfDepth();
 *   }</pre>
 * <p>
 * - Generating a CSV file of earthquake data:
 * <pre>{@code
 *   EarthQuakeClient client = new EarthQuakeClient();
 *   client.createCSV();
 *   }</pre>
 * <p>

 */
@Service
public class EarthQuakeClient {

    @Value("classpath:/data/nov20quakedatasmall.atom")
    private Resource atomFileResource;
    private static final Logger logger = LoggerFactory.getLogger(EarthQuakeClient.class);

    public EarthQuakeClient() {
    }

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

    /**
     * Filters and returns a list of earthquake entries (QuakeEntry) that are within a specified maximum distance
     * from a given location. This method is useful for identifying earthquakes that occurred near a specific
     * geographical point, such as a city or a landmark.
     *
     * @param quakeData An ArrayList of QuakeEntry objects, each representing an earthquake with its own location.
     @param distMax   The maximum distance (in kilometers) within which to find earthquakes from the specified location.
     It defines the radius of interest, indicating, "I'm only interested in earthquakes that are
     within this many kilometers from my specified location."

      * @param from      The reference Location object from which the distance to each earthquake is calculated.
     * @return An ArrayList of QuakeEntry objects, each representing an earthquake that occurred within the specified
     * maximum distance from the given location. The list is empty if no earthquakes meet the criteria.
     */
    public ArrayList<QuakeEntry> filterByDistanceFrom(ArrayList<QuakeEntry> quakeData,
                                                      double distMax,
                                                      Location from) {
        return quakeData.stream()
                // Calculate the distance from 'from' to 'quakeEntry.getLocation()'
                .filter(quakeEntry -> from.distanceTo(quakeEntry.getLocation()) < distMax)
                .collect(Collectors.toCollection(ArrayList::new));

    }

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
    public void bigQuakes() {
        ArrayList<QuakeEntry> largeQuakes = new ArrayList<>();
        ArrayList<QuakeEntry> list = new ArrayList<>();

        try {
            EarthQuakeParser parser = new EarthQuakeParser();
//            String source = "data/nov20quakedatasmall.atom";
//            logger.info("Reading earthquake data from {}", source);

            list = parser.read(String.valueOf(atomFileResource));
            logger.debug("Total earthquakes read: {}", list.size());

            largeQuakes = filterByMagnitude(list, 5.0);
            logger.info("Number of earthquakes with magnitude 5.0 and above: {}", largeQuakes.size());

            // Print each quake from the filtered list
            largeQuakes.forEach(quake -> logger.info(quake.toString()));

        } catch (Exception e) {
            logger.error("Error processing earthquake data: ", e);
        } finally {
            logger.info("Read data for {} quakes", list.size());
        }
    }



    public void createCSV() {
        EarthQuakeParser parser = new EarthQuakeParser();
        String source = "data/nov20quakedatasmall.atom";
        //String source = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        ArrayList<QuakeEntry> list = parser.read(source);
        dumpCSV(list);
        System.out.println("# quakes read: " + list.size());
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }



}
