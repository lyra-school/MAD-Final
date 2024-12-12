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

    private Button button;

    private View red;
    private View blue;
    private View yellow;
    private View green;

    private ImageView up;
    private ImageView right;
    private ImageView down;
    private ImageView left;

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

        red = findViewById(R.id.top_choice);
        blue = findViewById(R.id.right_choice);
        yellow = findViewById(R.id.bottom_choice);
        green = findViewById(R.id.left_choice);

        up = findViewById(R.id.up);
        left = findViewById(R.id.left);
        down = findViewById(R.id.down);
        right = findViewById(R.id.right);

        // get reference to step detector in the device
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /* use gyroscope instead of accelerometer because I found out that virtual accelerometer doesn't
         record movement on Y axis well -- there's no meaningful value if you turn a landscape phone
         towards its "bottom" nor "top" */
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        button = findViewById(R.id.nextBtn);

        correctSequence = getIntent().getIntArrayExtra("sequence");
        currentScore = getIntent().getIntExtra("currentScore", 0);

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

    private void lockChoice(int dir) {
        gameOnHold = true;
        sensorManager.unregisterListener(this, sensor);
        hideSequence(currentDir);
        showSequence(dir);
        checkChoice(dir);
        button.setVisibility(View.VISIBLE);
    }

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
        Intent intent = getIntent();
        intent.putExtra("okay", isOkay);
        intent.putExtra("currentScore", currentScore);
        if (!isOkay) {
            Intent nextPage = new Intent(this, EndActivity.class);
            nextPage.putExtra("currentScore", currentScore);
            startActivity(nextPage);
        }
        setResult(RESULT_OK, intent);
        finish();
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

    @Override
    public void onResume() {
        super.onResume();
        if(!gameOnHold) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

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