package ru.krus.around;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideosActivity extends AppCompatActivity {
    CircleImageView profileAvatar;
    public String avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_add_video,R.id.navigation_likes, R.id.navigation_user)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    public void logOutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(VideosActivity.this, LogInActivity.class));
    }

    public void settingsClick(View view) {
        Intent intent = new Intent(VideosActivity.this, EditProfileActivity.class);
        intent.putExtra("fromSignIn", false);
        startActivity(intent);
    }

    public void addContentButton(MenuItem item) {
        Intent intent = new Intent(VideosActivity.this, AddContentActivity.class);
        startActivity(intent);
    }
}
