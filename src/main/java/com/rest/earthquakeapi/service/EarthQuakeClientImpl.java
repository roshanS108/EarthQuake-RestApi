package com.rest.earthquakeapi.service;
import com.rest.earthquakeapi.csv.EarthQuakeParser;
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
}
