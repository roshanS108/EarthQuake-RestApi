package com.rest.earthquakeapi.TestingCode;

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

public class EarthQuakeParserTest {
    public EarthQuakeParserTest() {
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
            ElementParser<String> titleMagParser = new TitleParser();
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



                    String dateTime = (String) dateTimeParser.parseElement(elem);
                    System.out.println("the dateTime is : " + dateTime);

                    //depth parser
                    depth = depthParser.parseElement(elem);

                    String id = idParser.parseElement(elem);

                    String link = linkParser.parseElement(elem);

                    double mag = magParser.parseElement(elem);

                    String title = titleMagParser.parseElement(elem);


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

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
        EarthQuakeParserTest xp = new EarthQuakeParserTest();
        //String source = "data/2.5_week.atom";
        //String source = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "data/nov20quakedatasmall.atom";
        String data ="M 1.7 - 75km WSW of Cantwell, Alaska";
        ArrayList<QuakeEntry> list  = xp.read(source);
        Collections.sort(list);
        for(QuakeEntry loc : list){
            System.out.println(loc);
        }
        System.out.println("# quakes = "+list.size());


    }


}