package com.rest.earthquakeapi.ParserManager;

import com.rest.earthquakeapi.XMLParsing.*;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
public class EarthQuakeParser {
    public EarthQuakeParser() {
    }
    public ArrayList<QuakeEntry> read(String source) {
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
            ElementParser<String> titleParser = new TitleParser();
            ElementParser<String> dateTimeParser =  new DateTimeParser();
            ElementParser<Double> depthParser = new DepthParser();
            ElementParser<String> linkParser = new LinkParser();
            ElementParser<Double> magParser = new MagnitudeParser();


            for (int k = 0; k < nodeList.getLength(); k++) {
                Node node = nodeList.item(k);

                if (node.getNodeName().equals("entry")) {
                    Element elem = (Element) node;

                    //retrieves the latitude and longittude of earthquake.
                    NodeList t1 = elem.getElementsByTagName("georss:point");

                    double lat = 0.0, lon = 0.0, depth = 0.0;

                    if (t1 != null && t1.getLength()>0) {
                        String s2 = t1.item(0).getChildNodes().item(0).getNodeValue();
                        //System.out.print("point2: "+s2);
                        String[] args = s2.split(" ");
                        lat = Double.parseDouble(args[0]);
                        lon = Double.parseDouble(args[1]);
                    }

                    System.out.println("t1 is : " + lat + " " + lon);

                    String dateTime = dateTimeParser.parseElement(elem);
                    System.out.println("the dateTime is : " + dateTime);

                    //depth parser
                    depth = depthParser.parseElement(elem);

                    String id = idParser.parseElement(elem);

                    String link = linkParser.parseElement(elem);

                    double mag = magParser.parseElement(elem);

                    String title = titleParser.parseElement(elem);


                    QuakeEntry loc = new QuakeEntry(id,lat,lon,mag,title,depth,dateTime,link);
                    // Add the quakeEntry to the list
                    list.add(loc);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * A method specifically designed for pagination support
     * @param source      the source of earthquake data in XML format
     * @param pageNumber  the number of the page to retrieve
     * @param pageSize    the maximum number of earthquake entries per page
     * @return            a list of earthquake entries for the specified page
     */
    public ArrayList<QuakeEntry> getPaginatedEarthquakeData(String source, int pageNumber, int pageSize) {
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
            // get a list of all elements named 'entry' from XML document
            NodeList nodeList = document.getElementsByTagName("entry");

            // Get the total number of 'entry' elements found in the XML document.
            int totalEntries = nodeList.getLength();

            // calculate the starting index for the current page. this is determined by multiplying the page number by the number of items per page.
            int startIndex = pageNumber * pageSize;

            // Calculate the ending index for the current page. It is the smaller value between the start index plus
            // the page size and the total number of entries. This ensures we do not attempt to access beyond the available nodes.
            int endIndex = Math.min(startIndex + pageSize, totalEntries);

            // Create an instance of Parser object
            ElementParser<String> idParser = new IdParser();
            ElementParser<String> titleMagParser = new TitleParser();
            ElementParser<String> dateTimeParser =  new DateTimeParser();
            ElementParser<Double> depthParser = new DepthParser();
            ElementParser<String> linkParser = new LinkParser();
            ElementParser<Double> magParser = new MagnitudeParser();


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
    /**
     * Reads earthquake titles(country name) from a data source.
     *
     * @param source The location of the earthquake data source.
     * @return An ArrayList containing the titles of earthquake.
     */
    public ArrayList<String> readTitles(String source) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<String> titles = new ArrayList<>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            if (source.startsWith("http")) {
                document = builder.parse(source);
            } else {
                document = builder.parse(new File(source));
            }
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            // Create an instance of Parser object
            ElementParser<String> titleParser = new TitleParser();

            for (int k = 0; k < nodeList.getLength(); k++) {
                Node node = nodeList.item(k);

                if (node.getNodeName().equals("entry")) {
                    Element elem = (Element) node;
                    String newTitleData = titleParser.parseElement(elem);
                    titles.add(newTitleData);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return titles;
    }
}
