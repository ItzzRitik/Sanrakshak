package in.sanrakshak.sanrakshak;

import android.graphics.Bitmap;

public class Cracks {
    private String name,intensity,date,preview;
    Cracks(String name,String intensity, String date, String preview) {
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
    public String getPreview() {return preview;}
    public void setPreview(String preview) {this.preview = preview;}
}