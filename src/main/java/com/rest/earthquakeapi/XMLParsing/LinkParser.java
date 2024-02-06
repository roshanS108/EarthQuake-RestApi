package com.rest.earthquakeapi.XMLParsing;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LinkParser implements ElementParser<String>{
    @Override
    public String parseElement(Element element) {

        String link = "";
        //retrieves the <link> element
        NodeList linkList = element.getElementsByTagName("link");

        if (linkList != null && linkList.getLength() > 0) {
            Node linkNode = linkList.item(0); // Get the first <link> element

            String hrefValue = ((Element) linkNode).getAttribute("href");
            link = hrefValue;
            System.out.println("The link is: " + link);
        }
        return link;

    }

}
