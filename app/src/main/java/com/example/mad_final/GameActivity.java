package com.example.mad_final;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    // storing device sensors
    private SensorManager sensorManager;
    private Sensor sensor;

    // references to needed views
    private Button button;

    private View red;
    private View blue;
    private View yellow;
    private View green;

    private ImageView up;
    private ImageView right;
    private ImageView down;
    private ImageView left;

    // used to keep track of the player's inputs
    private int[] correctSequence = {};
    private boolean gameOnHold = false;
    private int currentIndex = 0;
    private int currentDir = 0;
    private int currentScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get refs to needed views
        red = findViewById(R.id.top_choice);
        blue = findViewById(R.id.right_choice);
        yellow = findViewById(R.id.bottom_choice);
        green = findViewById(R.id.left_choice);

        up = findViewById(R.id.up);
        left = findViewById(R.id.left);
        down = findViewById(R.id.down);
        right = findViewById(R.id.right);

        button = findViewById(R.id.nextBtn);

        // get reference to step detector in the device
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /* use gyroscope instead of accelerometer because I found out that virtual accelerometer doesn't
         record movement on Y axis well -- there's no meaningful value if you turn a landscape phone
         towards its "bottom" nor "top" */
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // load data from intent
        correctSequence = getIntent().getIntArrayExtra("sequence");
        currentScore = getIntent().getIntExtra("currentScore", 0);

        // start listening to the gyroscope
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // gyroscope values
    @Override
    public void onSensorChanged(SensorEvent event) {
        // left-right
        float changeY = event.values[0];
        // up-down
        float changeX = event.values[1];

        /* depending on the device's actual position, these are not
         mutually exclusive so always give precedence to X-axis by default */
        // up
        if(changeX > 2) {
            lockChoice(0);
        // down
        } else if(changeX < -2) {
            lockChoice(2);
        // left
        } else if(changeY > 2) {
            lockChoice(1);
        // right
        } else if(changeY < -2) {
            lockChoice(3);
        }
    }

    // resume the game by resetting the choice and listening to gyroscope again
    public void next(View view) {
        if(currentIndex == correctSequence.length) {
            finishActivity(true);
        }
        hideSequence(currentDir);
        gameOnHold = false;
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        button.setVisibility(View.INVISIBLE);
    }

    // lock in a choice by disabling the gyroscope for the moment and displaying the choice
    private void lockChoice(int dir) {
        gameOnHold = true;
        sensorManager.unregisterListener(this, sensor);
        hideSequence(currentDir);
        showSequence(dir);
        checkChoice(dir);
        button.setVisibility(View.VISIBLE);
    }

    // logic to check if the player is doing the correct sequence
    private void checkChoice(int dir) {
        if(dir == correctSequence[currentIndex]) {
            currentIndex++;
            currentDir = dir;
            currentScore++;
        } else {
            finishActivity(false);
        }

        if(currentIndex == correctSequence.length) {
            button.setText(R.string.button_finish);
        }
    }

    private void finishActivity(boolean isOkay) {
        // pass data back into the parent activity
        Intent intent = getIntent();
        intent.putExtra("okay", isOkay);
        intent.putExtra("currentScore", currentScore);
        if (!isOkay) {
            // pass data into the child activity
            Intent nextPage = new Intent(this, EndActivity.class);
            nextPage.putExtra("currentScore", currentScore);
            startActivity(nextPage);
        }
        setResult(RESULT_OK, intent);
        finish();
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

    // when user opens the app again, register sensor if a selection is in progress
    @Override
    public void onResume() {
        super.onResume();
        if(!gameOnHold) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // when user minimizes the app, unregister sensor if a selection is in progress
    // no need to do anything if the selection is locked in
    @Override
    public void onPause() {
        super.onPause();
        if(!gameOnHold) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // unused
    }
}