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
    // constants needed for managing the sequence count
    private final int SEQUENCE_BASE = 4;
    private final int SEQUENCE_INCREASE = 2;

    // used to identify two-way data sharing between child activity
    private final int REQUEST_CODE = 1;

    // used in display and sequence calculation
    private int[] sequence;
    private int sequenceSize;
    private int currentMultiplier = 0;
    private int currentSequenceViewed = 0;
    private int currentScore = 0;


    // references to widgets on screen
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

        // get refs to all necessary widgets
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

        // create a sequence
        populateSequenceList(currentMultiplier);
    }

    // creates the game activity with two-way communication, sharing the current score and
    // sequence
    public void goToGame(View view) {
        Intent gameActivity = new Intent(view.getContext(), GameActivity.class);
        gameActivity.putExtra("sequence", sequence);
        gameActivity.putExtra("currentScore", currentScore);

        // https://stackoverflow.com/questions/20114485/use-onactivityresult-android
        // this method is deprecated but the "newer" one I found, with the launcher, looks
        // poorly documented
        startActivityForResult(gameActivity, REQUEST_CODE);
    }

    // watch again button - restart the sequence viewing
    public void repeat(View view) {
        startViewing();
    }

    // randomly generate the sequence
    // usually, these games have a check against generating multiple of the same number in a row
    // but the lab sheet specifies that it must be "completely random"
    private void populateSequenceList(int multiplier) {
        sequenceSize = SEQUENCE_BASE + (SEQUENCE_INCREASE * multiplier);
        Random random = new Random();
        sequence = new int[sequenceSize];
        for(int i = 0; i < sequenceSize; i++) {
            sequence[i] = random.nextInt(4);
        }
    }

    // handles actions after the child activity closes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE  && resultCode  == RESULT_OK) {
                // if isOk is true, then that means that the player didn't mess up in the game activity
                // and therefore continue the game
                boolean isOk = data.getBooleanExtra("okay", false);
                if(isOk) {
                    // keep track of the current score for next time the game activity is opened
                    // also increase the number of sequence elements and make a new one
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
            // close in case of an exception
            finish();
        }
    }

    // used for changing colours of the views in display
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

    // used for changing colours of the views in display
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

    // make watch again and play buttons visible once sequence viewing ends
    private void finishViewing() {
        continueBtn.setVisibility(View.VISIBLE);
        repeatBtn.setVisibility(View.VISIBLE);
        viewBtn.setVisibility(View.INVISIBLE);
        text.setText(R.string.sequence_ready);
    }

    // make watch again and play buttons invisible once sequence viewing starts again
    private void startViewing() {
        continueBtn.setVisibility(View.INVISIBLE);
        repeatBtn.setVisibility(View.INVISIBLE);
        currentSequenceViewed = 0;
        text.setText(R.string.sequence_warning);
        viewBtn.setText(R.string.button_generate);
        viewBtn.setVisibility(View.VISIBLE);
    }

    // the logic that controls highlighting of a view in sequence
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