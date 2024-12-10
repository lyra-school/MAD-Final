package com.example.mad_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EndActivity extends AppCompatActivity {
    private int score;

    private TextView scoreView;
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

        score = getIntent().getIntExtra("currentScore", 0);
        scoreView.setText(getString(R.string.end_score) + " " + score);
    }

    public void goToLeaderboard(View view) {
        Intent highscoreActivity = new Intent(view.getContext(), HighscoreActivity.class);
        startActivity(highscoreActivity);
        finish();
    }

    public void goBack(View view) {
        finish();
    }
}