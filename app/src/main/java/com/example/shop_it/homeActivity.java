package com.example.shop_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class homeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    Fragment frag;
    Bundle data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        int customer_id = getIntent().getExtras().getInt("customer_id");
        bottomNav = findViewById(R.id.bottom_nav);
        data = new Bundle();
        data.putInt("customer_id", customer_id);
        frag = new homeFragment();
        frag.setArguments(data);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
        bottomNav.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                frag = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        frag = new homeFragment();
                        break;
                    case R.id.nav_cart:
                        frag = new cartFragment();
                        break;
                    case R.id.nav_profile:
                        frag = new profileFragment();
                        break;
                    case R.id.nav_search:
                        frag = new searchFragment();
                        break;
                    default:
                        break;
                }

                if (frag != null) {
                    frag.setArguments(data);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
                }
            }
        });

    }

}
