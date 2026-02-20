package group.eleven.snippet_sharing_app.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;
import group.eleven.snippet_sharing_app.data.model.NotificationItem;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.model.User;

/**
 * Provides realistic mock data for testing the app without API connection.
 * Use this class when the backend is unavailable or for UI testing.
 */
public class MockDataProvider {

    private static final Random random = new Random();

    // ==================== AVATAR URLs ====================
    // Using reliable placeholder services for realistic avatars
    private static final String[] AVATAR_URLS = {
            "https://i.pravatar.cc/150?u=john_dev",
            "https://i.pravatar.cc/150?u=sarah_codes",
            "https://i.pravatar.cc/150?u=mike_js",
            "https://i.pravatar.cc/150?u=emma_py",
            "https://i.pravatar.cc/150?u=alex_kotlin",
            "https://i.pravatar.cc/150?u=lisa_react",
            "https://i.pravatar.cc/150?u=david_go",
            "https://i.pravatar.cc/150?u=nina_rust",
            "https://i.pravatar.cc/150?u=chris_swift",
            "https://i.pravatar.cc/150?u=amy_flutter"
    };

    // ==================== USER DATA ====================
    private static final String[][] USER_DATA = {
            // {id, username, email, fullName, bio, avatarIndex}
            {"u1", "john_dev", "john@example.com", "John Developer", "Full-stack developer | React & Node.js enthusiast"},
            {"u2", "sarah_codes", "sarah@example.com", "Sarah Chen", "Senior iOS Developer @ Apple | Swift lover"},
            {"u3", "mike_js", "mike@example.com", "Mike Johnson", "JavaScript wizard | Open source contributor"},
            {"u4", "emma_py", "emma@example.com", "Emma Watson", "Python developer | AI/ML researcher"},
            {"u5", "alex_kotlin", "alex@example.com", "Alex Kim", "Android developer | Kotlin advocate"},
            {"u6", "lisa_react", "lisa@example.com", "Lisa Park", "Frontend specialist | Design systems"},
            {"u7", "david_go", "david@example.com", "David Smith", "Backend engineer | Go & Kubernetes"},
            {"u8", "nina_rust", "nina@example.com", "Nina Brown", "Systems programmer | Rust evangelist"},
            {"u9", "chris_swift", "chris@example.com", "Chris Taylor", "iOS/macOS developer | SwiftUI expert"},
            {"u10", "amy_flutter", "amy@example.com", "Amy Lee", "Mobile developer | Flutter & Dart"}
    };

    // ==================== LANGUAGE DATA ====================
    public static class LanguageInfo {
        public final String name;
        public final String color;
        public final String icon;

        public LanguageInfo(String name, String color, String icon) {
            this.name = name;
            this.color = color;
            this.icon = icon;
        }
    }

    public static final LanguageInfo[] LANGUAGES = {
            new LanguageInfo("JavaScript", "#F7DF1E", "js"),
            new LanguageInfo("TypeScript", "#3178C6", "ts"),
            new LanguageInfo("Python", "#3776AB", "py"),
            new LanguageInfo("Java", "#ED8B00", "java"),
            new LanguageInfo("Kotlin", "#7F52FF", "kt"),
            new LanguageInfo("Swift", "#FA7343", "swift"),
            new LanguageInfo("Go", "#00ADD8", "go"),
            new LanguageInfo("Rust", "#DEA584", "rs"),
            new LanguageInfo("C++", "#00599C", "cpp"),
            new LanguageInfo("PHP", "#777BB4", "php"),
            new LanguageInfo("Ruby", "#CC342D", "rb"),
            new LanguageInfo("Dart", "#0175C2", "dart"),
            new LanguageInfo("SQL", "#E38C00", "sql"),
            new LanguageInfo("HTML", "#E34F26", "html"),
            new LanguageInfo("CSS", "#1572B6", "css")
    };

