package com.example.newprojectmiracle;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class DetailingActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextEmail, editTextCarModel, editTextDate;
    private Spinner spinnerTime;
    private Button btnCreate;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailing);

        databaseHelper = new DatabaseHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCarModel = findViewById(R.id.editTextCarModel);
        editTextDate = findViewById(R.id.editTextDate);
        spinnerTime = findViewById(R.id.spinnerTime);
        btnCreate = findViewById(R.id.btnCreate);

        setupDateField();
        setupTimeSpinner();

        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDetailingRequest();
            }
        });
    }

    private void setupDateField() {
        editTextDate.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String text = s.toString();

                String digitsOnly = text.replaceAll("[^\\d]", "");

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i == 2 || i == 4) {
                        formatted.append(".");
                    }
                    if (i < 8) {
                        formatted.append(digitsOnly.charAt(i));
                    }
                }

                if (formatted.length() >= 2) {
                    String dayStr = formatted.substring(0, 2).replaceAll("[^\\d]", "");
                    if (!dayStr.isEmpty()) {
                        int day = Integer.parseInt(dayStr);
                        if (day > 31) {
                            formatted.replace(0, 2, "31");
                        } else if (day < 1 && dayStr.length() == 2) {
                            formatted.replace(0, 2, "01");
                        }
                    }
                }

                if (formatted.length() >= 5) {
                    String monthStr = formatted.substring(3, 5).replaceAll("[^\\d]", "");
                    if (!monthStr.isEmpty()) {
                        int month = Integer.parseInt(monthStr);
                        if (month > 12) {
                            formatted.replace(3, 5, "12");
                        } else if (month < 1 && monthStr.length() == 2) {
                            formatted.replace(3, 5, "01");
                        }
                    }
                }

                if (formatted.length() >= 10) {String yearStr = formatted.substring(6, 10).replaceAll("[^\\d]", "");
                    if (!yearStr.isEmpty()) {
                        int year = Integer.parseInt(yearStr);
                        if (year > 2100) {
                            formatted.replace(6, 10, "2100");
                        } else if (year < 1900 && yearStr.length() == 4) {
                            formatted.replace(6, 10, "1900");
                        }
                    }
                }

                if (!text.equals(formatted.toString())) {
                    editTextDate.setText(formatted.toString());
                    editTextDate.setSelection(formatted.length());
                }

                isFormatting = false;
            }
        });
    }

    private void setupTimeSpinner() {

        String[] timeSlots = new String[]{
                "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        };


        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                timeSlots
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
                return view;
            }
        };

        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);
    }

    private boolean isValidDate(String date) {
        if (date.length() != 10) return false;

        try {
            String[] parts = date.split("\\.");
            if (parts.length != 3) return false;

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            if (day < 1 || day > 31) return false;
            if (month < 1 || month > 12) return false;
            if (year < 1900 || year > 2100) return false;

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            return day <= maxDays;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void createDetailingRequest() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String carModel = editTextCarModel.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String time = spinnerTime.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || carModel.isEmpty() || date.isEmpty() || !isValidDate(date)) {
            Toast.makeText(this, "Заполните все поля правильно! Дата должна быть в формате ДД.ММ.ГГГГ (день 1-31, месяц 1-12, год 1900-2100)", Toast.LENGTH_LONG).show();
            return;
        }


        String dateTime = date + " " + time;

        long id = databaseHelper.addDetailingRequest(name, phone, email, carModel, dateTime);

        if (id != -1) {
            Toast.makeText(this, "Заявка на детейлинг создана! ID: " + id, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка создания заявки!", Toast.LENGTH_SHORT).show();
        }
    }
}