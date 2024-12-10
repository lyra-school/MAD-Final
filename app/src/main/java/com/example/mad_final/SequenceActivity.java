package com.example.mad_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class SequenceActivity extends AppCompatActivity {
    private final int SEQUENCE_BASE = 4;
    private final int SEQUENCE_INCREASE = 2;
    private final int REQUEST_CODE = 1;

    private int[] sequence;
    private int sequenceSize;
    private int currentMultiplier = 0;
    private int currentSequenceViewed = 0;
    private int currentScore = 0;


    private View red;
    private View blue;
    private View yellow;
    private View green;

    private ImageView up;
    private ImageView right;
    private ImageView down;
    private ImageView left;

    private Button continueBtn;
    private Button repeatBtn;
    private Button viewBtn;

    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sequence);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        red = findViewById(R.id.top_choice);
        blue = findViewById(R.id.right_choice);
        yellow = findViewById(R.id.bottom_choice);
        green = findViewById(R.id.left_choice);

        up = findViewById(R.id.up);
        left = findViewById(R.id.left);
        down = findViewById(R.id.down);
        right = findViewById(R.id.right);

        continueBtn = findViewById(R.id.playBtn);
        repeatBtn = findViewById(R.id.watchBtn);
        viewBtn = findViewById(R.id.viewBtn);

        text = findViewById(R.id.sequence_text);

        populateSequenceList(currentMultiplier);
    }

    public void goToGame(View view) {
        Intent gameActivity = new Intent(view.getContext(), GameActivity.class);
        gameActivity.putExtra("sequence", sequence);
        gameActivity.putExtra("currentScore", currentScore);

        // https://stackoverflow.com/questions/20114485/use-onactivityresult-android
        // this method is deprecated but the "newer" one I found, with the launcher, looks
        // poorly documented
        startActivityForResult(gameActivity, REQUEST_CODE);
    }

    public void repeat(View view) {
        startViewing();
    }

    private void populateSequenceList(int multiplier) {
        sequenceSize = SEQUENCE_BASE + (SEQUENCE_INCREASE * multiplier);
        Random random = new Random();
        sequence = new int[sequenceSize];
        for(int i = 0; i < sequenceSize; i++) {
            sequence[i] = random.nextInt(4);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE  && resultCode  == RESULT_OK) {
                boolean isOk = data.getBooleanExtra("okay", false);
                if(isOk) {
                    currentScore = data.getIntExtra("currentScore", 0);
                    currentMultiplier++;
                    populateSequenceList(currentMultiplier);
                    startViewing();
                } else {
                    finish();
                }
            } else {
                // not sure if the if statement exists as some sort of a failsafe in the original
                // answer, so close the activity if this is meant to indicate something going wrong
                finish();
            }
        } catch(Exception e) {
            finish();
        }
    }

    public void showSequence(int i) {
        switch(i) {
            case 0:
                red.setBackgroundColor(getResources().getColor( R.color.light_red));
                up.setColorFilter(getResources().getColor( R.color.black));
                break;
            case 1:
                blue.setBackgroundColor(getResources().getColor( R.color.light_blue));
                right.setColorFilter(getResources().getColor( R.color.black));
                break;
            case 2:
                yellow.setBackgroundColor(getResources().getColor( R.color.light_yellow));
                down.setColorFilter(getResources().getColor( R.color.black));
                break;
            case 3:
                green.setBackgroundColor(getResources().getColor( R.color.light_green));
                left.setColorFilter(ContextCompat.getColor(this, R.color.black));
                break;
        }
    }

    public void hideSequence(int i) {
        switch(i) {
            case 0:
                red.setBackgroundColor(getResources().getColor( R.color.red));
                up.setColorFilter(getResources().getColor( R.color.white));
                break;
            case 1:
                blue.setBackgroundColor(getResources().getColor( R.color.blue));
                right.setColorFilter(getResources().getColor( R.color.white));
                break;
            case 2:
                yellow.setBackgroundColor(getResources().getColor( R.color.yellow));
                down.setColorFilter(getResources().getColor( R.color.white));
                break;
            case 3:
                green.setBackgroundColor(getResources().getColor( R.color.green));
                left.setColorFilter(getResources().getColor( R.color.white));
                break;
        }
    }

    private void finishViewing() {
        continueBtn.setVisibility(View.VISIBLE);
        repeatBtn.setVisibility(View.VISIBLE);
        viewBtn.setVisibility(View.INVISIBLE);
        text.setText(R.string.sequence_ready);
    }

    private void startViewing() {
        continueBtn.setVisibility(View.INVISIBLE);
        repeatBtn.setVisibility(View.INVISIBLE);
        currentSequenceViewed = 0;
        text.setText(R.string.sequence_warning);
        viewBtn.setText(R.string.button_generate);
        viewBtn.setVisibility(View.VISIBLE);
    }

    public void viewSequence(View view) {
        if(currentSequenceViewed == sequenceSize - 1) {
            viewBtn.setText(R.string.button_finish);
        }

        if(currentSequenceViewed == 0) {
            showSequence(sequence[currentSequenceViewed]);
            viewBtn.setText(R.string.button_next);
            currentSequenceViewed++;
        } else if(currentSequenceViewed == sequenceSize) {
            hideSequence(sequence[currentSequenceViewed - 1]);
            finishViewing();
        } else {
            hideSequence(sequence[currentSequenceViewed - 1]);
            showSequence(sequence[currentSequenceViewed]);
            currentSequenceViewed++;
        }
    }
}