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

        largestEarthQuake.forEach(System.out::println);

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
        int indexLargest = 0;
        for(int i = 1; i<data.size(); i++){
            QuakeEntry quakeEntry = data.get(i);
            if(quakeEntry.getMagnitude() > data.get(indexLargest).getMagnitude()){
                indexLargest = i;
            }
        }
        return indexLargest;
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
    public ArrayList<QuakeEntry> getLargest(ArrayList<QuakeEntry> quakeData, int howMany){

        //copy of quakeData
        ArrayList<QuakeEntry> quakeDataCopy = new ArrayList<>(quakeData);

        ArrayList<QuakeEntry> largestMagnitude = new ArrayList<>();

        for(int i = 0; i<howMany && !quakeDataCopy.isEmpty(); i++){
            int maxIndex = indexOfLargest(quakeData);
            largestMagnitude.add(quakeDataCopy.get(maxIndex));
            quakeData.remove(maxIndex);

        }
        return largestMagnitude;
    }


}
