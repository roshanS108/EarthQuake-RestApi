package com.rest.earthquakeapi.XMLParsing;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TitleParser implements ElementParser {

    public TitleParser() {

    }

    @Override
    public String parseElement(Element element) {

        //retrieves the "title" info of the place where earth quake occurred. ex --->75km WSW of Cantwell, Alaska
        NodeList t2 = element.getElementsByTagName("title");

        String s2 = t2.item(0).getChildNodes().item(0).getNodeValue();

        int sp = s2.indexOf(" ", 5); //6
        String title = s2.substring(sp + 1);
        System.out.println("the title is: " + title);
        if (title.startsWith("-")) {
            int pos = title.indexOf(" ");
            title = title.substring(pos + 1);
        }
        return title;
    }

    @Override
    public boolean satisfies() {
        return false;
    }


}




