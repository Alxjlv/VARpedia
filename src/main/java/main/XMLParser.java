package main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {

    public List<String> parse(String XMLString){

        Document doc = convertToXML(XMLString);
        List<String> urlList = new ArrayList<>();
        if (doc != null) {
            NodeList photos = doc.getElementsByTagName("photos");
            Element photosElement = (Element) photos.item(0);
            NodeList list = photosElement.getElementsByTagName("photo");
            for(int i=0;i<list.getLength();i++){
                String url = list.item(i).getAttributes().getNamedItem("url_m").getTextContent();
                urlList.add(url);
                System.out.println(url);
            }
            return urlList;
        }
        return null;
    }

    public Document convertToXML(String XMLString){
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(XMLString)));
        } catch (ParserConfigurationException|SAXException|IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