    // ==================== CODE SNIPPETS ====================
    public static class CodeSnippet {
        public final String title;
        public final String description;
        public final String code;
        public final String language;
        public final String[] tags;

        public CodeSnippet(String title, String description, String code, String language, String... tags) {
            this.title = title;
            this.description = description;
            this.code = code;
            this.language = language;
            this.tags = tags;
        }
    }

    public static final CodeSnippet[] CODE_SNIPPETS = {
            // JavaScript snippets
            new CodeSnippet(
                    "React Custom Hook for API Calls",
                    "A reusable hook for handling API calls with loading and error states",
                    "import { useState, useEffect } from 'react';\n\nfunction useApi(url) {\n  const [data, setData] = useState(null);\n  const [loading, setLoading] = useState(true);\n  const [error, setError] = useState(null);\n\n  useEffect(() => {\n    fetch(url)\n      .then(res => res.json())\n      .then(setData)\n      .catch(setError)\n      .finally(() => setLoading(false));\n  }, [url]);\n\n  return { data, loading, error };\n}",
                    "JavaScript",
                    "react", "hooks", "api"
            ),
            new CodeSnippet(
                    "Debounce Function Implementation",
                    "Optimizes performance by limiting function calls",
                    "function debounce(func, wait) {\n  let timeout;\n  return function executedFunction(...args) {\n    const later = () => {\n      clearTimeout(timeout);\n      func(...args);\n    };\n    clearTimeout(timeout);\n    timeout = setTimeout(later, wait);\n  };\n}\n\n// Usage\nconst debouncedSearch = debounce(search, 300);",
                    "JavaScript",
                    "utility", "performance"
            ),
            new CodeSnippet(
                    "Array Shuffle (Fisher-Yates)",
                    "Properly shuffles array elements randomly",
                    "function shuffle(array) {\n  const arr = [...array];\n  for (let i = arr.length - 1; i > 0; i--) {\n    const j = Math.floor(Math.random() * (i + 1));\n    [arr[i], arr[j]] = [arr[j], arr[i]];\n  }\n  return arr;\n}",
                    "JavaScript",
                    "algorithm", "array"
            ),

            // TypeScript snippets
            new CodeSnippet(
                    "TypeScript Generic API Response",
                    "Type-safe API response wrapper with generics",
                    "interface ApiResponse<T> {\n  data: T;\n  status: 'success' | 'error';\n  message?: string;\n  timestamp: number;\n}\n\nasync function fetchData<T>(url: string): Promise<ApiResponse<T>> {\n  const response = await fetch(url);\n  const data = await response.json();\n  return {\n    data,\n    status: 'success',\n    timestamp: Date.now()\n  };\n}",
                    "TypeScript",
                    "typescript", "generics", "api"
            ),
            new CodeSnippet(
                    "TypeScript Utility Types",
                    "Common utility type patterns for TypeScript",
                    "// Make all properties optional\ntype Partial<T> = { [P in keyof T]?: T[P] };\n\n// Make all properties required\ntype Required<T> = { [P in keyof T]-?: T[P] };\n\n// Pick specific properties\ntype Pick<T, K extends keyof T> = { [P in K]: T[P] };\n\n// Omit specific properties\ntype Omit<T, K extends keyof any> = Pick<T, Exclude<keyof T, K>>;",
                    "TypeScript",
                    "typescript", "types"
            ),

            // Python snippets
            new CodeSnippet(
                    "Python Async Context Manager",
                    "Async context manager for database connections",
                    "import asyncio\nfrom contextlib import asynccontextmanager\n\n@asynccontextmanager\nasync def get_db_connection():\n    conn = await asyncio.create_connection()\n    try:\n        yield conn\n    finally:\n        await conn.close()\n\n# Usage\nasync with get_db_connection() as conn:\n    result = await conn.execute(query)",
                    "Python",
                    "python", "async", "database"
            ),
            new CodeSnippet(
                    "Python Decorator with Arguments",
                    "Create decorators that accept parameters",
                    "from functools import wraps\n\ndef retry(max_attempts=3, delay=1):\n    def decorator(func):\n        @wraps(func)\n        def wrapper(*args, **kwargs):\n            for attempt in range(max_attempts):\n                try:\n                    return func(*args, **kwargs)\n                except Exception as e:\n                    if attempt == max_attempts - 1:\n                        raise\n                    time.sleep(delay)\n        return wrapper\n    return decorator",
                    "Python",
                    "python", "decorator", "retry"
            ),
            new CodeSnippet(
                    "FastAPI CRUD Endpoint",
                    "Complete CRUD operations with FastAPI",
                    "from fastapi import FastAPI, HTTPException\nfrom pydantic import BaseModel\n\napp = FastAPI()\n\nclass Item(BaseModel):\n    name: str\n    price: float\n\nitems = {}\n\n@app.post(\"/items/{id}\")\ndef create_item(id: str, item: Item):\n    items[id] = item\n    return item\n\n@app.get(\"/items/{id}\")\ndef get_item(id: str):\n    if id not in items:\n        raise HTTPException(404)\n    return items[id]",
                    "Python",
                    "fastapi", "api", "crud"
            ),

            // Kotlin snippets
            new CodeSnippet(
                    "Kotlin Flow with StateFlow",
                    "Reactive state management with Kotlin Flow",
                    "class UserRepository {\n    private val _users = MutableStateFlow<List<User>>(emptyList())\n    val users: StateFlow<List<User>> = _users.asStateFlow()\n\n    suspend fun loadUsers() {\n        val result = apiService.getUsers()\n        _users.value = result\n    }\n\n    fun observeUsers(): Flow<List<User>> = users\n        .map { it.filter { user -> user.isActive } }\n        .distinctUntilChanged()\n}",
                    "Kotlin",
                    "kotlin", "flow", "coroutines"
            ),
            new CodeSnippet(
                    "Kotlin Extension Functions",
                    "Useful extension functions for Android",
                    "fun View.show() { visibility = View.VISIBLE }\nfun View.hide() { visibility = View.GONE }\nfun View.invisible() { visibility = View.INVISIBLE }\n\nfun String.isValidEmail(): Boolean =\n    Patterns.EMAIL_ADDRESS.matcher(this).matches()\n\nfun Context.toast(message: String) =\n    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()\n\nfun Fragment.hideKeyboard() {\n    view?.let { activity?.hideKeyboard(it) }\n}",
                    "Kotlin",
                    "kotlin", "android", "extensions"
            ),
            new CodeSnippet(
                    "Kotlin Sealed Class for UI State",
                    "Type-safe UI state management",
                    "sealed class UiState<out T> {\n    object Loading : UiState<Nothing>()\n    data class Success<T>(val data: T) : UiState<T>()\n    data class Error(val message: String) : UiState<Nothing>()\n}\n\n// Usage in ViewModel\nfun loadData() {\n    viewModelScope.launch {\n        _state.value = UiState.Loading\n        try {\n            val data = repository.getData()\n            _state.value = UiState.Success(data)\n        } catch (e: Exception) {\n            _state.value = UiState.Error(e.message ?: \"Unknown error\")\n        }\n    }\n}",
                    "Kotlin",
                    "kotlin", "android", "mvvm"
            ),

            // Swift snippets
            new CodeSnippet(
                    "Swift Combine Publisher",
                    "Custom Combine publisher for network requests",
                    "func fetchUsers() -> AnyPublisher<[User], Error> {\n    URLSession.shared\n        .dataTaskPublisher(for: URL(string: apiURL)!)\n        .map(\\.data)\n        .decode(type: [User].self, decoder: JSONDecoder())\n        .receive(on: DispatchQueue.main)\n        .eraseToAnyPublisher()\n}\n\n// Usage\ncancellable = fetchUsers()\n    .sink(receiveCompletion: { _ in },\n          receiveValue: { users in\n              self.users = users\n          })",
                    "Swift",
                    "swift", "combine", "networking"
            ),
            new CodeSnippet(
                    "SwiftUI Custom View Modifier",
                    "Reusable view modifiers in SwiftUI",
                    "struct CardStyle: ViewModifier {\n    func body(content: Content) -> some View {\n        content\n            .padding()\n            .background(Color.white)\n            .cornerRadius(12)\n            .shadow(radius: 4)\n    }\n}\n\nextension View {\n    func cardStyle() -> some View {\n        modifier(CardStyle())\n    }\n}\n\n// Usage\nText(\"Hello\").cardStyle()",
                    "Swift",
                    "swift", "swiftui", "ui"
            ),

            // Go snippets
            new CodeSnippet(
                    "Go HTTP Middleware",
                    "Chainable middleware pattern in Go",
                    "type Middleware func(http.Handler) http.Handler\n\nfunc Logger(next http.Handler) http.Handler {\n    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {\n        start := time.Now()\n        next.ServeHTTP(w, r)\n        log.Printf(\"%s %s %v\", r.Method, r.URL.Path, time.Since(start))\n    })\n}\n\nfunc Chain(h http.Handler, middlewares ...Middleware) http.Handler {\n    for _, m := range middlewares {\n        h = m(h)\n    }\n    return h\n}",
                    "Go",
                    "go", "http", "middleware"
            ),
            new CodeSnippet(
                    "Go Error Handling Pattern",
                    "Structured error handling with wrapping",
                    "type AppError struct {\n    Code    int\n    Message string\n    Err     error\n}\n\nfunc (e *AppError) Error() string {\n    return fmt.Sprintf(\"%s: %v\", e.Message, e.Err)\n}\n\nfunc (e *AppError) Unwrap() error {\n    return e.Err\n}\n\nfunc NewError(code int, msg string, err error) *AppError {\n    return &AppError{Code: code, Message: msg, Err: err}\n}",
                    "Go",
                    "go", "errors"
            ),

            // Rust snippets
            new CodeSnippet(
                    "Rust Result Error Handling",
                    "Idiomatic error handling in Rust",
                    "use std::fs::File;\nuse std::io::{self, Read};\n\nfn read_file(path: &str) -> Result<String, io::Error> {\n    let mut file = File::open(path)?;\n    let mut contents = String::new();\n    file.read_to_string(&mut contents)?;\n    Ok(contents)\n}\n\n// With custom error type\n#[derive(Debug)]\nenum AppError {\n    Io(io::Error),\n    Parse(String),\n}",
                    "Rust",
                    "rust", "errors"
            ),
            new CodeSnippet(
                    "Rust Async with Tokio",
                    "Async HTTP server with Tokio",
                    "use tokio::net::TcpListener;\n\n#[tokio::main]\nasync fn main() -> Result<(), Box<dyn std::error::Error>> {\n    let listener = TcpListener::bind(\"127.0.0.1:8080\").await?;\n    \n    loop {\n        let (socket, _) = listener.accept().await?;\n        tokio::spawn(async move {\n            handle_connection(socket).await;\n        });\n    }\n}",
                    "Rust",
                    "rust", "async", "tokio"
            ),

            // SQL snippets
            new CodeSnippet(
                    "SQL Window Functions",
                    "Advanced analytics with window functions",
                    "SELECT \n    employee_id,\n    department,\n    salary,\n    AVG(salary) OVER (PARTITION BY department) as dept_avg,\n    RANK() OVER (ORDER BY salary DESC) as salary_rank,\n    LAG(salary) OVER (ORDER BY hire_date) as prev_salary,\n    SUM(salary) OVER (ORDER BY hire_date ROWS UNBOUNDED PRECEDING) as running_total\nFROM employees;",
                    "SQL",
                    "sql", "analytics"
            ),
            new CodeSnippet(
                    "SQL Recursive CTE",
                    "Recursive query for hierarchical data",
                    "WITH RECURSIVE org_tree AS (\n    -- Base case: top-level managers\n    SELECT id, name, manager_id, 1 as level\n    FROM employees WHERE manager_id IS NULL\n    \n    UNION ALL\n    \n    -- Recursive case\n    SELECT e.id, e.name, e.manager_id, t.level + 1\n    FROM employees e\n    JOIN org_tree t ON e.manager_id = t.id\n)\nSELECT * FROM org_tree ORDER BY level, name;",
                    "SQL",
                    "sql", "recursive", "cte"
            ),

            // Java snippets
            new CodeSnippet(
                    "Java Stream Operations",
                    "Common Stream API patterns",
                    "List<User> activeUsers = users.stream()\n    .filter(User::isActive)\n    .sorted(Comparator.comparing(User::getName))\n    .collect(Collectors.toList());\n\nMap<String, List<User>> byDepartment = users.stream()\n    .collect(Collectors.groupingBy(User::getDepartment));\n\nOptional<User> topUser = users.stream()\n    .max(Comparator.comparing(User::getScore));",
                    "Java",
                    "java", "streams", "collections"
            ),
            new CodeSnippet(
                    "Java CompletableFuture",
                    "Async programming with CompletableFuture",
                    "CompletableFuture<User> userFuture = CompletableFuture\n    .supplyAsync(() -> userService.getUser(id))\n    .thenApply(user -> enrichUser(user))\n    .exceptionally(ex -> {\n        log.error(\"Failed to get user\", ex);\n        return User.empty();\n    });\n\n// Combine multiple futures\nCompletableFuture.allOf(future1, future2, future3)\n    .thenRun(() -> System.out.println(\"All done!\"));",
                    "Java",
                    "java", "async", "concurrent"
            )
    };

