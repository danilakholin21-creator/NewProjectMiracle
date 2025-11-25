package com.example.newprojectmiracle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // ДОБАВЬТЕ ЭТОТ ИМПОРТ

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsActivity extends AppCompatActivity {

    private TextView tvTotalRepairs, tvTotalDetailing, tvRepairsInProgress,
            tvDetailingInProgress, tvRepairsCompleted, tvDetailingCompleted;
    private LinearLayout containerAllRepairRequests, containerAllDetailingRequests;
    private TextView tvClock;
    private Button btnBackToMain;
    private DatabaseHelper databaseHelper;
    private Handler clockHandler = new Handler();
    private Runnable clockRunnable;

    // Добавлен BroadcastReceiver для обновления статистики
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("REQUEST_UPDATED".equals(intent.getAction())) {
                loadStatistics();
                loadAllRequests();
                Toast.makeText(StatsActivity.this, "Статистика обновлена", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        databaseHelper = new DatabaseHelper(this);

        tvTotalRepairs = findViewById(R.id.tvTotalRepairs);
        tvTotalDetailing = findViewById(R.id.tvTotalDetailing);
        tvRepairsInProgress = findViewById(R.id.tvRepairsInProgress);
        tvDetailingInProgress = findViewById(R.id.tvDetailingInProgress);
        tvRepairsCompleted = findViewById(R.id.tvRepairsCompleted);
        tvDetailingCompleted = findViewById(R.id.tvDetailingCompleted);

        containerAllRepairRequests = findViewById(R.id.containerAllRepairRequests);
        containerAllDetailingRequests = findViewById(R.id.containerAllDetailingRequests);

        tvClock = findViewById(R.id.tvClock);

        btnBackToMain = findViewById(R.id.btnBackToMain);

        updateClock();

        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startClock();
        loadStatistics();
        loadAllRequests();
    }

    private void startClock() {
        updateClock();

        clockRunnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                clockHandler.postDelayed(this, 1000);
            }
        };
        clockHandler.post(clockRunnable);
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        if (tvClock != null) {
            tvClock.setText(currentTime);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
        loadAllRequests();
        updateClock();

    }

    @Override
    protected void onPause() {
        super.onPause();


        try {
            unregisterReceiver(updateReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private void loadStatistics() {
        int totalRepairs = databaseHelper.getRepairRequestsCount();
        int totalDetailing = databaseHelper.getDetailingRequestsCount();
        int repairsInProgress = databaseHelper.getRequestsInProgressCount(DatabaseHelper.TABLE_REPAIRS);
        int detailingInProgress = databaseHelper.getRequestsInProgressCount(DatabaseHelper.TABLE_DETAILING);
        int repairsCompleted = databaseHelper.getCompletedRequestsCount(DatabaseHelper.TABLE_REPAIRS);
        int detailingCompleted = databaseHelper.getCompletedRequestsCount(DatabaseHelper.TABLE_DETAILING);

        tvTotalRepairs.setText("Общее количество заявок на ремонт: " + totalRepairs);
        tvTotalDetailing.setText("Общее количество заявок на детейлинг: " + totalDetailing);
        tvRepairsInProgress.setText("Заявки на ремонт в процессе: " + repairsInProgress);
        tvDetailingInProgress.setText("Заявки на детейлинг в процессе: " + detailingInProgress);
        tvRepairsCompleted.setText("Заявки на ремонт выполнены: " + repairsCompleted);
        tvDetailingCompleted.setText("Заявки на детейлинг выполнены: " + detailingCompleted);
    }

    private void loadAllRequests() {
        containerAllRepairRequests.removeAllViews();
        containerAllDetailingRequests.removeAllViews();

        List<String> repairRequests = databaseHelper.getAllRepairRequests();
        if (repairRequests.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Нет заявок");
            emptyText.setTextColor(getResources().getColor(android.R.color.white));
            containerAllRepairRequests.addView(emptyText);
        } else {
            for (String request : repairRequests) {
                TextView textView = createRequestTextView(request);
                containerAllRepairRequests.addView(textView);
            }
        }

        List<String> detailingRequests = databaseHelper.getAllDetailingRequests();
        if (detailingRequests.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Нет заявок");
            emptyText.setTextColor(getResources().getColor(android.R.color.white));
            containerAllDetailingRequests.addView(emptyText);
        } else {
            for (String request : detailingRequests) {
                TextView textView = createRequestTextView(request);
                containerAllDetailingRequests.addView(textView);
            }
        }
    }

    private TextView createRequestTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(12);
        textView.setPadding(8, 2, 8, 2);
        return textView;
    }
}
