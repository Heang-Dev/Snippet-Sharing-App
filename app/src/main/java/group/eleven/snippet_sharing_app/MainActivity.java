package group.eleven.snippet_sharing_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import group.eleven.snippet_sharing_app.ui.search.SearchActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.ui.profile.AccountSettingsActivity;
import group.eleven.snippet_sharing_app.ui.profile.NotificationSettingsActivity;
import group.eleven.snippet_sharing_app.ui.team.TeamsListActivity;

/**
 * Main Activity - Rich Dashboard with Drawer and Bottom Navigation.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        try {
            // Apply Window Insets for edge-to-edge
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // Initialize Views
            drawerLayout = findViewById(R.id.drawerLayout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
            FloatingActionButton fab = findViewById(R.id.fab);
            android.view.View menuIcon = findViewById(R.id.ivMenu);
            android.view.View searchBar = findViewById(R.id.etSearch);

            // Handle Drawer Open
            if (menuIcon != null) {
                menuIcon.setOnClickListener(v -> {
                    if (drawerLayout != null) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                });
            }

            // Handle Side Menu Item Clicks
            if (navigationView != null) {
                navigationView.setNavigationItemSelectedListener(this);
                // Handle Close Button in Header
                android.view.View headerView = navigationView.getHeaderView(0);
                if (headerView != null) {
                    android.view.View closeBtn = headerView.findViewById(R.id.btnClose);
                    if (closeBtn != null) {
                        closeBtn.setOnClickListener(v -> {
                            if (drawerLayout != null) {
                                drawerLayout.closeDrawer(GravityCompat.START);
                            }
                        });
                    }
                }

                // Handle Footer Logout Button
                // Footer is inflated by NavigationView, but accessing it might need finding it
                // via findViewById if it's part of the decor or getting child.
                // Actually, since we used <include> inside NavigationView in XML, it's a child
                // of NavigationView.
                // Safe way:
                android.view.View logoutBtn = findViewById(R.id.btnLogout);
                if (logoutBtn != null) {
                    logoutBtn.setOnClickListener(v -> {
                        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                        // Add logout logic here
                    });
                }
            }

            // Handle Bottom Nav Clicks
            if (bottomNav != null) {
                bottomNav.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.nav_home) {
                        // Already on dashboard
                        return true;
                    } else if (id == R.id.nav_teams) {
                        startActivity(new Intent(this, group.eleven.snippet_sharing_app.ui.team.TeamsListActivity.class));
                        return false;
                    } else if (id == R.id.nav_snippets) {
                        startActivity(new Intent(this, MySnippetsActivity.class));
                        return false;
                    } else if (id == R.id.nav_profile) {
                        startActivity(new Intent(this, group.eleven.snippet_sharing_app.ui.profile.ProfileActivity.class));
                        return false;
                    }
                    return false;
                });
            }

            // Handle FAB
            if (fab != null) {
                fab.setOnClickListener(v -> {
                    startActivity(new Intent(this, CreateSnippetActivity.class));
                });
            }

            // Handle Search Bar
            if (searchBar != null) {
                searchBar.setOnClickListener(v -> {
                    startActivity(new Intent(this, SearchActivity.class));
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
            Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_all_snippets) {
            startActivity(new Intent(this, MySnippetsActivity.class));
        } else if (id == R.id.nav_favorites) {
            Toast.makeText(this, "Favorites - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_shared) {
            Toast.makeText(this, "Shared with me - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_teams) {
            startActivity(new Intent(this, TeamsListActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_preferences) {
            startActivity(new Intent(this, AccountSettingsActivity.class));
        } else if (id == R.id.nav_help) {
            Toast.makeText(this, "Help & Support - Coming Soon", Toast.LENGTH_SHORT).show();
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
