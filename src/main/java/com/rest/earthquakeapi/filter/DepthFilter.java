package com.rest.earthquakeapi.filter;

import com.rest.earthquakeapi.apache.Location;
import com.rest.earthquakeapi.model.QuakeEntry;

public class DepthFilter implements Filter{

    private double minDepth;
    private double maxDepth;

    public DepthFilter(double minDepth, double maxDepth){
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }
    @Override
    public boolean satisfies(QuakeEntry qe) {
        return qe.getDepth()>=maxDepth && qe.getDepth()<=maxDepth;
    }
}