    // ==================== TIME STRINGS ====================
    private static final String[] TIME_STRINGS = {
            "Just now", "2m ago", "5m ago", "15m ago", "30m ago",
            "1h ago", "2h ago", "3h ago", "5h ago", "8h ago",
            "Yesterday", "2d ago", "3d ago", "1w ago"
    };

    // ==================== METHODS TO GET MOCK DATA ====================

    /**
     * Get a list of mock users
     */
    public static List<User> getMockUsers(int count) {
        List<User> users = new ArrayList<>();
        int limit = Math.min(count, USER_DATA.length);

        for (int i = 0; i < limit; i++) {
            User user = new User();
            user.setId(USER_DATA[i][0]);
            user.setUsername(USER_DATA[i][1]);
            user.setEmail(USER_DATA[i][2]);
            user.setFullName(USER_DATA[i][3]);
            user.setBio(USER_DATA[i][4]);
            user.setAvatarUrl(AVATAR_URLS[i % AVATAR_URLS.length]);
            user.setGithubUrl("https://github.com/" + USER_DATA[i][1]);
            user.setCreatedAt("2024-01-15T10:30:00Z");
            users.add(user);
        }

        return users;
    }

    /**
     * Get a single random mock user
     */
    public static User getRandomUser() {
        List<User> users = getMockUsers(USER_DATA.length);
        return users.get(random.nextInt(users.size()));
    }

