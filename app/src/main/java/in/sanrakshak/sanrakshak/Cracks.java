package in.sanrakshak.sanrakshak;

import android.graphics.Bitmap;

public class Cracks {
    private String name,intensity,date;
    private Bitmap preview;
    Cracks(String name,String intensity, String date, Bitmap preview) {
        this.name = name;
        this.intensity = intensity;
        this.date = date;
        this.preview = preview;
    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getIntensity() {return intensity;}
    public void setIntensity(String intensity) {this.intensity = intensity;}
    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}
    public Bitmap getPreview() {return preview;}
    public void setPreview(Bitmap preview) {this.preview = preview;}
}