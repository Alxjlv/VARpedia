package models.images;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * XMLParser is used to process the response XML from flickr and return the URLs and ID's of the images that are returned
 * @author Tait & Alex
 */
class XMLParser { // TODO - Implement into ImageSearcher?

    /**
     * This is the main method used to retrieve the relevant information from the XML document. This information
     * includes the URL of the image, and the image id.
     * @param XMLString - this is the string returned from the HTTP request to Flickr
     * @return - null if the response was blank, a HashMap with URL as the key and the id as the value otherwise
     * @throws MalformedURLException - if the URL isn't valid
     */
    List<URL> parse(String XMLString) throws MalformedURLException {
        List<URL> urlList = new ArrayList<>();

        Document doc = convertToXML(XMLString); //Converting to parse-able XML
        if (doc != null) {
            //Traversing the XML document to get to the correct element
            NodeList photos = doc.getElementsByTagName("photos");
            Element photosElement = (Element) photos.item(0);
            NodeList list = photosElement.getElementsByTagName("photo");

            //Cycling through each photo item from the response
            for (int i = 0; i < list.getLength(); i++) {
                //Sometimes the URL field can be blank so we need to check for that
                if (list.item(i).getAttributes().getNamedItem("url_m") != null) {
                    String link = list.item(i).getAttributes().getNamedItem("url_m").getTextContent();
                    URL url = new URL(link);
                    urlList.add(url);
                }
            }
            return urlList;
        }
        return null;
    }

    /**
     * This method is used to convert a String XML object into a parse-able XML document
     * @param XMLString - A String containing XML
     * @return - if successful this will return a parse-able Document object
     */
    private Document convertToXML(String XMLString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(XMLString)));
        } catch (ParserConfigurationException|SAXException|IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
