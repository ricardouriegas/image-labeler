package image.labeler.Pascal_VOC;

import image.labeler.Img;
import jakarta.xml.bind.*;
import java.io.File;

public class PascalVoc {
    private static String RoutePath;
    private PascalVoc(){}

    /**
     * this method open and parser with an external library Textflow for parser
     * file and make new image object
     * @param file
     * @return
     */

    public static Img openPascalvoc(File file){
        Img img = null;
        try {
            JAXBContext pojo = JAXBContext.newInstance(PascalvocXml.class);
            Unmarshaller unmarshaller = pojo.createUnmarshaller();
            img = ImgAdapter.adapter( (PascalvocXml) unmarshaller.unmarshal(file) );
        }catch (JAXBException e){
            e.printStackTrace();
        }
        return img;
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
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File pascalvoc = new File(routePath);
            marshaller.marshal(pascalvocXml, pascalvoc);
        }catch (JAXBException e){
            e.printStackTrace();
        }
    }

}

