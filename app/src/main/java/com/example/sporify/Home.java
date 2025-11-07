package com.example.sporify;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.sporify.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment playlistFragment = new PlaylistFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.topToolBar);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.topToolBar.setPadding(0, sys.top, 0, 0);
            binding.bottomNavigationView.setPadding(0, 0, 0, sys.bottom);
            return insets;
        });


        replaceFragment(homeFragment);

        Fragment selectedFragmente = null;
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home)   {
                replaceFragment(homeFragment);
                return true;
            }else if (id == R.id.profile){
                replaceFragment(profileFragment);
                return true;
            }else if (id == R.id.playlist)  {
                replaceFragment(playlistFragment);
                return true;
            }else {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.profile){
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}