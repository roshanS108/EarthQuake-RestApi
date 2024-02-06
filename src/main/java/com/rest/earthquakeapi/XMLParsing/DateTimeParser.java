package com.rest.earthquakeapi.XMLParsing;
import com.rest.earthquakeapi.model.QuakeEntry;
import org.springframework.util.FastByteArrayOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class DateTimeParser implements ElementParser<String>{
    @Override
    public String parseElement(Element element) {

        //retrieving the dateTime from <summary> element
        NodeList dateTime = element.getElementsByTagName("summary");
        String date = "";

        if (dateTime != null && dateTime.getLength() > 0) {
            Node summaryNode = dateTime.item(0);
            String summaryText = summaryNode.getTextContent();

            // replace &deg; entity with the degree symbol
            summaryText = summaryText.replace("&deg;", "°");

            // wrapping summaryText with a root element
            summaryText = "<root>" + summaryText + "</root>";

            System.out.println("summary text is: " + summaryText);

            // init the DocumentBuilderFactory and configure it
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
                        //returns the date from <dd> element
                        return date;
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
        //otherwise return null
        return null;

    }

    @Override
    public boolean satisfies() {
        return false;
    }
}
