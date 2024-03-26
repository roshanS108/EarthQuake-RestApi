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
            if (bigQuakesData.isEmpty()) {
                // if list is empty inform the user that no earthquake data is found
                String message = "No Earthquake data found.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(bigQuakesData);
         }
            catch (NumberFormatException e) {
                // handling invalid non-double values
                return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
            } catch (Exception e) {
            // Log the exception and returning an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            // Validate input parameters
            double minMagnitudeValue = Double.parseDouble(minMagnitude);
            double maxMagnitudeValue = Double.parseDouble(maxMagnitude);
            double minDepthValue = Double.parseDouble(minDepth);
            double maxDepthValue = Double.parseDouble(maxDepth);
            // Validate magnitude values
            if (minMagnitudeValue < 0 || maxMagnitudeValue < 0) {
                throw new QuakeDataNotFoundException("Earthquake data within the specified magnitude range (" + minMagnitudeValue + " - " + maxMagnitudeValue + ") was not found. Please put value that is greater than 0");
            }
            List<QuakeEntry> filteredQuakesData = earthquakeDataProcessor.getFilteredQuakes(minMagnitudeValue, maxMagnitudeValue, minDepthValue, maxDepthValue);
            if(filteredQuakesData.isEmpty()){
                // if list is empty inform the user that no earthquake data is found
                String message = "No Earthquake data found. Please consider putting valid value";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(filteredQuakesData);
        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }
        catch (Exception e) {
            // Log the exception and returning an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Exception handler to handle custom exception
    @ExceptionHandler
    public ResponseEntity<QuakeDataErrorResponse> handleException(QuakeDataNotFoundException exc) {
        QuakeDataErrorResponse error = new QuakeDataErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
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
            @RequestParam(required = false) Double minMagnitude,
            @RequestParam(required = false) Double maxMagnitude,
            @RequestParam(required = false, defaultValue = "0.0") Double minDepth,
            @RequestParam(required = false, defaultValue = "0.0") Double maxDepth,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) String phrase,
            @RequestParam(required = false) String where) {

        // Create Location object if latitude and longitude are provided by user
        Location location = new Location(latitude, longitude);

       /* // default values for minDepth and maxDepth for handling null values
        if (minDepth == null) {
            minDepth = 0.0;
        }
        if (maxDepth == null) {
            maxDepth = Double.MAX_VALUE;
        }*/

        try {
            List<QuakeEntry> filteredQuakesData = earthquakeDataProcessor.filterPossibleAllEarthquakeData(minMagnitude, maxMagnitude, minDepth, maxDepth,
                    location, maxDistance, phrase, where);
            if(filteredQuakesData.isEmpty()){
                // if list is empty inform the user that no earthquake data is found
                String message = "No Earthquake data found. Please consider putting valid value";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(filteredQuakesData);
        }
        catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        }
        catch (Exception e) {
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
     @GetRequest URL: {{url}}/earthquakes/nearby?distMax={distMax}&latitude={latitude}&longitude={longitude}
     @Testing URL: http://localhost:8080/earthquakes/nearby?distMax=1000000&latitude=38.17&longitude=-118.82
     */
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearByEarthQuakes(
                        @RequestParam double distMax,
                        @RequestParam double latitude,
                        @RequestParam double longitude){
        System.out.println("dist max is: " + distMax);
        Location location = new Location(latitude, longitude);
        System.out.println("location is: " + location);
        // Validate magnitude values
        if (distMax < 0) {
            throw new QuakeDataNotFoundException("Earthquake data within the specified magnitude range (" + distMax + " - "  + ") was not found. Please put value that is greater than 0");
        }
        try {
            List<QuakeEntry> nearEarthQuakesData = earthquakeDataProcessor.earthQuakesNearMe(distMax, location);
            if (nearEarthQuakesData.isEmpty()) {
                // if list is empty inform the user that no earthquake data is found
                String message = "No earthquake data found.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(nearEarthQuakesData);

        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves earthquake entries within a specified depth range.
     * @GetRequest URL: {{url}}/earthquakes/by-depth?minDepth={minDepth}&maxDepth={yourMaxDepthValue)
     * @Testing Url: {{url}}/earthquakes/by-depth?minDepth=-10000&maxDepth=-5000
     */
    @GetMapping("/by-depth")
    public ResponseEntity<?> getEarthQuakesByDepth(
            @RequestParam double minDepth,
            @RequestParam double maxDepth) {
        System.out.println("min depth is : " + minDepth);
        System.out.println("maxDepth is : " + maxDepth);
        try {
            List<QuakeEntry> earthQuakesDepthData = earthquakeDataProcessor.quakesOfDepth(minDepth, maxDepth);

            if (earthQuakesDepthData.isEmpty()) {
                // if list is empty inform the user that no earthquake data is found
                String message = "No earthquake data found for depth range: " + minDepth + "," + maxDepth + " Please consider putting valid value.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(earthQuakesDepthData);
        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam int howMany){
        System.out.println("howMany is: " + howMany);
        Location location = new Location(latitude, longitude);
        System.out.println("location is: " + location);

        try {
            List<QuakeEntry> nearEarthQuakesData = earthquakeDataProcessor.findClosestEarthQuakes(location, howMany);
            if(nearEarthQuakesData.isEmpty()){
                // if list is empty inform the user that no earthquake data is found
                String message = "No earthquake data found. Please consider putting valid value.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(nearEarthQuakesData);
        } catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            List<QuakeEntry> largestEarthQuakesData = magnitudeAnalysisService.findLargestEarthQuakes(howMany);
            if (largestEarthQuakesData.isEmpty()) {
                // if list is empty inform the user that no earthquake data is found
                String message = "No earthquake data found";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(largestEarthQuakesData);

        }catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
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
                String message = "No earthquake data found";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuakeDataErrorResponse(message));
            }
            return ResponseEntity.ok(earthQuakesByPhraseData);
        }catch (NumberFormatException e) {
            // handling invalid non-double values
            return ResponseEntity.badRequest().body("Invalid parameter value. Please provide numeric values.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
