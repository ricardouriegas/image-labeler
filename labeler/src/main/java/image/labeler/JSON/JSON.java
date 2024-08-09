package image.labeler.JSON;

import java.util.ArrayList;
import com.google.gson.Gson;
import image.labeler.*;

public class JSON {
    private final Gson gson = new Gson();

    public void toJson(ArrayList<Img> imgs){
        String json = gson.toJson(imgs);
        System.out.println(json);
    }

}
