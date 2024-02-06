package com.rest.earthquakeapi.XMLParsing;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
public class MagnitudeParser implements ElementParser<Double> {
    public MagnitudeParser() {
    }
    @Override
    public Double parseElement(Element element) {
        //retrieves the "title" info of the place where earth quake occurred. ex --->75km WSW of Cantwell, Alaska with the MAGNITUDE --->75km
        NodeList t2 = element.getElementsByTagName("title");

        if (t2 != null && t2.getLength() > 0) {
            String s2 = t2.item(0).getChildNodes().item(0).getNodeValue();
            String mags = s2.substring(2, s2.indexOf(" ", 2));
            System.out.println("the mags is: " + mags);
            double mag = 0.0;
            if (mags.contains("?")) {
                mag = 0.0;
                System.err.println("unknown magnitude in data");
            } else {
                mag = Double.parseDouble(mags);
            }
            return mag;
        }
        return null;

    }

    @Override
    public boolean satisfies() {
        return false;
    }

}
