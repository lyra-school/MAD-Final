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
    private int score;

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

        scoreView = findViewById(R.id.score);
        outcomeView = findViewById(R.id.outcome);
        warningView = findViewById(R.id.warning);
        editName = findViewById(R.id.nameInput);
        btn = findViewById(R.id.confirmBtn);


        score = getIntent().getIntExtra("currentScore", 0);
        scoreView.setText(getString(R.string.end_score) + " " + score);

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

    public void goToLeaderboard(View view) {
        Intent highscoreActivity = new Intent(view.getContext(), HighscoreActivity.class);
        startActivity(highscoreActivity);
        finish();
    }

    private boolean isPlayerEligible(int score) {
        List<RecordEntry> records = db.getFiveHighestRecords();
        for(RecordEntry rec : records) {
            if(score >= rec.getScore()) {
                return true;
            }
        }
        return false;
    }

    public void goBack(View view) {
        finish();
    }

    public void confirm(View view) {
        String text = editName.getText().toString();
        if(text.isBlank()) {
            warningView.setVisibility(View.VISIBLE);
            return;
        }

        int length = text.length();
        if(length < 3 || length > 16) {
            warningView.setVisibility(View.VISIBLE);
            return;
        }

        db.addRecord(new RecordEntry(text, score));
        editName.setVisibility(View.INVISIBLE);
        warningView.setVisibility(View.INVISIBLE);
        outcomeView.setText(R.string.end_option3);
        btn.setVisibility(View.INVISIBLE);
    }
}