package com.example.cargame;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import com.example.cargame.Logic.GameManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final int ROWS = 8;
    private static final int COLS = 3;
    private static final long UPDATE_DELAY = 500;

    private GameManager gameManager;
    private FrameLayout[] lanes;
    private AppCompatImageView[] hearts;
    private TextView scoreTextView;
    private Handler handler;
    private Runnable updateRunnable;
    private boolean isNightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameManager = new GameManager(ROWS, COLS);
        initializeViews();
        setupUpdateLoop();
    }

    private void initializeViews() {
        lanes = new FrameLayout[]{
                findViewById(R.id.main_Left_line),
                findViewById(R.id.main_Middle_line),
                findViewById(R.id.main_Right_line)
        };

        hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };

        scoreTextView = findViewById(R.id.score_text);

        FloatingActionButton leftButton = findViewById(R.id.left_ARROW_btn);
        FloatingActionButton rightButton = findViewById(R.id.right_ARROW_btn);

        leftButton.setOnClickListener(v -> gameManager.moveTractor(true));
        rightButton.setOnClickListener(v -> gameManager.moveTractor(false));

        MaterialButton toggleDayNightButton = findViewById(R.id.toggle_day_night);
        toggleDayNightButton.setOnClickListener(v -> toggleDayNightMode());
    }

    private void setupUpdateLoop() {
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                gameManager.update();
                updateUI();
                if (gameManager.isGameOver()) {
                    restartGame();
                } else {
                    handler.postDelayed(this, UPDATE_DELAY);
                }
            }
        };
        handler.post(updateRunnable);
    }

    private void updateUI() {
        int[][] logicalGrid = gameManager.getGrid();
        for (int i = 0; i < COLS; i++) {
            lanes[i].removeAllViews();
            for (int j = 0; j < ROWS; j++) {
                if (logicalGrid[j][i] != GameManager.CLEAR) {
                    AppCompatImageView imageView = new AppCompatImageView(this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.topMargin = j * (lanes[i].getHeight() / ROWS);
                    imageView.setLayoutParams(params);

                    switch (logicalGrid[j][i]) {
                        case GameManager.TRACTOR:
                            imageView.setImageResource(R.drawable.tractor);
                            break;
                        case GameManager.BLOCK:
                            imageView.setImageResource(R.drawable.scarecrow);
                            if (j == ROWS - 1) {
                                imageView.setAlpha(0.5f);
                            }
                            break;
                    }

                    lanes[i].addView(imageView);
                }
            }
        }

        scoreTextView.setText("Score: " + gameManager.getScore());
        updateLives();
    }

    private void updateLives() {
        int lives = gameManager.getLives();
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisibility(i < lives ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void toggleDayNightMode() {
        isNightMode = !isNightMode;
        findViewById(R.id.main).setBackgroundResource(
                isNightMode ? R.drawable.night_background : R.drawable.day_background
        );
        MaterialButton toggleButton = findViewById(R.id.toggle_day_night);
        toggleButton.setText(isNightMode ? "Day Mode" : "Night Mode");
    }

    private void restartGame() {
        gameManager.resetGame();
        updateUI();
        handler.postDelayed(updateRunnable, UPDATE_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}
