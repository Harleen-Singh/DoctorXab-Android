package com.example.doc_app_android;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.doc_app_android.Adapter.FilterRCVadapter;
import com.example.doc_app_android.DoctorHomeFragments.DoctorAppointmentsFragment;
import com.example.doc_app_android.DoctorHomeFragments.PrivacyPolicyFragment;
import com.example.doc_app_android.DoctorHomeFragments.ProfileFragment;
import com.example.doc_app_android.DoctorHomeFragments.ScheduleFragment;
import com.example.doc_app_android.DoctorHomeFragments.SettingsFragment;
import com.example.doc_app_android.PatentHomeFragments.NotificationFragment;
import com.example.doc_app_android.data_model.FilterData;
import com.example.doc_app_android.data_model.ProfileData;
import com.example.doc_app_android.databinding.ActivityHomeBinding;
import com.example.doc_app_android.view_model.HomeViewModel;
import com.example.doc_app_android.view_model.ProfileViewModel;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    private NavigationView nav;
    private ProfileViewModel profileViewModel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private boolean regornot = false;
    private FilterRCVadapter filterAdapter;
    ActivityHomeBinding binding;
    private EditText search_field;
    private ImageButton search, draw_btn, noti_btn;
    private HomeViewModel model;
    private CircleImageView nav_profile;
    private TextView nav_name;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    Fragment temp;



    @Override
    protected void onResume() {
        super.onResume();

        binding.toolbar.setVisibility(View.VISIBLE);
        if(getIntent().getExtras()!=null) {
            try {
                boolean b = getIntent().getExtras().getBoolean("notification");
                if (b) {
                    temp = new NotificationFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.fragmentHome_container, temp).setReorderingAllowed(true).addToBackStack(null).commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ImplicitIntentTesting", "I am getting called 4");

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        regornot = getIntent().getBooleanExtra("reg", false);
        search = findViewById(R.id.search_btn);
        noti_btn = findViewById(R.id.noti_btn);
        filterAdapter = new FilterRCVadapter(this);
        binding.setLifecycleOwner(this);
        binding.filterRcv.setAdapter(filterAdapter);
        model = new ViewModelProvider(this).get(HomeViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        preferences = getSharedPreferences("tokenFile", Context.MODE_PRIVATE);

        if (!preferences.getBoolean("isDoc", false)) {
            search.setVisibility(View.GONE);

        }

        model.getFilters().observe(this, new Observer<ArrayList<FilterData>>() {
            @Override
            public void onChanged(ArrayList<FilterData> filterData) {
                filterAdapter.setFilter(filterData);
                filterAdapter.notifyDataSetChanged();
            }
        });

        profileViewModel.getProfileDetails().observe(this, new Observer<ProfileData>() {
            @Override
            public void onChanged(ProfileData profileData) {
                Log.d("HomeTesting", "Name: " + profileData.getName());

                nav_name = findViewById(R.id.nav_profile_name);
                nav_profile = findViewById(R.id.nav_profile_image);
                nav_name.setText(profileData.getName());
                Picasso.get()
                        .load(profileData.getImage())
                        .placeholder(R.drawable.doctor_profile_image)
                        .into(nav_profile);
            }
        });


        search_field = findViewById(R.id.edit_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutTransition layoutTransition = binding.homeLayoutContainer.getLayoutTransition();
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
                if (search_field.getVisibility() == View.GONE)
                    search_field.setVisibility(View.VISIBLE);
                else
                    search_field.setVisibility(View.GONE);
            }
        });

        noti_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = new NotificationFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentHome_container, temp).setReorderingAllowed(true).addToBackStack(null).commit();

            }
        });


        setSupportActionBar((Toolbar) binding.toolbar);
        nav = findViewById(R.id.nav_menu);
        drawerLayout = binding.drawer;
        draw_btn = findViewById(R.id.drawer_btn);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        draw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav.setCheckedItem(R.id.menu_profile);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_profile:
                        temp = new ProfileFragment();
                        break;

                    case R.id.menu_appointments:
                        temp = new DoctorAppointmentsFragment();
                        break;

                    case R.id.menu_schedule:
                        temp = new ScheduleFragment();
                        break;

                    case R.id.menu_settings:
                        temp = new SettingsFragment();
                        break;

                    case R.id.menu_privacyplicy:
                        temp = new PrivacyPolicyFragment();
                        break;

                    case R.id.menu_logout:
                        SharedPreferences.Editor editor = getSharedPreferences("tokenFile", Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();
                        finishAffinity();
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }

                if (temp != null)
                    getSupportFragmentManager().beginTransaction().add(R.id.fragmentHome_container, temp).setReorderingAllowed(true).addToBackStack("name").commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }
}