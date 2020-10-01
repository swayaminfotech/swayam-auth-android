package com.swayamauth;

/**
 * Created by swayam infotech
 */

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.swayamauth.activity.LoginActivity;
import com.swayamauth.Utils.Prefs;
import com.swayamauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ActivityMainBinding binding;

    private AppBarConfiguration mAppBarConfiguration;
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        mTvTitle = toolbar.findViewById(R.id.toolbarTitle);
        setSupportActionBar(toolbar);
        
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_edit_profile, R.id.nav_about_us, R.id.nav_change_password, R.id.nav_rate_app, R.id.nav_share_app)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        setClickListener();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.white)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTvTitle.setText(getString(R.string.menu_home));
    }

    // Set click listener
    private void setClickListener() {
        binding.llLogout.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Handle click
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llLogout:
                AlertDialog.Builder alerDialog = new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Prefs.clear(MainActivity.this);
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setMessage(getString(R.string.are_you_sure_you_want_to_logout_from_app));

                AlertDialog alert = alerDialog.create();
                alert.show();
                break;
        }
    }

    // Handle side menu click event
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        switch (id) {
            case R.id.nav_share_app:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out this app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.nav_rate_app:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("market://details?id=" +getPackageName()))));
                } catch (ActivityNotFoundException e1) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" +getPackageName())));
                    } catch (ActivityNotFoundException e2) {
                        Toast.makeText(this, "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:

                mTvTitle.setText(item.getTitle());
                navController.navigate(id);

                break;
        }
        binding.drawerLayout.closeDrawers();
        return false;
    }

}