    /**
     * Get mock snippet cards for the feed
     */
    public static List<SnippetCard> getMockSnippetCards(int count) {
        List<SnippetCard> cards = new ArrayList<>();
        List<User> users = getMockUsers(USER_DATA.length);

        for (int i = 0; i < count; i++) {
            CodeSnippet snippet = CODE_SNIPPETS[i % CODE_SNIPPETS.length];
            User author = users.get(random.nextInt(users.size()));
            LanguageInfo lang = getLanguageByName(snippet.language);

            int langColor = android.graphics.Color.WHITE;
            try {
                langColor = android.graphics.Color.parseColor(lang.color);
            } catch (Exception ignored) {}

            cards.add(new SnippetCard(
                    "snippet-" + (i + 1),                  // id
                    snippet.title,                          // title
                    snippet.description,                    // description
                    lang.name,                              // languageBadge
                    TIME_STRINGS[i % TIME_STRINGS.length],  // updatedTime
                    snippet.code,                           // codePreview
                    snippet.tags,                           // tags
                    langColor,                              // languageColor
                    author.getFullName(),                   // authorName
                    author.getAvatarUrl(),                  // authorAvatar
                    author.getUsername(),                   // authorUsername
                    random.nextInt(150) + 5,                // likesCount
                    random.nextInt(30),                     // commentsCount
                    random.nextBoolean(),                   // isLiked
                    random.nextFloat() > 0.3 ? "public" : "private"  // visibility
            ));
        }

        return cards;
    }

