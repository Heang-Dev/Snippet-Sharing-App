package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Tag model for snippet tags
 * Matches backend Tag model
 */
public class Tag {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("color")
    private String color;

    @SerializedName("usage_count")
    private int usageCount;

    // Default constructor for Gson
    public Tag() {}

    // Constructor for local use
    public Tag(String name, int usageCount) {
        this.name = name;
        this.usageCount = usageCount;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public int getUsageCount() {
        return usageCount;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    /**
     * Get formatted usage count string
     */
    public String getFormattedUsageCount() {
        if (usageCount == 0) return "No snippets";
        if (usageCount == 1) return "1 snippet";
        return usageCount + " snippets";
    }
}
