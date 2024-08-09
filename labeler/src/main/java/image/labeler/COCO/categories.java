package image.labeler.COCO;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class categories {
    String id;
    String name;

    /**
     * This constructor should be able to create a categorie object from the parameters
     * @param id
     * @param name
     */
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

    private String generateId(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // MD5 hash function
            byte[] messageDigest = md.digest(name.getBytes()); // generate hash
            BigInteger no = new BigInteger(1, messageDigest); // convert byte array to signum representation
            String hashText = no.toString(16); // convert message digest to hex value
            while (hashText.length() < 32) // add leading zeros to make it 32 bit
                hashText = "0" + hashText;
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}