package com.rest.earthquakeapi.restcontroller;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.exception.InvalidDoubleException;
import com.rest.earthquakeapi.exception.InvalidTypeException;
import com.rest.earthquakeapi.exception.QuakeDataErrorResponse;
import com.rest.earthquakeapi.exception.QuakeDataNotFoundException;
import com.rest.earthquakeapi.model.QuakeEntry;
import com.rest.earthquakeapi.service.EarthquakeDataProcessor;
import com.rest.earthquakeapi.service.MagnitudeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> getBigQuakes() {
        try {
            List<QuakeEntry> bigQuakesData = earthquakeDataProcessor.bigQuakes();
            // if list is empty inform the user that no earthquake data is found
            if (bigQuakesData.isEmpty()) {
                String errorMessage = "No Earthquake data found.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(bigQuakesData);
         }
            catch (NumberFormatException e) {
                // handling invalid non-double values
                return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
            }
    }
    /**
     * Filters earthquake data based on specified magnitude and depth ranges.
     * @param minMagnitude The minimum magnitude of earthquakes to include.
     * @param maxMagnitude The maximum magnitude of earthquakes to include.
     * @param minDepth     The minimum depth of earthquakes to include.
     * @param maxDepth     The maximum depth of earthquakes to include.
     * @return A list of QuakeEntry objects representing earthquakes that match the specified criteria.
     * @GetRequest URL: {{url}}/filtered-quakes?minMagnitude={minMag}&maxMagnitude={maxMag}&minDepth={minDepth}&maxDepth={maxDepth}
     * @Testing Url: {{url}}/filtered-quakes?minMagnitude=4.0&maxMagnitude=5.0&minDepth=-35000.0&maxDepth=-12000.0
     */
    @GetMapping("/filtered-quakes")
    public ResponseEntity<?> getFilteredQuakes(
            @RequestParam String minMagnitude,
            @RequestParam String maxMagnitude,
            @RequestParam String minDepth,
            @RequestParam String maxDepth) {
        try {
            // validating input parameters
            double minMagnitudeValue = Double.parseDouble(minMagnitude);
            double maxMagnitudeValue = Double.parseDouble(maxMagnitude);
            double minDepthValue = Double.parseDouble(minDepth);
            double maxDepthValue = Double.parseDouble(maxDepth);

            // checking if any of the required parameters are missing
            if(minMagnitudeValue == 0.0 || maxMagnitudeValue == 0.0){
                String errorMessage = "All parameter values are required. Please provide values for minMagnitude, maxMagnitude, minDepth, and maxDepth.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            // validating magnitude values
            if (minMagnitudeValue <=0 || maxMagnitudeValue <= 0) {
                String errorMessage = "No earthquake data was found within the specified magnitude range ("
                        + minMagnitudeValue + " - " + maxMagnitudeValue
                        + "). Please ensure the range values are greater than 0.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            List<QuakeEntry> filteredQuakesData = earthquakeDataProcessor.getFilteredQuakes(minMagnitudeValue, maxMagnitudeValue, minDepthValue, maxDepthValue);

            // if list is empty inform the user that no earthquake data is found
            if(filteredQuakesData.isEmpty()){
                String errorMessage = "No Earthquake data found. Please consider putting valid value";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(filteredQuakesData);
        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }
    }
    /**
     * Filters earthquake data based on specified magnitude and depth ranges.
     * @param minMagnitude The minimum magnitude of earthquakes to include.
     * @param maxMagnitude The maximum magnitude of earthquakes to include.
     * @param minDepth     The minimum depth of earthquakes to include.
     * @param maxDepth     The maximum depth of earthquakes to include.
     * @return A list of QuakeEntry objects representing earthquakes that match the specified criteria.
     * @GetRequest URL: {{url}}/earthquakes/filtered-quakes2?minMagnitude={minMag}&maxMagnitude={maxMag}&latitude={latitude}&longitude={longitude}&maxDistance={maxDistance}&phrase={phrase}&where={where}
     * @Testing Url:{{url}}/earthquakes/filtered-quakes2?minMagnitude=0.0&maxMagnitude=3.0&latitude=36.1314&longitude=-95.9372&maxDistance=10000000&phrase=California&where=any
     *
     */
    @GetMapping("/filtered-quakes2")
    public ResponseEntity<?> getFilteredQuakes2(
            @RequestParam(required = false) String minMagnitude,
            @RequestParam(required = false) String maxMagnitude,
            @RequestParam(required = false, defaultValue = "0.0") String minDepth,
            @RequestParam(required = false, defaultValue = "0.0") String maxDepth,
            @RequestParam(required = false) String latitude,
            @RequestParam(required = false) String longitude,
            @RequestParam(required = false) String maxDistance,
            @RequestParam(required = false) String phrase,
            @RequestParam(required = false) String where) {
        try {
            // validating input parameters
            double minMagnitudeValue = Double.parseDouble(minMagnitude);
            double maxMagnitudeValue = Double.parseDouble(maxMagnitude);
            double minDepthValue = Double.parseDouble(minDepth);
            double maxDepthValue = Double.parseDouble(maxDepth);
            double latitudeValue = Double.parseDouble(latitude);
            double longitudeValue = Double.parseDouble(longitude);
            double maxDistanceValue = Double.parseDouble(maxDistance);
            // Create Location object if latitude and longitude are provided by user
            Location location = new Location(latitudeValue, longitudeValue);

            // checking if any of the required parameters are missing
            if(minMagnitudeValue == 0.0 || maxMagnitudeValue == 0.0 || minDepthValue == 0.0 || maxDepthValue == 0.0){
                String errorMessage = "Minimum magnitude, maximum magnitude, minimum depth, and maximum depth parameters are required.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            // validating magnitude values
            if (minMagnitudeValue <=0 || maxMagnitudeValue <= 0) {
                throw new QuakeDataNotFoundException("Earthquake data within the specified magnitude range (" + minMagnitudeValue + " - " + maxMagnitudeValue + ") was not found. Please consider putting value that is greater than 0");
            }
            if(maxMagnitudeValue >= 10){
                String errorMessage = "No earthquakes with a magnitude of 10 or higher were found. Please try using a smaller magnitude value.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            if(latitudeValue < -90 || latitudeValue > 90){
                String message = "Invalid latitude value. Latitude value must be between -90 and and 90 degrees.";
                throw new QuakeDataNotFoundException(message);
            }
            if(longitudeValue < -180 || longitudeValue > 180){
                String message = "Invalid longitude value. Longitude value must be between -180 and and 180 degrees.";
                throw new QuakeDataNotFoundException(message);
            }
            List<QuakeEntry> filteredQuakesData = earthquakeDataProcessor.filterPossibleAllEarthquakeData(minMagnitudeValue, maxMagnitudeValue, minDepthValue, maxDepthValue,
                    location, maxDistanceValue, phrase, where);
            // if list is empty inform the user that no earthquake data is found
            if(filteredQuakesData.isEmpty()){
                String errorMessage = "No Earthquake data found. Please consider putting valid value";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(filteredQuakesData);
        }
        catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }

    }
    /**
     Retrieves a list of earthquake entries near a specified location within a maximum distance.
     @param distMax The maximum distance within which earthquakes are searched. -->distMax = 1000000
     @param latitude The latitude of the location.
     @param longitude The longitude of the location.
     @return ResponseEntity containing a list of nearby earthquake entries.
     @GetRequest URL: {{url}}/earthquakes/nearby?distMax={distMax}&latitude={latitude}&longitude={longitude}
     @Testing URL: http://localhost:8080/earthquakes/nearby?distMax=1000000&latitude=38.17&longitude=-118.82
     */
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearByEarthQuakes(
                        @RequestParam String distMax,
                        @RequestParam String latitude,
                        @RequestParam String longitude){
        try {
            double distMaxValue = Double.parseDouble(distMax);
            double latitudeValue = Double.parseDouble(latitude);
            double longitudeValue = Double.parseDouble(longitude);


            // checking if any of the required parameters are missing
            if(distMaxValue == 0.0 || latitudeValue == 0.0 || longitudeValue == 0.0){
                String errorMessage = "Distance, Latitude, and Longitude value are required.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            // checking if any of the required parameters are missing
            if(latitudeValue < -90 || latitudeValue > 90){
                String errorMessage = "Invalid latitude value. Latitude value must be between -90 and and 90 degrees.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            if(longitudeValue < -180 || longitudeValue > 180){
                String errorMessage = "Invalid longitude value. Longitude value must be between -180 and and 180 degrees.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            Location location = new Location(latitudeValue, longitudeValue);
            System.out.println("location is: " + location);
            // validating magnitude values
            if (distMaxValue < 0) {
                throw new QuakeDataNotFoundException("Earthquake data within the specified magnitude range (" + distMax + " - "  + ") was not found. Please put value that is greater than 0");
            }
            List<QuakeEntry> nearEarthQuakesData = earthquakeDataProcessor.earthQuakesNearMe(distMaxValue, location);
            // if list is empty inform the user that no earthquake data is found
            if (nearEarthQuakesData.isEmpty()) {
                String errorMessage = "No earthquake data found. Hint: Please consider providing valid values.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(nearEarthQuakesData);
        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }
    }
    /**
     * Retrieves earthquake entries within a specified depth range.
     * @GetRequest URL: {{url}}/earthquakes/by-depth?minDepth={minDepth}&maxDepth={yourMaxDepthValue)
     * @Testing Url: {{url}}/earthquakes/by-depth?minDepth=-10000&maxDepth=-5000
     */
    @GetMapping("/by-depth")
    public ResponseEntity<?> getEarthQuakesByDepth(
            @RequestParam String minDepth,
            @RequestParam String maxDepth) {
        System.out.println("min depth is : " + minDepth);
        System.out.println("maxDepth is : " + maxDepth);
        try {
            double minDepthValue = Double.parseDouble(minDepth);
            double maxDepthValue = Double.parseDouble(maxDepth);

            // checking if any of the required parameters are missing
            if(minDepthValue == 0.0 || maxDepthValue == 0.0){
                String errorMessage = "minDepth and maxDepth value are required.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            List<QuakeEntry> earthQuakesDepthData = earthquakeDataProcessor.quakesOfDepth(minDepthValue, maxDepthValue);
            // if list is empty inform the user that no earthquake data is found
            if (earthQuakesDepthData.isEmpty()) {
                String errorMessage = "No earthquake data found for depth range: " + minDepth + "," + maxDepth + " Please consider providing valid values.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(earthQuakesDepthData);
        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }
    }
    /**
     * Retrieves a list of earthquake entries closest to the specified location.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param howMany The number of closest earthquakes to retrieve.
     * @return ResponseEntity containing a list of closest earthquake entries.
     * @GetRequest Url: localhost:{{url}}/earthquakes/closest-quakes?latitude={latitude}&longitude={longitude}&howMany={howMany}
     * @Testing Url: {url}}/earthquakes/closest-quakes?latitude=-6.211&longitude=106.845&howMany=3
     */
    @GetMapping("/closest-quakes")
    public ResponseEntity<?> getClosestQuakes(
            @RequestParam String latitude,
            @RequestParam String longitude,
            @RequestParam int howMany){
        try {
            double latitudeValue = Double.parseDouble(latitude);
            double longitudeValue = Double.parseDouble(longitude);

            // checking if any of the required parameters are missing
            if (latitudeValue == 0.0 || longitudeValue == 0.0 || howMany == 0) {
                String errorMessage = "All parameter values are required. Please provide values for latitude, longitude, and howMany.";
                throw new QuakeDataNotFoundException(errorMessage);
            }

            System.out.println("howMany is: " + howMany);
            Location location = new Location(latitudeValue, latitudeValue);
            System.out.println("location is: " + location);

            if(latitudeValue < -90 || latitudeValue > 90){
                String errorMessage = "Invalid latitude value. Latitude value must be between -90 and and 90 degrees.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new QuakeDataErrorResponse(errorMessage));
            }
            if(longitudeValue < -180 || longitudeValue > 180){
                String errorMessage = "Invalid longitude value. Longitude value must be between -180 and and 180 degrees.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            List<QuakeEntry> nearEarthQuakesData = earthquakeDataProcessor.findClosestEarthQuakes(location, howMany);
            // if list is empty inform the user that no earthquake data is found
            if(nearEarthQuakesData.isEmpty()){
                String errorMessage = "No earthquake data found. Hint: Please consider providing valid values.";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(nearEarthQuakesData);
        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }
    }
    /**
     * Displays earthquake with the highest magnitude in earthquakes
     * @param howMany specifies howMany largest-quakes it should return
     * @return A list of largest quakes
     * @GetRequest URL: localhost:{{url}}/earthquakes/largest-quakes?howMany={howMany}
     * @Testing Url: {{url}}/earthquakes/largest-quakes?howMany=5
     */
    @GetMapping("/largest-quakes")
    public ResponseEntity<?> getLargestQuakes(
            @RequestParam int howMany){
        System.out.println("how many is: " + howMany);
        try {
            if(howMany <= 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new QuakeDataErrorResponse("Parameter 'howMany' must be a positive integer."));
            }
            List<QuakeEntry> largestEarthQuakesData = magnitudeAnalysisService.findLargestEarthQuakes(howMany);
            // if list is empty inform the user that no earthquake data is found
            if (largestEarthQuakesData.isEmpty()) {
                String errorMessage = "No earthquake data found";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(largestEarthQuakesData);

        }catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Parameter 'howMany' must be a valid integer.");
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
     * @GetUrlRequest: {{url}}/earthquakes/by-phrase?phrase={}&where={}
     * @Testing Url: {{url}}/earthquakes/by-phrase?phrase=California&where=end
     */
    @GetMapping("/by-phrase")
    public ResponseEntity<?> findEarthquakesByPhrase(
            @RequestParam("phrase") String phrase,
            @RequestParam("where")String where){
        System.out.println("phrase is: " + phrase);
        System.out.println("where is: " + where);
        try {
            List<QuakeEntry> earthQuakesByPhraseData = earthquakeDataProcessor.findEarthQuakesByPhrase(phrase, where);

            // if list is empty inform the user that no earthquake data is found
            if (earthQuakesByPhraseData.isEmpty()) {
                String errorMessage = "No earthquake data found";
                throw new QuakeDataNotFoundException(errorMessage);
            }
            return ResponseEntity.ok(earthQuakesByPhraseData);
        }catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }
    }
}
