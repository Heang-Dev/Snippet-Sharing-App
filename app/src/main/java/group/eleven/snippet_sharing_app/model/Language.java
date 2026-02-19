package group.eleven.snippet_sharing_app.model;

public class Language {
    private String name;
    private String mime;
    private String shortCode;
    private String colorHex; // Added for UI aesthetics
    private boolean isSelected;

    public Language(String name, String mime, String shortCode, String colorHex) {
        this.name = name;
        this.mime = mime;
        this.shortCode = shortCode;
        this.colorHex = colorHex;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public String getMime() {
        return mime;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getColorHex() {
        return colorHex;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
