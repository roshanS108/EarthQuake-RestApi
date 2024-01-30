package com.rest.earthquakeapi.earthquakerestapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
@RestController
public class EarthquakeDisplayController {

    @Value("classpath:/data/nov20quakedatasmall.atom")
    private Resource atomFileResource;





}
