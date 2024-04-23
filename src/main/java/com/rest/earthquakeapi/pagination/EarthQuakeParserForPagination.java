package com.rest.earthquakeapi.pagination;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.rest.earthquakeapi.XMLParsing.*;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
public  class EarthQuakeParserForPagination {
    public static ArrayList<QuakeEntry> read(String source, int pageNumber, int pageSize) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<QuakeEntry> list = new ArrayList<QuakeEntry>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
            if (source.startsWith("http")) {
                document = builder.parse(source);
            } else {
                document = builder.parse(new File(source));
            }
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            // Create an instance of Parser object
            ElementParser<String> idParser = new IdParser();
            ElementParser<String> titleMagParser = new TitleParser();
            ElementParser<String> dateTimeParser =  new DateTimeParser();
            ElementParser<Double> depthParser = new DepthParser();
            ElementParser<String> linkParser = new LinkParser();
            ElementParser<Double> magParser = new MagnitudeParser();

            //calculate the index of the first entry to be shown on the current page
            int startIndex = pageNumber * pageSize;

            //calculate the index of the last entry to be shown on the current page,
            // ensuring that it does not exceed the total number of entries in the XMl document
            int endIndex = Math.min((pageNumber + 1) * pageSize, nodeList.getLength());

            for (int k = 0; k < nodeList.getLength(); k++) {
                if (k >= startIndex && k < endIndex) {
                    Node node = nodeList.item(k);

                    if (node.getNodeName().equals("entry")) {
                        Element elem = (Element) node;

                        //retrieves the latitude and longittude of earthquake.
                        NodeList t1 = elem.getElementsByTagName("georss:point");

                        double lat = 0.0, lon = 0.0, depth = 0.0;

                        if (t1 != null && t1.getLength() > 0) {
                            String s2 = t1.item(0).getChildNodes().item(0).getNodeValue();
                            //System.out.print("point2: "+s2);
                            String[] args = s2.split(" ");
                            lat = Double.parseDouble(args[0]);
                            lon = Double.parseDouble(args[1]);
                        }
                        System.out.println("t1 is : " + lat + " " + lon);
                        String dateTime = (String) dateTimeParser.parseElement(elem);
                        System.out.println("the dateTime is : " + dateTime);
                        //depth parser
                        depth = depthParser.parseElement(elem);

                        String id = idParser.parseElement(elem);

                        String link = linkParser.parseElement(elem);

                        double mag = magParser.parseElement(elem);

                        String title = titleMagParser.parseElement(elem);

                        QuakeEntry loc = new QuakeEntry(id, lat, lon, mag, title, depth, dateTime, link);
                        // Add the quakeEntry to the list
                        list.add(loc);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
