package com.example.mad_final;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class HighscoreActivity extends AppCompatActivity {
    private TextView tv;
    private TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_highscore);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv = findViewById(R.id.names);
        tv2 = findViewById(R.id.scores);

        ScoreDatabaseHandler db = new ScoreDatabaseHandler(this);
        List<RecordEntry> records = db.getFiveHighestRecords();
        int currentPos = 1;
        for(RecordEntry rec : records) {
            tv.setText(tv.getText().toString() + currentPos + ". " + rec.getName() + "\n");
            tv2.setText(tv2.getText().toString() + rec.getScore() + "\n");
            currentPos++;
        }
    }

    public void goBack(View view) {
        finish();
    }
}