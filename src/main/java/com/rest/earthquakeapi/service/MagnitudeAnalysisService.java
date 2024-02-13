package com.rest.earthquakeapi.service;

import com.rest.earthquakeapi.model.QuakeEntry;

import java.util.ArrayList;
import java.util.List;

public interface MagnitudeAnalysisService {

    List<QuakeEntry> findLargestEarthQuakes(int howMany);

}
