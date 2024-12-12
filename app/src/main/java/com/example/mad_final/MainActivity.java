package com.example.mad_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// screen orientation: https://stackoverflow.com/questions/4885620/force-portrait-orientation-mode
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // seed the database at start if it's empty
        ScoreDatabaseHandler db = new ScoreDatabaseHandler(this);
        if(db.isDatabaseEmpty()) {
            databaseSeeder(db);
        }
    }

    // first screen to go to after starting is the sequence screen
    public void goToSequence(View view) {
        Intent sequenceActivity = new Intent(view.getContext(), SequenceActivity.class);
        startActivity(sequenceActivity);
    }

    // premade data to put into the database
    private void databaseSeeder(ScoreDatabaseHandler db) {
        db.addRecord(new RecordEntry("CoolDawg", 69));
        db.addRecord(new RecordEntry("Nia2005", 20));
        db.addRecord(new RecordEntry("Twin_Helix", 16));
        db.addRecord(new RecordEntry("emmy", 12));
        db.addRecord(new RecordEntry("How2Play", 7));
    }
}