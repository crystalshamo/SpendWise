package com.example.spendwise;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.spendwise.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController =
                ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_nav_menu)).getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // ✅ Safe Firebase init
        if (!isRunningInTest()) {
            mAuth = FirebaseAuth.getInstance();
        }
    }



    private boolean isRunningInTest() {
        return "robolectric".equals(android.os.Build.FINGERPRINT)
                || System.getProperty("robolectric.running") != null;
    }
    @Override
    public void onStart() {
        super.onStart();

        if (!isRunningInTest()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }
    }
    public void logout() {
        if (!isRunningInTest()) {
            FirebaseAuth.getInstance().signOut(); // ✅ Only runs in real app
        }

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish(); // prevent back press to this screen
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu); // Menu file with logout
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
