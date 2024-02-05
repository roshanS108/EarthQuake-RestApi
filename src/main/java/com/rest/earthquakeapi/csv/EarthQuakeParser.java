package com.rest.earthquakeapi.csv;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.rest.earthquakeapi.model.QuakeEntry;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EarthQuakeParser {
    public EarthQuakeParser() {

    }

    public ArrayList<QuakeEntry> read(String source) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Document document = builder.parse(new File(source));
            //Document document = builder.parse("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom");
            Document document = null;

            if (source.startsWith("http")){
                document = builder.parse(source);
            }
            else {
                document = builder.parse(new File(source));
            }
            //Document document = builder.parse("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom");

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            ArrayList<QuakeEntry> list = new ArrayList<QuakeEntry>();
            for(int k=0; k < nodeList.getLength(); k++){
                Node node = nodeList.item(k);

                if (node.getNodeName().equals("entry")) {
                    Element elem = (Element) node;
                    // Get a NodeList containing all elements with the tag name "georss:point" within the "entry" node.
                    NodeList t1 = elem.getElementsByTagName("georss:point");
                    NodeList t2 = elem.getElementsByTagName("title");
                    NodeList t3 = elem.getElementsByTagName("georss:elev");

                    NodeList idNode = elem.getElementsByTagName("id");

                    NodeList dateTime = elem.getElementsByTagName("summary");

//                    NodeList link = elem.getElementsByTagName("link").item(0).getAttributes().getNamedItem("href").getChildNodes();

                    double lat = 0.0, lon = 0.0, depth = 0.0;
                    String title = "NO INFORMATION";
                    String id = "";
                    String date = "";
                    double mag = 0.0;

                    if (t1 != null) {
                        String s2 = t1.item(0).getChildNodes().item(0).getNodeValue();
                        //System.out.print("point2: "+s2);
                        String[] args = s2.split(" ");
                        lat = Double.parseDouble(args[0]);
                        lon = Double.parseDouble(args[1]);
                    }
                    if (t2 != null){
                        String s2 = t2.item(0).getChildNodes().item(0).getNodeValue();
                        String mags = s2.substring(2,s2.indexOf(" ",2));
                        System.out.println("the mags is: " + mags);
                        if (mags.contains("?")) {
                            mag = 0.0;
                            System.err.println("unknown magnitude in data");
                        }
                        else {
                            mag = Double.parseDouble(mags);
                            //System.out.println("mag= "+mag);
                        }
                        int sp = s2.indexOf(" ",5); //6
                        title = s2.substring(sp+1);
                        System.out.println("the title is: " + title);
                        if (title.startsWith("-")){
                            int pos = title.indexOf(" ");
                            title = title.substring(pos+1);
                        }
                    }
                    if (t3 != null){
                        String s2 = t3.item(0).getChildNodes().item(0).getNodeValue();
                        depth = Double.parseDouble(s2);
                    }
                    if(idNode!=null){
                        String s2 = idNode.item(0).getChildNodes().item(0).getNodeValue();
                        id = s2.substring(s2.indexOf(':', s2.indexOf(':', s2.indexOf(':') + 1) + 1) + 1);
                    }

                    if (dateTime != null && dateTime.getLength() > 0) {
                        Node summaryNode = dateTime.item(0);
                        String summaryText = summaryNode.getTextContent();

                        // Replace &deg; entity with the degree symbol
                        summaryText = summaryText.replace("&deg;", "°");

                        // wrapping summaryText with a root element
                        summaryText = "<root>" + summaryText + "</root>";

                        // Initialize the DocumentBuilderFactory and configure it
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        try {
                            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                            DocumentBuilder db = dbf.newDocumentBuilder();
                            InputSource is = new InputSource(new StringReader(summaryText));
                            try {
                                Document summaryDoc = db.parse(is);
                                // Retrieve the <dl> element from the parsed summary
                                NodeList dlList = summaryDoc.getElementsByTagName("dl");
                                if (dlList != null && dlList.getLength() > 0) {
                                    Node dlNode = dlList.item(0); // Get the first <dl> element

                                    NodeList childNodes = dlNode.getChildNodes();
                                    for (int i = 0; i < childNodes.getLength(); i++) {
                                        Node child = childNodes.item(i);

                                        // Check if the child node is a <dd> element
                                        if (child.getNodeType() == Node.ELEMENT_NODE && "dd".equals(child.getNodeName())) {
                                             date = child.getTextContent().trim();
                                            break; // Break after finding the first <dd>
                                        }
                                    }
                                } else {
                                    System.out.println("No <dl> elements found within <summary>");
                                }
                            } catch (SAXException e) {
                                e.printStackTrace();
                                System.out.println("Error parsing summary text");
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("IO Error occurred");
                            }
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        }
                    }
                    // Create QuakeEntry object with new fields
//                    QuakeEntry loc = new QuakeEntry(id, mag, lat,lon,depth, title, dateTime, link);
                    QuakeEntry loc = new QuakeEntry(id,lat,lon,mag,title,depth,date);
                    list.add(loc);
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

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
        EarthQuakeParser xp = new EarthQuakeParser();
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