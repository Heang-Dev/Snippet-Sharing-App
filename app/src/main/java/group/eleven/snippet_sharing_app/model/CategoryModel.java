package group.eleven.snippet_sharing_app.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private String id;
    private String name;
    private int snippetCount;
    private boolean isSelected;
    private int iconResId;
    private boolean isRecent;

    public CategoryModel(String id, String name, int snippetCount, int iconResId, boolean isRecent) {
        this.id = id;
        this.name = name;
        this.snippetCount = snippetCount;
        this.iconResId = iconResId;
        this.isRecent = isRecent;
        this.isSelected = false;
    }

    public boolean isRecent() {
        return isRecent;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSnippetCount() {
        return snippetCount;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
