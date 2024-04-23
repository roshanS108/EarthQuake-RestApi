package com.rest.earthquakeapi.TestingCode;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class EarthQuakeParserTest {
    public EarthQuakeParserTest() {
        // TODO Auto-generated constructor stub
    }
    public static ArrayList<QuakeEntry> read(String source, int pageNumber, int pageSize) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Document document = builder.parse(new File(source));
            //Document document = builder.parse("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom");
            Document document = null;

            if (source.startsWith("http")){
                document = builder.parse(source);
                System.out.println("starts with http");
            }
            else {
                document = builder.parse(new File(source));
            }
            //Document document = builder.parse("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom");

            NodeList nodeList = document.getDocumentElement().getChildNodes();

            //calculate the index of the first entry to be shown on the current page
            int startIndex = pageNumber * pageSize;

            //calculate the index of the last entry to be shown on the current page,
            // ensuring that it does not exceed the total number of entries in the XMl document
            int endIndex = Math.min((pageNumber + 1) * pageSize, nodeList.getLength());

            //store all the field information
            ArrayList<QuakeEntry> list = new ArrayList<QuakeEntry>();


            for(int k=0; k < nodeList.getLength(); k++){
                if (k >= startIndex && k < endIndex) {
                    Node node = nodeList.item(k);

                    if (node.getNodeName().equals("entry")) {
                        Element elem = (Element) node;
                        NodeList t1 = elem.getElementsByTagName("georss:point");
                        NodeList t2 = elem.getElementsByTagName("title");
                        NodeList t3 = elem.getElementsByTagName("georss:elev");
                        double lat = 0.0, lon = 0.0, depth = 0.0;
                        String title = "NO INFORMATION";
                        double mag = 0.0;

                        if (t1 != null) {
                            String s2 = t1.item(0).getChildNodes().item(0).getNodeValue();
                            //System.out.print("point2: "+s2);
                            String[] args = s2.split(" ");
                            lat = Double.parseDouble(args[0]);
                            lon = Double.parseDouble(args[1]);
                        }
                        if (t2 != null) {
                            String s2 = t2.item(0).getChildNodes().item(0).getNodeValue();

                            String mags = s2.substring(2, s2.indexOf(" ", 2));
                            if (mags.contains("?")) {
                                mag = 0.0;
                                System.err.println("unknown magnitude in data");
                            } else {
                                mag = Double.parseDouble(mags);
                                //System.out.println("mag= "+mag);
                            }
                            int sp = s2.indexOf(" ", 5);
                            title = s2.substring(sp + 1);
                            if (title.startsWith("-")) {
                                int pos = title.indexOf(" ");
                                title = title.substring(pos + 1);
                                System.out.println("title is: " + title);
                            }
                        }
                        if (t3 != null) {
                            String s2 = t3.item(0).getChildNodes().item(0).getNodeValue();
                            depth = Double.parseDouble(s2);
                        }
                        QuakeEntry loc = new QuakeEntry(lat, lon, mag, title, depth);
//                    QuakeEntry title2 = new QuakeEntry(title, lat, lon);
//                    countryList.add(title2);
                        list.add(loc);
                    }

                }
            }
            return list;
        }
        catch (ParserConfigurationException pce){
            System.err.println("parser configuration exception");
        }
        catch (SAXException se){
            System.err.println("sax exception");
        }
        catch (IOException ioe){
            System.err.println("ioexception");
        }
        return null;
    }





}