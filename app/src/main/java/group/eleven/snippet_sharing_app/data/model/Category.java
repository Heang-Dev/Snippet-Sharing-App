package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Category model for snippet categories
 * Matches backend Category model
 */
public class Category {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("description")
    private String description;

    @SerializedName("parent_category_id")
    private String parentCategoryId;

    @SerializedName("icon")
    private String icon;

    @SerializedName("color")
    private String color;

    @SerializedName("order")
    private int order;

    @SerializedName("snippet_count")
    private int snippetCount;

    @SerializedName("snippets_count")
    private int snippetsCount;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("children")
    private java.util.List<Category> children;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public int getOrder() {
        return order;
    }

    public int getSnippetCount() {
        return snippetCount;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setSnippetCount(int snippetCount) {
        this.snippetCount = snippetCount;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getSnippetsCount() {
        // Return whichever is populated
        return snippetsCount > 0 ? snippetsCount : snippetCount;
    }

    public void setSnippetsCount(int snippetsCount) {
        this.snippetsCount = snippetsCount;
    }

    public java.util.List<Category> getChildren() {
        return children;
    }

    public void setChildren(java.util.List<Category> children) {
        this.children = children;
    }
}
