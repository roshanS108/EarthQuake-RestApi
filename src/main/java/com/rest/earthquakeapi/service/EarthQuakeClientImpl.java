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
public class EarthQuakeClientImpl implements EarthquakeDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EarthQuakeClientImpl.class);

    public EarthQuakeClientImpl(){

    }
    @Override
    public ArrayList<QuakeEntry> filterByMagnitude(ArrayList<QuakeEntry> quakeData,
                                                   double magMin){
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
    /**
     * Finds and returns a list of earthquakes that occurred near a specified location.
     * How it works:
     * 1. It creates an instance of EarthQuakeParser to read earthquake data.
     * 2. It specifies the source of the earthquake data. use a URL to fetch live data instead.)
     * 3. It reads the earthquake data into a list of QuakeEntry objects.
     * 4. It prints out the number of earthquakes read from the data source.
     * 5. It filters the list of QuakeEntry objects to find those earthquakes that are within
     *    a specified maximum distance ('distMax') from a 'from' location.
     * 6. For each earthquake found within the specified distance, it prints out the distance
     *    to the earthquake from a hard-coded location (in this case, Bridgeport, CA) and the
     *    earthquake's information, --->soon making the location dynamic.
     * 7. It prints out the total number of earthquakes found that match the criteria.
     * 8. Finally, it returns the filtered list of close earthquakes.
     * Parameters:
     *  - distMax: The maximum distance (in kilometers) from the 'from' location to consider
     *             an earthquake as being "near".
     *  - from: The location from which the distance to each earthquake is measured.
     * Returns:
     *  - A list of QuakeEntry objects, each representing an earthquake that occurred within
     *    the specified maximum distance from the 'from' location.
     */
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

    @Override
    public List<QuakeEntry> findClosestEarthQuakes(Location current, int howMany) {
        EarthQuakeParser parser = new EarthQuakeParser();
        String source = "data/nov20quakedatasmall.atom";
//        String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        ArrayList<QuakeEntry> list  = parser.read(source);
        System.out.println("read data for "+list.size());

        ArrayList<QuakeEntry> closeQuakes = getClosest(list,current,howMany);
        for(int k=0; k < closeQuakes.size(); k++){
            QuakeEntry entry = closeQuakes.get(k);
            double distanceInMeters = current.distanceTo(entry.getLocation());
            System.out.printf("%4.2f\t %s\n", distanceInMeters/1000,entry);
        }
        return closeQuakes;
    }
    /**
     * Finds and returns a list of the closest earthquakes to a given location.
     * This method identifies the specified number of earthquakes (howMany) from a list of earthquake data (quakeData)
     * that are closest to a given location (current).
     * The earthquakes are sorted based on their proximity to the specified location, with the closest earthquake at index 0.

     * Note: This method does not alter the original quakeData list.
     * @param quakeData An ArrayList of QuakeEntry objects representing earthquake data.
     * @param current   The Location object representing the location from which distances are measured.
     * @param howMany   The maximum number of closest earthquakes to find. If this number is greater than
     *                  the size of quakeData, all earthquakes from quakeData are included in the returned list.
     * @return An ArrayList of QuakeEntry objects representing the closest earthquakes to the specified location,
     *         sorted by ascending distance. The size of the returned list is the smaller of howMany or the size
     *         of quakeData.
     */
    public ArrayList<QuakeEntry> getClosest(ArrayList<QuakeEntry> quakeData, Location current, int howMany) {
        ArrayList<QuakeEntry> copy = new ArrayList<>(quakeData);
        ArrayList<QuakeEntry> ret = new ArrayList<QuakeEntry>();

        // Find the closest earthquakes up to 'howMany' times
        for(int i = 0; i<howMany; i++) {

            // Initialize minIndex to store the index of the closest earthquake in the copy ArrayList
            int minIndex = 0;
            for (int j = 1; j < copy.size(); j++) {
                QuakeEntry quake = copy.get(j);
                Location loc = quake.getLocation();

                if (loc.distanceTo(current) < copy.get(minIndex)
                        .getLocation().distanceTo(current)) {
                    minIndex = j;
                }
            }
            ret.add(copy.get(minIndex));
            copy.remove(minIndex);
        }
        return ret;
    }
    /**
     * Filters a list of earthquake entries based on a specified phrase and its location in the earthquake's title.
     * @param quakeData QuakeEntry objects, each representing an earthquake.
     * @param where     Specifying where to search for the phrase in the title.
     *                  It can be one of three values: "start", "end", or "any".
     *                  "start" - The phrase must start the title.
     *                  "end" - The phrase must end the title.
     *                  "any" - The phrase can be anywhere in the title.
     * @param phrase    The phrase to search for in the title of each earthquake.
     * @return An ArrayList of QuakeEntry objects whose titles contain the specified phrase
     * in the specified location. The returned list is empty if no earthquakes meet the criteria.
     */
    public static ArrayList<QuakeEntry> filterByPhrase(ArrayList<QuakeEntry> quakeData,
                                                       String where, String phrase) {
        ArrayList<QuakeEntry> answer = new ArrayList<>();

        if(where.equals("start")) {
            for (QuakeEntry qe: quakeData) {
                if (qe.getInfo().startsWith(phrase)) {
                    answer.add(qe);
                }
            }
        }
        else if (where.equals("any")) {
            for (QuakeEntry qe: quakeData) {
                if (qe.getInfo().contains(phrase)) {
                    answer.add(qe);
                }
            }
        }
        else if (where.equals("end")) {
            for (QuakeEntry qe: quakeData) {
                if (qe.getInfo().endsWith(phrase)) {
                    answer.add(qe);
                }
            }
        }
        return answer;
    }
    @Override
    public List<QuakeEntry> findEarthQuakesByPhrase(String phrase, String where) {
        EarthQuakeParser parser = new EarthQuakeParser();
        //String source = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list = parser.read(source);


        // Use filterByPhrase to filter the earthquakes
        ArrayList<QuakeEntry> filteredList = filterByPhrase(list, where, phrase);

        // Iterate over the filtered list and print each quake
        for (QuakeEntry quakeEntry : filteredList) {
            System.out.println(quakeEntry);
        }
        // Print the number of earthquakes found
        System.out.println("Found " + filteredList.size() + " quakes that match the criteria");
        return filteredList;
    }

}
