package com.rest.earthquakeapi.XMLParsing;
import java.util.*;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IdParser implements ElementParser<String>{

    @Override
    public String parseElement(Element element) {
        NodeList idNode = element.getElementsByTagName("id");

        if(idNode!=null && idNode.getLength()>0){
            String s2 = idNode.item(0).getChildNodes().item(0).getNodeValue();
            String id = s2.substring(s2.indexOf(':', s2.indexOf(':', s2.indexOf(':') + 1) + 1) + 1);

            return id;
        }
        return null;
    }

    @Override
    public boolean satisfies() {
        return false;
    }
}
