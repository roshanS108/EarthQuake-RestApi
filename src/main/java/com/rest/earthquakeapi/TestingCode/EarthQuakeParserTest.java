package com.rest.earthquakeapi.TestingCode;

import com.rest.earthquakeapi.XMLParsing.ElementParser;
import com.rest.earthquakeapi.XMLParsing.IdParser;
import com.rest.earthquakeapi.csv.EarthQuakeParser;
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

            // Create an instance of IdParser
            ElementParser idParser = new IdParser();

            for (int k = 0; k < nodeList.getLength(); k++) {
                Node node = nodeList.item(k);

                if (node.getNodeName().equals("entry")) {
                    Element elem = (Element) node;
                    QuakeEntry quakeEntry = new QuakeEntry();

                    // Use IdParser to parse and set the ID
                    idParser.parseElement(elem, quakeEntry);

                    // ... other parsing logic here ...

                    // Add the quakeEntry to the list
                    list.add(quakeEntry);
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
