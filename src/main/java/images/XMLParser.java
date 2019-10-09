package images;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.*;

public class XMLParser {

    public HashMap<String,String> parse(String XMLString){

        Document doc = convertToXML(XMLString);
        HashMap<String,String> urlList = new HashMap<>();
        if (doc != null) {
            NodeList photos = doc.getElementsByTagName("photos");
            Element photosElement = (Element) photos.item(0);
            NodeList list = photosElement.getElementsByTagName("photo");
            for(int i=0;i<list.getLength();i++){
                String id = list.item(i).getAttributes().getNamedItem("id").getTextContent();
                String url = list.item(i).getAttributes().getNamedItem("url_m").getTextContent();
                urlList.put(id,url);

                //System.out.println(url);
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
