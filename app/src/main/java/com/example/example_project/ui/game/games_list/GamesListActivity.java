package com.example.example_project.ui.game.games_list;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.example_project.R;
import com.example.example_project.ui.character.character_list.CharactersListActivity;
import com.example.example_project.ui.game.Game;
import com.example.example_project.ui.game.GameActivity;
import com.example.example_project.ui.main_page.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GamesListActivity extends AppCompatActivity {
    private final ArrayList<Game> games = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;
    private GameAdapter gameAdapter;
    private RecyclerView recyclerView;
    private ImageView joinButton;
    private ImageView addButton;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.menu_game);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.menu_home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.menu_game:
                        return true;
                    case R.id.menu_characters:
                        startActivity(new Intent(getApplicationContext(),CharactersListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(GamesListActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        email = firebaseUser.getEmail();

        // set up the recycler view
        recyclerView = findViewById(R.id.recycleview_chat);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set up the adapter
        gameAdapter = new GameAdapter(games);
        recyclerView.setAdapter(gameAdapter);

        // get the list of characters from the database
        db = FirebaseFirestore.getInstance();
        db.collection("games")
                .whereArrayContains("players", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        //get the list of documents
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            //add the document to the list
                            Game game = document.toObject(Game.class);
                            game.setId(document.getId());
                            games.add(game);
                        }

                        //notify the adapter that the data has changed
                        gameAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error getting documents: ", e);
                });

        gameAdapter.setOnItemClickListener(new GameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Game game = games.get(position);
                Intent intent = new Intent(GamesListActivity.this, GameActivity.class);
                intent.putExtra("selected_game", game);
                startActivity(intent);
            }
        });

        // join a game
        joinButton = findViewById(R.id.imageview_join_game);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and show the message box
                AlertDialog.Builder builder = new AlertDialog.Builder(GamesListActivity.this);
                builder.setTitle("Enter the group's code");

                // Set up the input
                final EditText input = new EditText(GamesListActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();

                        //get document reference
                        DocumentReference docRef = db.collection("games").document(name);

                        // get document and update it
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        //get the list of players
                                        ArrayList<String> players = (ArrayList<String>) document.get("players");
                                        //add the current player to the list
                                        players.add(email);
                                        //update the document
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("players", players);
                                        docRef.update(updates)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(getApplicationContext(), "Player successfully added to the game!", Toast.LENGTH_SHORT).show();
                                                    //add the game to the list
                                                    Game game = document.toObject(Game.class);
                                                    game.setId(document.getId());
                                                    games.add(game);
                                                    //notify the adapter that the data has changed
                                                    gameAdapter.notifyDataSetChanged();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error adding player", Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(getApplicationContext(), "the game doesn't exist!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        addButton = findViewById(R.id.imageview_add_game);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and show the message box
                AlertDialog.Builder builder = new AlertDialog.Builder(GamesListActivity.this);
                builder.setTitle("Enter the group's name");

                // Set up the input
                final EditText input = new EditText(GamesListActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();

                        ArrayList<String> players = new ArrayList<>();
                        players.add(email);

                        Game game = new Game(name, email, "Test Icon", players, null);

                        // Add a new document with a generated ID
                        db.collection("games")
                                .add(game)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        games.add(game);
                                        //notify the adapter that the data has changed
                                        gameAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}