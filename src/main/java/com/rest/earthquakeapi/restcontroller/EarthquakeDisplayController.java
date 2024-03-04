package com.rest.earthquakeapi.restcontroller;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;
import com.rest.earthquakeapi.service.EarthquakeDataProcessor;
import com.rest.earthquakeapi.service.MagnitudeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/earthquakes")
public class EarthquakeDisplayController {

    private EarthquakeDataProcessor earthquakeDataProcessor;

    private MagnitudeAnalysisService magnitudeAnalysisService;

    @Autowired
    public EarthquakeDisplayController(EarthquakeDataProcessor earthquakeDataProcessor, MagnitudeAnalysisService magnitudeAnalysisService){
        this.earthquakeDataProcessor = earthquakeDataProcessor;
        this.magnitudeAnalysisService = magnitudeAnalysisService;
    }
    @GetMapping("/bigQuakes")
    public ResponseEntity<List<QuakeEntry>> getBigQuakes() {
        try {
            List<QuakeEntry> largeQuakes = earthquakeDataProcessor.bigQuakes();
            return ResponseEntity.ok(largeQuakes);
        } catch (Exception e) {
            // Log the exception and returning an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     Retrieves a list of earthquake entries near a specified location within a maximum distance.
     @param distMax The maximum distance within which earthquakes are searched. -->distMax = 1000000
     @param latitude The latitude of the location.
     @param longitude The longitude of the location.
     @return ResponseEntity containing a list of nearby earthquake entries.
     @GetREquest URL: http://localhost:8080/earthquakes/nearby?distMax={distMax}&latitude={latitude}&longitude={longitude}
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<QuakeEntry>> getNearByEarthQuakes(
                        @RequestParam double distMax,
                        @RequestParam double latitude,
                        @RequestParam double longitude){
        System.out.println("dist max is: " + distMax);
        Location location = new Location(latitude, longitude);
        System.out.println("location is: " + location);
        try {
            List<QuakeEntry> nearEarthQuakes = earthquakeDataProcessor.earthQuakesNearMe(distMax, location);
            return ResponseEntity.ok(nearEarthQuakes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-depth")
    public ResponseEntity<List<QuakeEntry>> getEarthQuakesByDepth(
            @RequestParam double minDepth,
            @RequestParam double maxDepth){
        System.out.println("min depth is : " + minDepth);
        System.out.println("maxDepth is : " + maxDepth);
        try {
            List<QuakeEntry> nearEarthQuakes = earthquakeDataProcessor.quakesOfDepth(minDepth, maxDepth);
            return ResponseEntity.ok(nearEarthQuakes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/closest-quakes")
    public ResponseEntity<List<QuakeEntry>> getClosestQuakes(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam int howMany){
        System.out.println("howMany is: " + howMany);
        Location location = new Location(latitude, longitude);
        System.out.println("location is: " + location);

        try {
            List<QuakeEntry> nearEarthQuakes = earthquakeDataProcessor.findClosestEarthQuakes(location, howMany);
            return ResponseEntity.ok(nearEarthQuakes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Displays earthquake with the highest magnitude in earthquakes
     * @param howMany specifies howMany largest-quakes it should return
     * @return A list of largest quakes
     */

    @GetMapping("/largest-quakes")
    public ResponseEntity<List<QuakeEntry>> getLargestQuakes(
            @RequestParam int howMany){
        System.out.println("how many is: " + howMany);
        try {
            List<QuakeEntry> nearEarthQuakes = magnitudeAnalysisService.findLargestEarthQuakes(howMany);
            return ResponseEntity.ok(nearEarthQuakes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Displays earthquakes based on a specified phrase and its location in the earthquake's title.
     * @param phrase The phrase to search for in the title of each earthquake.
     * @param where Specifying where to search for the phrase in the title.
     *              It can be one of three values: "start", "end", or "any".
     *              "start" - The phrase must start the title.
     *              "end" - The phrase must end the title.
     *              "any" - The phrase can be anywhere in the title.
     * @return A list of earthquakes that match the search criteria.
     */
    @GetMapping("/by-phrase")
    public ResponseEntity<List<QuakeEntry>> findEarthquakesByPhrase(
            @RequestParam("phrase") String phrase,
            @RequestParam("where")String where){
        System.out.println("phrase is: " + phrase);
        System.out.println("where is: " + where);
        try {
            List<QuakeEntry> nearEarthQuakes = earthquakeDataProcessor.findEarthQuakesByPhrase(phrase, where);
            return ResponseEntity.ok(nearEarthQuakes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
