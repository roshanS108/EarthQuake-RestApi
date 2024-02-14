package com.rest.earthquakeapi.filter;

import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;

public class DistanceFilter implements Filter{

    private Location location;
    private double maxDistance;

    public DistanceFilter(Location location, double maxDistance){
        this.location = location;
        this.maxDistance = maxDistance;
    }
    @Override
    public boolean satisfies(QuakeEntry qe) {
        return qe.getLocation().distanceTo(location) < maxDistance;
    }
}
