package image.labeler;

import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.math.BigInteger;

public class Img {
    private ArrayList<Polygon> polygons;
    private String fileName;
    private int width;
    private int height;
    private String id;
    private Date exportDate;

    public Img(String fileName, int width, int height) {
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.id = generateId(fileName);
        polygons = new ArrayList<>();
    }

    /**
     * MD5 hash function to generate unique id for image
     * @param input - file name
     * @return - unique id
     * @author Joshua Arrazola
     */
    private String generateId(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // MD5 hash function
            byte[] messageDigest = md.digest(input.getBytes()); // generate hash
            BigInteger no = new BigInteger(1, messageDigest); // convert byte array to signum representation
            String hashText = no.toString(16); // convert message digest to hex value
            while (hashText.length() < 32) // add leading zeros to make it 32 bit
                hashText = "0" + hashText;
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public String getFileName() {
        return fileName;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getId() {
        return id;
    }

    public Date getExportDate() {
        return exportDate;
    }

    public void setExportDate(Date exportDate) {
        this.exportDate = exportDate;
    }
}