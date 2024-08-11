package image.labeler.Pascal_VOC;

import image.labeler.Img;
import image.labeler.Polygon;
import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.List;

public class PascalVoc {

    private static String RoutePath;

    private PascalVoc(){}

    /**
     * this method open and parser with an external library Textflow for parser
     * file and make new image object
     * @param pascalfile
     * @return
     */

    public static Img openPascalvoc(String pascalfile){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
        }catch (ParserConfigurationException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * this method generate pascalvoc file
     * @param img
     */
    public static void pascalvocParser(Img img, String routePath) {
        PascalvocXml pascalvocXml = PascalvocXml.instance(img);
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(PascalvocXml.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            File pascalvoc = new File(routePath);
            marshaller.marshal(img, pascalvoc);
        }catch (JAXBException e){
            e.printStackTrace();
        }
    }

}

