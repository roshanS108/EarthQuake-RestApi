package com.rest.earthquakeapi.filter;

import com.rest.earthquakeapi.model.QuakeEntry;

public interface Filter {

    public boolean satisfies(QuakeEntry qe);




}