    /**
     * Get mock teams
     */
    public static List<Team> getMockTeams(int count) {
        String[][] teamData = {
                {"t1", "Frontend Guild", "React, Vue, Angular experts sharing UI patterns", "https://i.pravatar.cc/150?u=team1"},
                {"t2", "Backend Masters", "API design, microservices, and database optimization", "https://i.pravatar.cc/150?u=team2"},
                {"t3", "Mobile Dev Team", "iOS, Android, and cross-platform development", "https://i.pravatar.cc/150?u=team3"},
                {"t4", "DevOps & Cloud", "CI/CD, Kubernetes, and infrastructure as code", "https://i.pravatar.cc/150?u=team4"},
                {"t5", "AI/ML Research", "Machine learning models and data science", "https://i.pravatar.cc/150?u=team5"},
                {"t6", "Security Squad", "Application security and best practices", "https://i.pravatar.cc/150?u=team6"},
                {"t7", "Open Source Contributors", "Community-driven open source projects", "https://i.pravatar.cc/150?u=team7"},
                {"t8", "Code Review Club", "Peer code review and mentorship", "https://i.pravatar.cc/150?u=team8"}
        };

        String[] roles = {"owner", "admin", "member"};
        String[] privacies = {"public", "private", "invite-only"};

        List<Team> teams = new ArrayList<>();
        int limit = Math.min(count, teamData.length);

        for (int i = 0; i < limit; i++) {
            teams.add(new Team(
                    teamData[i][0],
                    teamData[i][1],
                    teamData[i][2],
                    teamData[i][3],
                    random.nextInt(20) + 3,           // memberCount
                    random.nextInt(50) + 10,          // snippetCount
                    privacies[random.nextInt(privacies.length)],
                    "u1",                              // ownerId
                    roles[i == 0 ? 0 : random.nextInt(roles.length)]
            ));
        }

        return teams;
    }

