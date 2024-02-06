package com.rest.earthquakeapi.XMLParsing;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
public class LongitudeLatitudeParser implements ElementParser<Double>{
    private double latitude;
    private double longitude;

    @Override
    public Double parseElement(Element element) {
        NodeList t1 = element.getElementsByTagName("georss:point");

        if (t1 != null && t1.getLength()>0) {
            String s2 = t1.item(0).getChildNodes().item(0).getNodeValue();
            //System.out.print("point2: "+s2);
            String[] args = s2.split(" ");
            double lat = Double.parseDouble(args[0]);
            double lon = Double.parseDouble(args[1]);

            // Combining lat and lon into a single double value
            double combinedValue = lat + lon / 1000.0;
            return combinedValue;
        }
        return 0.0;
    }





}
