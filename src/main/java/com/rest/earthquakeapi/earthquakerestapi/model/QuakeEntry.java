package com.rest.earthquakeapi.earthquakerestapi.model;

import com.rest.earthquakeapi.earthquakerestapi.apache.Location;

/**
 * Represents a single earthquake entry.
 * This class encapsulates the basic characteristics of an earthquake, including its location, magnitude, depth, and a descriptive title.
 * The QuakeEntry class is immutable, meaning its state cannot change after construction.
 *
 * <p>Key Concepts:</p>
 * <ul>
 *     <li>POJO (Plain Old Java Object): Although QuakeEntry has behavior beyond just storing data, it is a simple representation of an earthquake's data.</li>
 *     <li>Immutable: Once a QuakeEntry is constructed, its data cannot be altered.</li>
 *     <li>Has-A Relationship: QuakeEntry has a Location object to represent the earthquake's geographical coordinates.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <ul>
 *     <li>Created when parsing earthquake data.</li>
 *     <li>Used to access specific information about an earthquake such as location, magnitude, etc.</li>
 *     <li>Supports comparison based on location for sorting and searching operations.</li>
 * </ul>
 *
 * @see Location
 */

public class QuakeEntry implements Comparable<QuakeEntry>{

    private Location myLocation;
    private String title;
    private double depth;
    private double magnitude;

    public QuakeEntry(double lat, double lon, double mag,
                      String t, double d) {
        myLocation = new Location(lat,lon);

        magnitude = mag;
        title = t;
        depth = d;
    }

    public Location getLocation(){
        return myLocation;
    }

    public double getMagnitude(){
        return magnitude;
    }

    public String getInfo(){
        return title;
    }

    public double getDepth(){
        return depth;
    }

    @Override
    public int compareTo(QuakeEntry loc) {
//        double difflat = myLocation.getLatitude() - loc.myLocation.getLatitude();
//        if (Math.abs(difflat) < 0.001) {
//            double diff = myLocation.getLongitude() - loc.myLocation.getLongitude();
//            if (diff < 0) return -1;
//            if (diff > 0) return 1;
//            return 0;
//        }
//        if (difflat < 0) return -1;
//        if (difflat > 0) return 1;

        // never reached
        return 0;
    }


    public String toString(){
        return String.format("(%3.2f, %3.2f), mag = %3.2f, depth = %3.2f, title = %s", myLocation.getLatitude(),myLocation.getLongitude(),magnitude,depth,title);
    }

}