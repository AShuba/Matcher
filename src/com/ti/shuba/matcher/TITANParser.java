package com.ti.shuba.matcher;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * TITAN system test description file parser
 * @author Anatoliy Shuba, created  Mar 26, 2012
 */
public class TITANParser {

    public TITANParser() {
    }
    /**
     * 
     * @param in file to parse
     * @return  Return hash map that represents all tests from the XML file
     *          Key - testID string, value - Test object
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public HashMap<String, Test> parse(File in) throws ParserConfigurationException, SAXException, IOException {
        HashMap<String, Test> hm = new HashMap<String, Test>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(in);
        Element root = document.getDocumentElement();
        //get a nodelist of <event> elements
        NodeList nl = root.getElementsByTagName("test");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                //get the event element
                Element el = (Element) nl.item(i);

                //get the event object
                Test e = getTest(el);

                //add it to list
                hm.put(e.getTestID(), e);
            }
        }
        return hm;
    }
    
    private Test getTest(Element el) {
        Test test = new Test();
        //String testID = getTextValue(el, "id");
        String testID = el.getAttribute("id");
        test.setTestID(testID);
        NodeList info = el.getElementsByTagName("information");
        test.setAuthor(getTextValue((Element)info.item(0), "autor"));
        test.setDescription(getTextValue((Element)info.item(0), "description"));
        test.setDateofcreation(getTextValue((Element)info.item(0), "dateofcreation"));
        return test;
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }
    
}
