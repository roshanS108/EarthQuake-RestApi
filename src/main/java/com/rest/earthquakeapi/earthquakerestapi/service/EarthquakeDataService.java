package com.rest.earthquakeapi.earthquakerestapi.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EarthquakeDataService {

    @Scheduled(fixedRate = 100000) //runs every 100 seconds
    public void fetchEarthQuakeData(){

        //logic and implementation for fetching and parsing the atom feed.


    }


}
