package com.rest.earthquakeapi.XMLParsing;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public interface ElementParser<T> {


    T parseElement(Element element);





}
