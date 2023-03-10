package com.example.example_project.ui.character.character_list;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.example_project.R;
import com.example.example_project.ui.character.Character;
import com.example.example_project.ui.character.CharacterAdapter;
import com.example.example_project.ui.character.character_creation.CharacterCreationActivity;
import com.example.example_project.ui.games_list.GamesListActivity;
import com.example.example_project.ui.login.LoginActivity;
import com.example.example_project.ui.main_page.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CharactersListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView addButton;
    private FirebaseFirestore db;
    private final List<Character> characters = new ArrayList<>();
    private CharacterAdapter characterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheets_list);

        // click on the add button to go to the character creation activity
        addButton = findViewById(R.id.imageview_add_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CharacterCreationActivity.class);
            startActivity(intent);
        });

        // set up the recycler view
        recyclerView = findViewById(R.id.recycleview_charactersheets);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set up the adapter
        characterAdapter = new CharacterAdapter(characters);
        recyclerView.setAdapter(characterAdapter);

        // get the list of characters from the database
        db = FirebaseFirestore.getInstance();
        db.collection("characters")
                .whereEqualTo("email", "username@email.com")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        //get the list of documents
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            //add the document to the list
                            Character character = document.toObject(Character.class);
                            characters.add(character);
                        }

                        //notify the adapter that the data has changed
                        characterAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error getting documents: ", e);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_game:
                Intent intent = new Intent(CharactersListActivity.this, GamesListActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_logout:
                Intent intent1 = new Intent(CharactersListActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;

            case R.id.menu_home:
                Intent intent2 = new Intent(CharactersListActivity.this, MainActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}