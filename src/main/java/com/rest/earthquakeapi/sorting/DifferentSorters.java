package com.rest.earthquakeapi.sorting;

import com.rest.earthquakeapi.ParserManager.EarthQuakeParser;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;

import java.util.ArrayList;
import java.util.Collections;

public class DifferentSorters {
    private final EarthQuakeParser parser;

    public DifferentSorters() {
        this.parser = new EarthQuakeParser();
    }

    public void sortByDistance() {
        EarthQuakeParser parser = new EarthQuakeParser();
        String source = "data/nov20quakedata.atom";
        ArrayList<QuakeEntry> list = parser.read(source);
        Location where = new Location(35.9886, -78.9072);
        Collections.sort(list, new DistanceComparator(where));
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }

    public void sortByMagnitude() {
        String source = "data/nov20quakedata.atom";
        ArrayList<QuakeEntry> list = parser.read(source);
        Collections.sort(list, new MagnitudeComparator());
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }

    public void sortByTitle() {
        EarthQuakeParser parser = new EarthQuakeParser();
        String source = "data/nov20quakedata.atom";
        ArrayList<QuakeEntry> list = parser.read(source);
        Collections.sort(list, new TitleComparator());
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }
}