    /**
     * Get mock team members
     */
    public static List<TeamMember> getMockTeamMembers(int count) {
        List<TeamMember> members = new ArrayList<>();
        List<User> users = getMockUsers(Math.min(count, USER_DATA.length));

        String[] roles = {"owner", "admin", "member", "member", "member"};

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            members.add(new TeamMember(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getAvatarUrl(),
                    roles[i % roles.length]
            ));
        }

        return members;
    }

    /**
     * Get mock notifications
     */
    public static List<NotificationItem> getMockNotifications(int count) {
        String[][] notificationData = {
                {NotificationItem.TYPE_LIKE, "New Like", "liked your snippet", "sarah_codes"},
                {NotificationItem.TYPE_COMMENT, "New Comment", "commented on your code", "mike_js"},
                {NotificationItem.TYPE_FOLLOW, "New Follower", "started following you", "emma_py"},
                {NotificationItem.TYPE_FORK, "Snippet Forked", "forked your snippet", "alex_kotlin"},
                {NotificationItem.TYPE_TEAM_INVITE, "Team Invite", "invited you to join", "lisa_react"},
                {NotificationItem.TYPE_MENTION, "Mentioned You", "mentioned you in a comment", "david_go"},
                {NotificationItem.TYPE_LIKE, "New Like", "liked your snippet", "nina_rust"},
                {NotificationItem.TYPE_COMMENT, "New Comment", "replied to your comment", "chris_swift"},
                {NotificationItem.TYPE_FOLLOW, "New Follower", "started following you", "amy_flutter"},
                {NotificationItem.TYPE_FORK, "Snippet Forked", "forked 'React Custom Hook'", "john_dev"}
        };

        List<NotificationItem> notifications = new ArrayList<>();
        int limit = Math.min(count, notificationData.length);

        for (int i = 0; i < limit; i++) {
            String[] data = notificationData[i % notificationData.length];
            NotificationItem item = new NotificationItem(
                    "notif-" + (i + 1),
                    data[0],                               // type
                    data[1],                               // title
                    data[3] + " " + data[2],               // message
                    TIME_STRINGS[i % TIME_STRINGS.length], // timestamp
                    i > 3,                                  // isRead (first 4 unread)
                    data[3]                                 // actorName
            );
            item.setActorAvatar(AVATAR_URLS[i % AVATAR_URLS.length]);
            notifications.add(item);
        }

        return notifications;
    }

    /**
     * Get mock activity feed items
     */
    public static List<ActivityFeedItem> getMockActivityFeed(int count) {
        String[][] activityData = {
                {"sarah_codes", "created", "React State Management"},
                {"mike_js", "updated", "Node.js Express Server"},
                {"emma_py", "forked", "Python ML Pipeline"},
                {"alex_kotlin", "starred", "Android Compose Layout"},
                {"lisa_react", "commented on", "CSS Grid Flexbox"},
                {"david_go", "created", "Go HTTP Handler"},
                {"nina_rust", "updated", "Rust Error Handling"},
                {"chris_swift", "forked", "SwiftUI Animation"},
                {"amy_flutter", "starred", "Dart Stream Builder"},
                {"john_dev", "created", "TypeScript Utilities"}
        };

        int[] icons = {
                R.drawable.ic_add,
                R.drawable.ic_edit,
                R.drawable.ic_git_branch,
                R.drawable.ic_star_filled,
                R.drawable.ic_more_horiz,
                R.drawable.ic_add,
                R.drawable.ic_edit,
                R.drawable.ic_git_branch,
                R.drawable.ic_star_filled,
                R.drawable.ic_add
        };

        List<ActivityFeedItem> activities = new ArrayList<>();
        int limit = Math.min(count, activityData.length);

        for (int i = 0; i < limit; i++) {
            String[] data = activityData[i % activityData.length];
            activities.add(new ActivityFeedItem(
                    data[0],                               // userName
                    data[1],                               // action
                    data[2],                               // snippetName
                    TIME_STRINGS[i % TIME_STRINGS.length], // timestamp
                    icons[i % icons.length]                // iconResId
            ));
        }

        return activities;
    }

    /**
     * Get language info by name
     */
    public static LanguageInfo getLanguageByName(String name) {
        for (LanguageInfo lang : LANGUAGES) {
            if (lang.name.equalsIgnoreCase(name)) {
                return lang;
            }
        }
        return new LanguageInfo("Unknown", "#808080", "txt");
    }

    /**
     * Get all supported languages
     */
    public static List<LanguageInfo> getAllLanguages() {
        return Arrays.asList(LANGUAGES);
    }

    /**
     * Get random code snippet
     */
    public static CodeSnippet getRandomCodeSnippet() {
        return CODE_SNIPPETS[random.nextInt(CODE_SNIPPETS.length)];
    }

    /**
     * Get all code snippets
     */
    public static List<CodeSnippet> getAllCodeSnippets() {
        return Arrays.asList(CODE_SNIPPETS);
    }
}
