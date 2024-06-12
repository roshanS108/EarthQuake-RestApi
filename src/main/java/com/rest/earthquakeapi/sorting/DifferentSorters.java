package com.rest.earthquakeapi.sorting;

import com.rest.earthquakeapi.ParserManager.EarthQuakeParser;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
public class DifferentSorters {
    private final EarthQuakeParser parser;
    @Value("${earthquake.source}")
    private String source;
    public DifferentSorters() {
        this.parser = new EarthQuakeParser();
    }

    public void sortByMagnitude() {
        ArrayList<QuakeEntry> list = parser.read(source);
        Collections.sort(list, new MagnitudeComparator());
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }
    public void sortByTitle() {
        ArrayList<QuakeEntry> list = parser.read(source);
        Collections.sort(list, new TitleComparator());
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }
    public void sortByDepth() {
        ArrayList<QuakeEntry> list = parser.read(source);
        Collections.sort(list, new DepthComparator());
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }
    public void sortAll() {
        sortByMagnitude();
        sortByTitle();
        sortByDepth();
    }

}