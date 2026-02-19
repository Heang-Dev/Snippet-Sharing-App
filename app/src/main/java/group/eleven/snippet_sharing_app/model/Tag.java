package group.eleven.snippet_sharing_app.model;

public class Tag {
    private String name;
    private int snippetCount;

    public Tag(String name, int snippetCount) {
        this.name = name;
        this.snippetCount = snippetCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSnippetCount() {
        return snippetCount;
    }

    public void setSnippetCount(int snippetCount) {
        this.snippetCount = snippetCount;
    }
}
