package com.lujuf.stado.mixup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujuf.stado.mixup.Fragments.AddSongsFragment;
import com.lujuf.stado.mixup.Fragments.AppSettingsFragment;
import com.lujuf.stado.mixup.Fragments.MyLibFragment;
import com.lujuf.stado.mixup.Fragments.MyProfileFragment;
import com.lujuf.stado.mixup.Fragments.ExploreFragment;
import com.lujuf.stado.mixup.Fragments.OrdersFragment;
import com.lujuf.stado.mixup.Fragments.UserCartFragment;
import com.lujuf.stado.mixup.Fragments.WallFragment;


// DO A FUC%$N CLEANUP SOON OK BRO?

public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Context context;

    private FirebaseAuth auth;

    private Toolbar mainToolBar;
    private ImageButton userCart;

    private LayoutInflater inflater;

    // User Stuff
    private TextView userName;
    private ImageView userAvatar;

    private TextView cartItems;

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        // User Auth Part
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainScreenActivity.this, GreetingsActivity.class));
            finish();
        }

        context = getApplicationContext();

        // Adding Main Bar to Layout
        mainToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setTitle(R.string.bar_text_wall); // Need to do this auto

        userCart = (ImageButton) findViewById(R.id.menu_user_cart);
        cartItems = findViewById(R.id.cart_items_amount);

        userCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new UserCartFragment();

                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }

                getSupportActionBar().setTitle(R.string.bar_text_user_cart);
            }
        });

        // Adding This little sh&t in bottom right corner
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Adding Rear Panel
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mainToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View mainBarView = navigationView.getHeaderView(0);
        //Possible NullPointer Crash?
        userAvatar = mainBarView.findViewById(R.id.user_avatar_bar);
        userName = mainBarView.findViewById(R.id.username_bar);

        LoadCartData();

        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mainToolBar.setTitle(R.string.bar_text_user_profile);
                Log.d("GUI", "User Pressed Avatar!");

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                Fragment fragment = new MyProfileFragment();

                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
                // Send To My Profile Tab
            }

        });

        if(userName != null)
            userName.setText(auth.getCurrentUser().getEmail());

        inflater = getLayoutInflater();

        Fragment fragment = new WallFragment();

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.nav_wall:
                getSupportActionBar().setTitle(R.string.bar_text_wall);
                Log.d("GUI", "User Pressed Wall Button!");
                fragment = new WallFragment();
                break;

            case R.id.nav_add_songs:
                getSupportActionBar().setTitle(R.string.bar_text_add_songs);
                Log.d("GUI", "User Pressed Add Songs Button!");
                fragment = new AddSongsFragment();
                break;

            /*case R.id.nav_search:
                mainToolBar.setTitle(R.string.bar_text_lookup);
                Log.d("GUI", "User Pressed Search Button!");
                fragment = new ExploreFragment();
                break;
            */
            case R.id.nav_my_lib:
                getSupportActionBar().setTitle("My Library");
                Log.d("GUI", "User Pressed My library Button!");
                fragment = new MyLibFragment();
                break;

            case R.id.nav_explore:
                getSupportActionBar().setTitle("Explore");
                Log.d("GUI", "User Pressed Explore Button!");
                fragment = new ExploreFragment();
                break;

            case R.id.nav_app_settings:
                getSupportActionBar().setTitle(R.string.bar_text_settings);
                fragment = new AppSettingsFragment();
                break;

            case R.id.nav_order_history:
                getSupportActionBar().setTitle(R.string.bar_text_settings);
                fragment = new OrdersFragment();
                break;

            case R.id.nav_logout:
                if (auth.getCurrentUser() != null)
                {
                    auth.signOut();
                    startActivity(new Intent(MainScreenActivity.this, GreetingsActivity.class));
                    finish();
                }
                break;

            default: break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void LoadCartData()
    {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        mDatabase.getReference().child("Users").child(auth.getUid()).child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItems.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
