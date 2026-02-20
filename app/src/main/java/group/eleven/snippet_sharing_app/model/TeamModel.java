package group.eleven.snippet_sharing_app.model;

public class TeamModel {
    private int id;
    private String actualId; // UUID string from API
    private String name;
    private int memberCount;
    private int colorHex; // Dynamic color for avatar background
    private String initials;
    private boolean isSelected;

    public TeamModel(int id, String name, int memberCount, int colorHex, String initials) {
        this.id = id;
        this.name = name;
        this.memberCount = memberCount;
        this.colorHex = colorHex;
        this.initials = initials;
        this.isSelected = false;
    }

    public int getId() {
        return id;
    }

    public String getActualId() {
        return actualId;
    }

    public void setActualId(String actualId) {
        this.actualId = actualId;
    }

    public String getName() {
        return name;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public int getColorHex() {
        return colorHex;
    }

    public String getInitials() {
        return initials;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
