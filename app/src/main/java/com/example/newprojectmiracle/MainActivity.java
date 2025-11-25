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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnCreateRepair, btnCreateDetailing, btnStats;
    private LinearLayout containerRepairRequests, containerDetailingRequests;
    private TextView tvClock;
    private DatabaseHelper databaseHelper;
    private Handler clockHandler = new Handler();
    private Runnable clockRunnable;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("REQUEST_UPDATED".equals(intent.getAction())) {
                loadActiveRequests();
                Toast.makeText(MainActivity.this, "Список заявок обновлен", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        btnCreateRepair = findViewById(R.id.btnCreateRepair);
        btnCreateDetailing = findViewById(R.id.btnCreateDetailing);
        btnStats = findViewById(R.id.btnStats);
        containerRepairRequests = findViewById(R.id.containerRepairRequests);
        containerDetailingRequests = findViewById(R.id.containerDetailingRequests);
        tvClock = findViewById(R.id.tvClock);

        updateClock();

        if (btnCreateRepair == null || btnCreateDetailing == null || btnStats == null) {
            Toast.makeText(this, "Ошибка: не все кнопки найдены в разметке", Toast.LENGTH_LONG).show();
            return;
        }

        btnCreateRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateZayavkaActivity.class);
                startActivity(intent);
            }
        });

        btnCreateDetailing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailingActivity.class);
                startActivity(intent);
            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });

        startClock();
        loadActiveRequests();
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
        loadActiveRequests();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private void loadActiveRequests() {
        if (containerRepairRequests != null) {
            containerRepairRequests.removeAllViews();
        }
        if (containerDetailingRequests != null) {
            containerDetailingRequests.removeAllViews();
        }

        // Загружаем заявки на ремонт с ID
        List<RequestInfo> repairRequests = databaseHelper.getActiveRepairRequestsWithId();
        if (repairRequests.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Нет активных заявок");
            emptyText.setTextColor(getResources().getColor(android.R.color.white));
            emptyText.setTextSize(14);
            emptyText.setPadding(8, 4, 8, 4);
            if (containerRepairRequests != null) {
                containerRepairRequests.addView(emptyText);
            }
        } else {
            for (RequestInfo requestInfo : repairRequests) {
                TextView textView = createRequestTextView(requestInfo.getText(), requestInfo.getId(), "repair");
                if (containerRepairRequests != null) {
                    containerRepairRequests.addView(textView);
                }
            }
        }


        List<RequestInfo> detailingRequests = databaseHelper.getActiveDetailingRequestsWithId();
        if (detailingRequests.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Нет активных заявок");
            emptyText.setTextColor(getResources().getColor(android.R.color.white));
            emptyText.setTextSize(14);
            emptyText.setPadding(8, 4, 8, 4);
            if (containerDetailingRequests != null) {
                containerDetailingRequests.addView(emptyText);
            }
        } else {
            for (RequestInfo requestInfo : detailingRequests) {
                TextView textView = createRequestTextView(requestInfo.getText(), requestInfo.getId(), "detailing");
                if (containerDetailingRequests != null) {
                    containerDetailingRequests.addView(textView);
                }
            }
        }
    }

    private TextView createRequestTextView(String text, final long requestId, final String requestType) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(14);
        textView.setPadding(8, 4, 8, 4);

        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setBackgroundResource(android.R.drawable.list_selector_background);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestDetailActivity.class);
                intent.putExtra("REQUEST_ID", requestId);
                intent.putExtra("REQUEST_TYPE", requestType);
                startActivity(intent);
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showQuickActions(requestId, requestType);
                return true;
            }
        });

        return textView;
    }

    private void showQuickActions(final long requestId, final String requestType) {
        Toast.makeText(this, "Долгое нажатие на заявку ID: " + requestId, Toast.LENGTH_SHORT).show();
    }
}