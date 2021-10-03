package com.example.cookbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.cookbook.R;
import com.example.cookbook.fragment.AboutAppFragment;
import com.example.cookbook.fragment.DashboardFragment;
import com.example.cookbook.fragment.FavouritesFragment;
import com.example.cookbook.util.ConnectionManager;
import com.google.android.material.navigation.NavigationView;

public class HomeScreenActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    CoordinatorLayout coordinatorLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    MenuItem previousMenuItem= null;
    ConnectionManager cm= new ConnectionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialising Views
        frameLayout = findViewById(R.id.frameLayout);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolBar);
        cm = new ConnectionManager();
        setUpToolbar();
        openDashboard();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener((it)-> {
            if(previousMenuItem != null){
                previousMenuItem.setChecked(false);
            }
            it.setChecked(true);
            it.setCheckable(true);
            previousMenuItem= it;
            if(it.getItemId()==R.id.dashboard){
                openDashboard();
                drawerLayout.closeDrawers();
            }else if(it.getItemId()==R.id.favourites){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new FavouritesFragment())
                        .addToBackStack("Favourites")
                        .commit();
                getSupportActionBar().setTitle("Favourites");
                drawerLayout.closeDrawers();
            }else if(it.getItemId()== R.id.about_app){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new AboutAppFragment())
                        .addToBackStack("About App")
                        .commit();
                getSupportActionBar().setTitle("About App");
                drawerLayout.closeDrawers();
            }
            return true;
        });

        if(cm.checkConnectivity(getApplicationContext())){
            Toast.makeText(this, "Internet Connected", Toast.LENGTH_SHORT).show();
        }else{
            AlertDialog.Builder dialog =new AlertDialog.Builder(this);
            dialog.setTitle("No Internet");
            dialog.setMessage("Please connect to internet to continue..");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent settingsIntent= new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(settingsIntent);
                    finish();
                }
            });
            dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.finishAffinity((Activity) getApplicationContext());
                }
            });

            dialog.create();
            dialog.show();
        }

    }

    void setUpToolbar(){
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("ToolBar Title");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void openDashboard(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new DashboardFragment())
                .commit();
        getSupportActionBar().setTitle("Dashboard");
        navigationView.setCheckedItem(R.id.dashboard);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id== android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       int cnt= getSupportFragmentManager().getBackStackEntryCount();
       if (cnt>0){
           openDashboard();
           while (cnt-->0){
               getSupportFragmentManager().popBackStack();
           }
       }else
           super.onBackPressed();

       drawerLayout.closeDrawers();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}