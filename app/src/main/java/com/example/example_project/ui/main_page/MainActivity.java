package com.example.example_project.ui.main_page;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.example_project.R;
import com.example.example_project.ui.character.character_list.CharactersListActivity;
import com.example.example_project.ui.game.games_list.GamesListActivity;
import com.example.example_project.ui.login.LoginActivity;
import com.example.example_project.ui.model.NotificationWorker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_REQUEST_ID = 911;
    private MainPresenter presenter;
    private TextView welcome;
    private ImageView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        SetViews();

        presenter = new MainPresenter(this);

        //sign out method
        signOut = findViewById(R.id.signout_button);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.LogOutClicked();
            }
        });
    }

    private void SetViews() {
        welcome = findViewById(R.id.textview_welcome);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.menu_game:
                        startActivity(new Intent(getApplicationContext(),GamesListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.menu_home:
                        return true;
                    case R.id.menu_characters:
                        startActivity(new Intent(getApplicationContext(),CharactersListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        // Check for notification permission
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            StartWorker();
        }
        else{
            // ask for permission
            String[] permissions = {android.Manifest.permission.POST_NOTIFICATIONS};
            requestPermissions(permissions, NOTIFICATION_REQUEST_ID);
        }
    }

    public void WelcomeText(FirebaseUser firebaseUser){
        welcome.setText("Welcome " + firebaseUser.getDisplayName() + "!");
        welcome.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_REQUEST_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StartWorker();
            }
        }
    }

    public void StartWorker(){
        WorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .build();
        WorkManager.getInstance(this).enqueue(request);
    }
}