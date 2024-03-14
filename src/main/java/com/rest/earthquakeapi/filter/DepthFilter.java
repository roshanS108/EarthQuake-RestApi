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
        if(minDepth == 0.0 || maxDepth==0.0){
            return true;
        }
        return qe.getDepth()>=minDepth && qe.getDepth()<=maxDepth;
    }
}
