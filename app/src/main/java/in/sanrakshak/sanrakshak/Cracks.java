package in.sanrakshak.sanrakshak;

import android.graphics.Bitmap;

public class Cracks {
    private String name,date;
    private Bitmap preview;
    Cracks(String name, String date, Bitmap preview) {
        this.name = name;
        this.date = date;
        this.preview = preview;
    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}
    public Bitmap getPreview() {return preview;}
    public void setPreview(Bitmap preview) {this.preview = preview;}
}