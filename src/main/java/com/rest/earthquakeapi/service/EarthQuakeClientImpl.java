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
 * <p>
 * <p>

 */
@Service
public class EarthQuakeClientImpl implements EarthquakeDataProcessor,EarthQuakeDataExporter{

    @Value("classpath:/data/nov20quakedatasmall.atom")
    private Resource atomFileResource;
    private static final Logger logger = LoggerFactory.getLogger(EarthQuakeClientImpl.class);

    public EarthQuakeClientImpl() {
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
    public void bigQuakes() {

        ArrayList<QuakeEntry> largeQuakes = new ArrayList<QuakeEntry>();
        ArrayList<QuakeEntry> list = new ArrayList<>();

        try {
            EarthQuakeParser parser = new EarthQuakeParser();
//            String source = "data/nov20quakedatasmall.atom";
//            logger.info("Reading earthquake data from {}", source);

            list = parser.read(String.valueOf(atomFileResource.toString()));
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



}
