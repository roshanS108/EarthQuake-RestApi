package com.rest.earthquakeapi.restcontroller;
import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;
import com.rest.earthquakeapi.service.EarthquakeDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/earthquakes")
public class EarthquakeDisplayController {

    private EarthquakeDataProcessor earthquakeDataProcessor;

    @Autowired
    public EarthquakeDisplayController(EarthquakeDataProcessor earthquakeDataProcessor){
        this.earthquakeDataProcessor = earthquakeDataProcessor;
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




}
