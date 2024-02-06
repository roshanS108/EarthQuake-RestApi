package com.rest.earthquakeapi.XMLParsing;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TitleMagParser implements ElementParser{

    public TitleMagParser(){

    }
    @Override
    public void parseElement(Element element, QuakeEntry quakeEntry) {

        //retrieves the "title" info of the place where earth quake occurred. ex --->75km WSW of Cantwell, Alaska
        NodeList t2 = element.getElementsByTagName("title");

        if (t2 != null && t2.getLength()>0){
            String s2 = t2.item(0).getChildNodes().item(0).getNodeValue();
            String mags = s2.substring(2,s2.indexOf(" ",2));
            System.out.println("the mags is: " + mags);
            double mag = 0.0;
            if (mags.contains("?")) {
                mag = 0.0;
                System.err.println("unknown magnitude in data");
            }
            else {
                mag = Double.parseDouble(mags);
                //System.out.println("mag= "+mag);
            }
            int sp = s2.indexOf(" ",5); //6
            String title = s2.substring(sp + 1);
            System.out.println("the title is: " + title);
            if (title.startsWith("-")){
                int pos = title.indexOf(" ");
                title = title.substring(pos+1);
            }
        }
    }

    @Override
    public boolean satisfies() {
        return false;
    }

    @Override
    public Object getParsedData() {
        return new TitleMagParser(la)
    }
}
