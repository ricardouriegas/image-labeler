package image.labeler.Pascal_VOC;

import image.labeler.Img;
import junit.framework.TestCase;

public class PascalVocTest extends TestCase {

    public void testOpenPascalvoc() {
    }

    public void testPascalvocParser() {
        Img img = new Img("sonic",800,600,12);
        PascalVoc.pascalvocParser(img,"src/main/java/image/labeler/Pascal_VOC/Figure.xml");
    }
}