package com.rest.earthquakeapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class EarthquakeDisplayController {


    Logger logger = LoggerFactory.getLogger(EarthquakeDisplayController.class);


    @GetMapping("/hello")
    public String hello(){

        logger.trace("Fatal Error");

        return "hello world";
    }




}
