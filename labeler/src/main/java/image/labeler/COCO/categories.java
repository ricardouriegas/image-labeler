package image.labeler.COCO;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class categories {
    String id;
    String name;

    public categories(String name) {
        this.id = generateId(name);
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String generateId(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); 
            byte[] messageDigest = md.digest(name.getBytes()); 
            BigInteger no = new BigInteger(1, messageDigest); 
            String hashText = no.toString(16); 
            while (hashText.length() < 32)
                hashText = "0" + hashText;
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "categories{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
