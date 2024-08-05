package com.busesari.pomodoro;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView timeText;
    ImageButton startButton;
    ImageButton stopButton;
    ImageButton resetButton;
    Button longBreak;
    Button shortBreak;
    TextView sessionText;
    int sessionCount;
    int timeLeft;
    boolean isRunning;

    Handler handler;
    Runnable runnable;
    private static final int WORK_TIME = 25 * 60;
    private static final int SHORT_BREAK_TIME = 5 * 60;
    private static final int LONG_BREAK_TIME = 15 * 60;

    private TimerService timerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        timerService = new TimerService(this);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);
        longBreak = findViewById(R.id.longBreak);
        shortBreak = findViewById(R.id.shortBreak);
        sessionText = findViewById(R.id.sessionText);
        timeText = findViewById(R.id.timeText);

        sessionCount = 0;
        timeLeft = WORK_TIME;
        isRunning = false;

        handler = new Handler();

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTimer(WORK_TIME); // Start focus timer by default
            }
        });
        stopButton.setOnClickListener(v -> stopTimer());
        resetButton.setOnClickListener(v -> resetTimer(WORK_TIME));
        longBreak.setOnClickListener(v -> startBreakTimer(LONG_BREAK_TIME));
        shortBreak.setOnClickListener(v -> startBreakTimer(SHORT_BREAK_TIME));

        updateTimerText();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startTimer(int seconds) {
        stopTimer(); // Stop any existing timers
        timeLeft = seconds;
        isRunning = true;
        timerService.startTimer(seconds); // Start the timer service
        runnable = new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    updateTimerText();
                    handler.postDelayed(this, 1000);
                } else {
                    // Check if it's a focus timer
                    if (seconds == WORK_TIME) {
                        sessionCount++;
                        sessionText.setText("Sessions: " + sessionCount);
                    }
                    isRunning = false;
                }
            }
        };
        handler.post(runnable);
    }


    private void startBreakTimer(int breakDuration) {
        stopTimer(); // Stop any existing timers
        timeLeft = breakDuration;
        isRunning = true;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    updateTimerText();
                    handler.postDelayed(this, 1000);
                } else {
                    isRunning = false;
                }
            }
        };
        handler.post(runnable);
    }

    private void stopTimer() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        isRunning = false;
    }

    private void resetTimer(int seconds) {
        stopTimer();
        timeLeft = seconds;
        updateTimerText();
    }

    private void updateTimerText() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timeText.setText(String.format("%02d:%02d", minutes, seconds));
    }
}
