package group.eleven.snippet_sharing_app.data;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;

/**
 * Singleton/Static manager for handling snippets in memory
 */
public class SnippetManager {
    public static List<SnippetCard> allSnippets = new ArrayList<>();

    static {
        // Initial dummy data
        allSnippets.add(new SnippetCard(
                "Python CSV Parser",
                "Py",
                "Updated 2h ago",
                "import csv\ndef parse_csv(file_path):\n    with open(file_path, 'r') as f:\n        reader = csv.reader(f)\n        for row in reader:\n            print(row)",
                new String[]{"Data", "Utils"},
                R.color.error
        ));
        allSnippets.add(new SnippetCard(
                "Auth Hook",
                "React",
                "Updated yesterday",
                "export const useAuth = () => {\n  const {user, setUser} = useState(null);\n  \n  useEffect(() => {\n    // Auth logic\n  }, []);\n}",
                new String[]{"React", "Hooks"},
                R.color.info
        ));
    }
}
