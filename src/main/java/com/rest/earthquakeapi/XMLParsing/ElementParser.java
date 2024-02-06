package com.rest.earthquakeapi.XMLParsing;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;

public interface ElementParser<T> {


    T parseElement(Element element);







    boolean satisfies();


}
