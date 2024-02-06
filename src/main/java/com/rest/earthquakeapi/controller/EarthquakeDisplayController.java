package com.rest.earthquakeapi.controller;
import com.rest.earthquakeapi.model.QuakeEntry;
import com.rest.earthquakeapi.service.EarthquakeDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EarthquakeDisplayController {

    private EarthquakeDataProcessor earthquakeDataProcessor;

    @Autowired
    public EarthquakeDisplayController(EarthquakeDataProcessor earthquakeDataProcessor){
        this.earthquakeDataProcessor = earthquakeDataProcessor;
    }

    @GetMapping("/earthquakes")
    public ResponseEntity<List<QuakeEntry>> getBigQuakes() {
        try {
            List<QuakeEntry> largeQuakes = earthquakeDataProcessor.bigQuakes();
            return ResponseEntity.ok(largeQuakes);
        } catch (Exception e) {
            // Log the exception and returning an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
