package models.images;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class XMLParser {

    Map<URL,File> parse(String XMLString) throws MalformedURLException {
        File imageFolder = new File(".images/");
        Document doc = convertToXML(XMLString);
        Map<URL,File> urlList = new HashMap<>();
        if (doc != null) {
            NodeList photos = doc.getElementsByTagName("photos");
            Element photosElement = (Element) photos.item(0);
            NodeList list = photosElement.getElementsByTagName("photo");
            for(int i=0;i<list.getLength();i++){
                String id = list.item(i).getAttributes().getNamedItem("id").getTextContent();
                File image = new File(imageFolder,id+".jpg");
                if(list.item(i).getAttributes().getNamedItem("url_m")!=null){
                    String link = list.item(i).getAttributes().getNamedItem("url_m").getTextContent();
                    System.out.println("link number: " + i + " " +link);
                    URL url = new URL(link);
                    urlList.put(url,image);
                }
            }
            return urlList;
        }
        return null;
    }

    private Document convertToXML(String XMLString){
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
