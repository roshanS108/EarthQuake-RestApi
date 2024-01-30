package com.rest.earthquakeapi.service;

import java.util.*;
import java.util.stream.Collectors;

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


    public EarthQuakeClient() {
        // TODO Auto-generated constructor stub
    }

    public ArrayList<QuakeEntry> filterByMagnitude(ArrayList<QuakeEntry> quakeData,
                                                   double magMin) {
        if(quakeData == null){
            throw new IllegalArgumentException("quakeData cannot be null");
        }
        if(magMin < 0){
            throw new IllegalArgumentException("magMin must be non-negative");
        }
        return quakeData.stream()
                .filter(quakeEntry -> quakeEntry.getMagnitude() > magMin)
                .collect(Collectors.toCollection(ArrayList::new));
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


    /**
     * Filters a list of earthquake entries based on a specified phrase and its location in the earthquake's title.
     * The method examines the title of each earthquake (obtained via the {@code getInfo} method of {@code QuakeEntry})
     * to determine if it contains the given phrase according to the specified criteria.
     *
     * @param quakeData An {@code ArrayList} of {@code QuakeEntry} objects, each representing an earthquake.
     * @param where     A {@code String} specifying where to search for the phrase in the title.
     *                  It can be one of three values: "start", "end", or "any".
     *                  "start" - The phrase must start the title.
     *                  "end" - The phrase must end the title.
     *                  "any" - The phrase can be anywhere in the title.
     * @param phrase    The {@code String} phrase to search for in the title of each earthquake.
     * @return An {@code ArrayList} of {@code QuakeEntry} objects whose titles contain the specified phrase
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

    public void quakesByPhrase() {
        EarthQuakeParser parser = new EarthQuakeParser();
        //String source = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedata.atom";
        ArrayList<QuakeEntry> list = parser.read(source);

        String phrase = "Creek";
        String where = "any";

        // Use filterByPhrase to filter the earthquakes
        ArrayList<QuakeEntry> filteredList = filterByPhrase(list, where, phrase);

        // Iterate over the filtered list and print each quake
        for (QuakeEntry quakeEntry : filteredList) {
            System.out.println(quakeEntry);
        }
        // Print the number of earthquakes found
        System.out.println("Found " + filteredList.size() + " quakes that match the criteria");


    }


    public void bigQuakes() {
        EarthQuakeParser parser = new EarthQuakeParser();
        //String source = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list = parser.read(String.valueOf(atomFileResource));
        ArrayList<QuakeEntry> largeQuakes = filterByMagnitude(list, 5.0);

        //print each quake from the filtered list
        largeQuakes.forEach(System.out::println);
        //counting and printing the number of earthquakes larger than 5.0
        long count = largeQuakes.stream()
                .count();
        System.out.println("Number of earthquakes with magnitude greater than 5.0: " + count);

        System.out.println("read data for " + list.size() + " quakes");
    }

    /**
     * Filters earthquakes based on their depth within a specified range.
     *
     * @param quakeData The ArrayList of type QuakeEntry containing earthquake data.
     * @param minDepth  The minimum depth value for filtering (exclusive).
     * @param maxDepth  The maximum depth value for filtering (exclusive).
     * @return An ArrayList of type QuakeEntry containing earthquakes with depth between minDepth and maxDepth.
     * (Excludes earthquakes with depth exactly minDepth or maxDepth.)
     */
    public ArrayList<QuakeEntry> filterByDepth(ArrayList<QuakeEntry> quakeData,
                                               double minDepth, double maxDepth) {

        ArrayList<QuakeEntry> filteredList = new ArrayList<>();

        for (QuakeEntry quake : quakeData) {
            double depth = quake.getDepth();

            if (depth > minDepth && depth < maxDepth) {
                filteredList.add(quake);
            }
        }
        return filteredList;
    }


    /**
     * Prints details of all earthquakes from a specified data source whose depth is within a given range.
     * The method reads earthquake data, filters it based on the depth criteria, and prints each qualifying earthquake.
     * It also prints the total number of earthquakes found that match the depth criteria.
     * <p>
     * The method assumes the existence of a data source (like a file named 'nov20quakedatasmall.atom') or from URL link
     * containing earthquake information. Each earthquake's information is encapsulated as a QuakeEntry object.
     * <p>
     * The depth range for filtering is set within the method as -10000.0 to -5000.0 (exclusive range).
     * These values can be adjusted according to the requirements.
     * <p>
     * Usage Example:
     * To use this method, simply call it without any parameters. It will read the earthquake data,
     * filter based on the predefined depth range, and print the relevant information.
     *
     * <pre>
     * {@code
     * quakesOfDepth();
     * }
     * </pre>
     * <p>
     * Output Example:
     * On running this method with 'nov20quakedatasmall.atom', if there are earthquakes within the specified depth range,
     * it will print the details of each such earthquake and the total count of these earthquakes.
     */
    public void quakesOfDepth() {
        EarthQuakeParser parser = new EarthQuakeParser();
        //String source = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list = parser.read(source);

        double minDepth = -10000.0;
        double maxDepth = -5000.0;

        ArrayList<QuakeEntry> filtersDepth = filterByDepth(list, minDepth, maxDepth);

        System.out.println("Find quakes with depth between " + minDepth + " and" + maxDepth);

        for (QuakeEntry quakeEntry : filtersDepth) {
            System.out.println(quakeEntry);
        }
        System.out.println("Found " + filtersDepth.size() + " quakes that match that criteria\n");
    }

    /**
     * The 'closeToMe' method to find and print information about earthquakes within 1000 kilometers
     * of a specified city. This method currently reads earthquake data from a URL, stores each earthquake as a
     * QuakeEntry in an ArrayList named 'list', and prints the total number of earthquakes read. The method includes
     * predefined locations for two cities: Durham, NC (35.988, -78.907) and Bridgeport, CA (38.17, -118.82).
     *
     * After modifications, this method will:
     * 1. Use the 'filterByDistanceFrom' method to filter earthquakes within 1000 kilometers of the specified city.
     * 2. Print the distance from each earthquake to the city, followed by the earthquake information obtained via 'getInfo()'.
     */
    public void closeToMe() {
        EarthQuakeParser parser = new EarthQuakeParser();
//        String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list = parser.read(source);
        System.out.println("read data for " + list.size() + " quakes");

        // This location is Durham, NC
//        Location city = new Location(35.988, -78.907);

        // This location is Bridgeport, CA
        Location city = new Location(38.17, -118.82);

        ArrayList<QuakeEntry>closeEarthQuakes = filterByDistanceFrom(list, 1000000, city);
        closeEarthQuakes.forEach(quakeEntry ->
                System.out.println((quakeEntry.getLocation().distanceTo(city) / 1000) + " " + quakeEntry.getInfo()));

        System.out.println("Found " + closeEarthQuakes.size() + " that match that criteria");
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

    public static void main(String[] args) {

        EarthQuakeClient earthQuakeClient = new EarthQuakeClient();
        earthQuakeClient.closeToMe();



    }

}
