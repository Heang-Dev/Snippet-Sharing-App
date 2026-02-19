package group.eleven.snippet_sharing_app.data;

import java.util.ArrayList;
import java.util.List;
import group.eleven.snippet_sharing_app.model.SnippetModel;

public class SnippetRepository {
    private static SnippetRepository instance;
    private List<SnippetModel> allSnippets;

    private SnippetRepository() {
        allSnippets = new ArrayList<>();
        // Initialize with default mock data
        allSnippets.add(new SnippetModel("1", "Date Formatter Utility", "JavaScript", "#F7DF1E", "Public", "v1.2.0",
                "Modified 2h ago", false,
                "function formatDate(date) {\n  return new Intl.DateTimeFormat('en-US').format(date);\n}"));
        allSnippets.add(new SnippetModel("2", "API Retry Logic", "Python", "#3776AB", "Private", "v1.0.1",
                "Modified 1d ago", true,
                "import time\n\ndef retry_request(url, retries=3):\n    for i in range(retries):\n        try:\n            response = requests.get(url)\n            return response\n        except Exception:\n            time.sleep(1)"));
        allSnippets.add(
                new SnippetModel("3", "Flexbox Centering", "CSS", "#E34F26", "Team", "v2.1.0", "Modified 3d ago", false,
                        ".container {\n  display: flex;\n  justify-content: center;\n  align-items: center;\n}"));
    }

    public static synchronized SnippetRepository getInstance() {
        if (instance == null) {
            instance = new SnippetRepository();
        }
        return instance;
    }

    public List<SnippetModel> getAllSnippets() {
        return allSnippets;
    }

    public List<SnippetModel> getRecentSnippets() {
        return allSnippets;
    }

    public void addSnippet(SnippetModel snippet) {
        allSnippets.add(0, snippet); // Add to front
    }

    public void deleteSnippet(SnippetModel snippet) {
        allSnippets.remove(snippet);
    }

    public void deleteSnippets(List<SnippetModel> snippets) {
        allSnippets.removeAll(snippets);
    }

    public void updateSnippet(SnippetModel updatedSnippet) {
        for (int i = 0; i < allSnippets.size(); i++) {
            if (allSnippets.get(i).getId().equals(updatedSnippet.getId())) {
                allSnippets.set(i, updatedSnippet);
                return;
            }
        }
    }
}
