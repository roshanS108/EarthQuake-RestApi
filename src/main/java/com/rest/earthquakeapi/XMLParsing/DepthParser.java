package com.rest.earthquakeapi.XMLParsing;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DepthParser implements ElementParser<Double>{

    @Override
    public Double parseElement(Element element) {

        NodeList t3 = element.getElementsByTagName("georss:elev");

        if (t3 != null && t3.getLength()>0){
            String s2 = t3.item(0).getChildNodes().item(0).getNodeValue();
            double depth = Double.parseDouble(s2);
            return depth;
        }
        return 0.0;
    }

    @Override
    public boolean satisfies() {
        return false;
    }
}
