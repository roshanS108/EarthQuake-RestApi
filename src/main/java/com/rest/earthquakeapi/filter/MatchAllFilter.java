package com.rest.earthquakeapi.filter;

import com.rest.earthquakeapi.model.QuakeEntry;

import java.util.ArrayList;

public class MatchAllFilter implements Filter{

    private ArrayList<Filter> filters;

    public MatchAllFilter(){
        filters = new ArrayList<>();
    }

    public void addFilter(Filter f){
        filters.add(f);
    }

    @Override
    public boolean satisfies(QuakeEntry qe) {
        for(Filter filter: filters){
            if(!filter.satisfies(qe)){
                return false;
            }
        }
        return true;
    }

}
