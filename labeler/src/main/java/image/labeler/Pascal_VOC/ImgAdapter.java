package image.labeler.Pascal_VOC;

import image.labeler.Img;
import image.labeler.Polygon;

import java.util.ArrayList;

public class ImgAdapter {

    private ImgAdapter(){}

    public static Img adapter(PascalvocXml pascalvocXml){
        String id = pascalvocXml.getId();
        String name = pascalvocXml.getName();
        int width = pascalvocXml.getWidth();
        int height = pascalvocXml.getHeight();
        ArrayList<Polygon> polygon = (ArrayList<Polygon>) ObjectAdapter.toPolygon(pascalvocXml.getObjects());
        return new Img(name,id,width,height,polygon);
    }

}
