package com.example.mad_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class EndActivity extends AppCompatActivity {
    // used to display the final score
    private int score;

    // refs to all needed widgets
    private TextView scoreView;
    private TextView outcomeView;
    private TextView warningView;
    private EditText editName;
    private Button btn;

    private ScoreDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get refs of widgets
        scoreView = findViewById(R.id.score);
        outcomeView = findViewById(R.id.outcome);
        warningView = findViewById(R.id.warning);
        editName = findViewById(R.id.nameInput);
        btn = findViewById(R.id.confirmBtn);

        // get score data from parent activity
        score = getIntent().getIntExtra("currentScore", 0);
        scoreView.setText(getString(R.string.end_score) + " " + score);

        // change the screen to add an edittext if player's score is equal to or higher than
        // someone else's in the DB
        db = new ScoreDatabaseHandler(this);

        boolean isEligible = isPlayerEligible(score);
        if(isEligible) {
            outcomeView.setVisibility(View.VISIBLE);
            editName.setVisibility(View.VISIBLE);
            btn.setVisibility(View.VISIBLE);
            outcomeView.setText(R.string.end_option1);
        } else {
            outcomeView.setText(R.string.end_option2);
        }
    }

    // show the leaderboard
    public void goToLeaderboard(View view) {
        Intent highscoreActivity = new Intent(view.getContext(), HighscoreActivity.class);
        startActivity(highscoreActivity);
        finish();
    }

    // checks that player's score is equal to or higher than someone else's in the DB
    private boolean isPlayerEligible(int score) {
        List<RecordEntry> records = db.getFiveHighestRecords();
        for(RecordEntry rec : records) {
            if(score >= rec.getScore()) {
                return true;
            }
        }
        return false;
    }

    // end activity
    public void goBack(View view) {
        finish();
    }

    // logic to add an entry to the database
    public void confirm(View view) {
        String text = editName.getText().toString();
        // don't allow null/empty/"blank" strings (those that only have spaces)
        if(text.isBlank()) {
            warningView.setVisibility(View.VISIBLE);
            return;
        }

        // only allow names between 3-16 characters
        int length = text.length();
        if(length < 3 || length > 16) {
            warningView.setVisibility(View.VISIBLE);
            return;
        }

        // remove the prompts + widgets for adding the entry once it's done
        db.addRecord(new RecordEntry(text, score));
        editName.setVisibility(View.INVISIBLE);
        warningView.setVisibility(View.INVISIBLE);
        outcomeView.setText(R.string.end_option3);
        btn.setVisibility(View.INVISIBLE);
    }
}