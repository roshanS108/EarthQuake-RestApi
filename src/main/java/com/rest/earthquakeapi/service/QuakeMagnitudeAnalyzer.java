package com.rest.earthquakeapi.service;
import com.rest.earthquakeapi.ParserManager.EarthQuakeParser;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
@Service
public class QuakeMagnitudeAnalyzer implements MagnitudeAnalysisService{
    @Override
    public List<QuakeEntry> findLargestEarthQuakes(int howMany) {
        EarthQuakeParser parser = new EarthQuakeParser();
//        String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list = parser.read(source);

        ArrayList<QuakeEntry> largestEarthQuake = getLargest(list, howMany);
        // Printing the total number of earthquakes
        System.out.println("Number of earthquakes read: " + list.size());

        return largestEarthQuake;
    }
    /**
     * Returns the index location in data of earthquake with largest magnitude.
     * @param data An ArrayList of QuakeEntry representing earthquake data.
     * @return the index location of largest earthquake magnitude
     */
    public int indexOfLargest(ArrayList<QuakeEntry> data){
        if(data == null || data.isEmpty()){
            return -1;
        }
        double maxMagnitude = Integer.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0; i < data.size(); i++) {
            double currMagnitude = data.get(i).getMagnitude();
            if (currMagnitude > maxMagnitude) {
                maxMagnitude = currMagnitude;
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    /**
     * Finds and returns a list of the top 'howMany' largest magnitude earthquakes from the given list of quakes.
     * The returned earthquakes are sorted by magnitude, with the largest earthquake at the first position.
     * If the input list contains fewer earthquakes than 'howMany', the method returns all available earthquakes.
     *
     * @param quakeData An ArrayList of QuakeEntry representing earthquake data.
     * @param howMany   The number of top magnitude earthquakes to return.
     * @return An ArrayList of QuakeEntry containing the largest earthquakes, up to 'howMany', sorted by magnitude.
     */
    private ArrayList<QuakeEntry> getLargest(ArrayList<QuakeEntry> quakeData, int howMany) {
        ArrayList<QuakeEntry> ret = new ArrayList<QuakeEntry>();
        ArrayList<QuakeEntry> copy = new ArrayList<QuakeEntry>(quakeData);

        for (int i = 0; i < howMany; i++) {
            int maxIndex = indexOfLargest(copy);
            ret.add(copy.get(maxIndex));
            copy.remove(maxIndex);
        }
        return ret;
    }

}
