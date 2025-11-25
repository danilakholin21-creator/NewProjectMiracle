package com.example.newprojectmiracle;

import android.content.Intent;
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

public class RequestDetailActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextModel, editTextDate, editTextDescription, editTextEmail;
    private Spinner spinnerTime, spinnerStatus;
    private Button btnUpdate, btnDelete, btnBack;
    private TextView tvTitle;

    private DatabaseHelper databaseHelper;
    private long requestId;
    private String requestType;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        databaseHelper = new DatabaseHelper(this);

        requestId = getIntent().getLongExtra("REQUEST_ID", -1);
        requestType = getIntent().getStringExtra("REQUEST_TYPE");

        if (requestId == -1 || requestType == null) {
            Toast.makeText(this, "Ошибка загрузки заявки", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupDateField();
        setupTimeSpinner();
        setupStatusSpinner();
        loadRequestData();
        setupClickListeners();
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextModel = findViewById(R.id.editTextModel);
        editTextDate = findViewById(R.id.editTextDate);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextEmail = findViewById(R.id.editTextEmail);
        spinnerTime = findViewById(R.id.spinnerTime);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        if ("repair".equals(requestType)) {
            tvTitle.setText("Детали заявки на ремонт");
            editTextDescription.setVisibility(View.VISIBLE);
            editTextEmail.setVisibility(View.GONE);
        } else {
            tvTitle.setText("Детали заявки на детейлинг");
            editTextDescription.setVisibility(View.GONE);
            editTextEmail.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    updateRequest();
                } else {
                    enableEditing();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRequest();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void enableEditing() {
        isEditing = true;
        btnUpdate.setText("Сохранить");
        btnUpdate.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));

        editTextName.setEnabled(true);
        editTextPhone.setEnabled(true);
        editTextModel.setEnabled(true);
        editTextDate.setEnabled(true);
        spinnerTime.setEnabled(true);
        spinnerStatus.setEnabled(true);

        if ("repair".equals(requestType)) {
            editTextDescription.setEnabled(true);
        } else {editTextEmail.setEnabled(true);
        }

        Toast.makeText(this, "Режим редактирования включен", Toast.LENGTH_SHORT).show();
    }

    private void disableEditing() {
        isEditing = false;
        btnUpdate.setText("Редактировать");
        btnUpdate.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.orange));

        editTextName.setEnabled(false);
        editTextPhone.setEnabled(false);
        editTextModel.setEnabled(false);
        editTextDate.setEnabled(false);
        spinnerTime.setEnabled(false);
        spinnerStatus.setEnabled(false);
        editTextDescription.setEnabled(false);
        editTextEmail.setEnabled(false);
    }

    private void loadRequestData() {
        if ("repair".equals(requestType)) {
            RepairRequest repairRequest = databaseHelper.getRepairRequest(requestId);
            if (repairRequest != null) {
                fillRepairRequestData(repairRequest);
            } else {
                Toast.makeText(this, "Заявка не найдена", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            DetailingRequest detailingRequest = databaseHelper.getDetailingRequest(requestId);
            if (detailingRequest != null) {
                fillDetailingRequestData(detailingRequest);
            } else {
                Toast.makeText(this, "Заявка не найдена", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        disableEditing();
    }

    private void fillRepairRequestData(RepairRequest request) {
        editTextName.setText(request.getName());
        editTextPhone.setText(request.getPhone());
        editTextModel.setText(request.getDeviceModel());
        editTextDescription.setText(request.getDescription());

        if (request.getDate() != null && request.getDate().contains(" ")) {
            String[] dateTimeParts = request.getDate().split(" ");
            if (dateTimeParts.length == 2) {
                editTextDate.setText(dateTimeParts[0]);
                setSpinnerTime(dateTimeParts[1]);
            } else {
                editTextDate.setText(request.getDate());
            }
        } else {
            editTextDate.setText(request.getDate());
        }


        setSpinnerStatus(request.getStatus());
    }

    private void fillDetailingRequestData(DetailingRequest request) {
        editTextName.setText(request.getName());
        editTextPhone.setText(request.getPhone());
        editTextEmail.setText(request.getEmail());
        editTextModel.setText(request.getCarModel());

        if (request.getDate() != null && request.getDate().contains(" ")) {
            String[] dateTimeParts = request.getDate().split(" ");
            if (dateTimeParts.length == 2) {
                editTextDate.setText(dateTimeParts[0]);
                setSpinnerTime(dateTimeParts[1]);
            } else {
                editTextDate.setText(request.getDate());
            }
        } else {
            editTextDate.setText(request.getDate());
        }


        setSpinnerStatus(request.getStatus());
    }

    private void setSpinnerTime(String time) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerTime.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(time);
            if (position >= 0) {
                spinnerTime.setSelection(position);
            } else {
                spinnerTime.setSelection(0);
            }
        }
    }

    private void setSpinnerStatus(String status) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerStatus.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(status);
            if (position >= 0) {
                spinnerStatus.setSelection(position);
            } else {

                spinnerStatus.setSelection(0);}
        }
    }

    private void updateRequest() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String model = editTextModel.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String time = spinnerTime.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || model.isEmpty() || date.isEmpty() || !isValidDate(date)) {
            Toast.makeText(this, "Заполните все обязательные поля! Дата должна быть в формате ДД.ММ.ГГГГ (день 1-31, месяц 1-12, год 1900-2100)", Toast.LENGTH_LONG).show();
            return;
        }

        String dateTime = date + " " + time;
        boolean success = false;

        if ("repair".equals(requestType)) {
            String description = editTextDescription.getText().toString().trim();
            success = databaseHelper.updateRepairRequest(requestId, name, phone, "", model, dateTime, description);


            if (success) {
                databaseHelper.updateRepairStatus(requestId, status);
            }
        } else {
            String email = editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Заполните поле Email", Toast.LENGTH_SHORT).show();
                return;
            }
            success = databaseHelper.updateDetailingRequest(requestId, name, phone, email, model, dateTime);


            if (success) {
                databaseHelper.updateDetailingStatus(requestId, status);
            }
        }

        if (success) {
            Toast.makeText(this, "Заявка обновлена! Статус: " + status, Toast.LENGTH_SHORT).show();
            disableEditing();


            loadRequestData();
        } else {
            Toast.makeText(this, "Ошибка обновления заявки!", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteRequest() {
        boolean success;

        if ("repair".equals(requestType)) {
            success = databaseHelper.deleteRepairRequest(requestId);
        } else {
            success = databaseHelper.deleteDetailingRequest(requestId);
        }

        if (success) {
            Toast.makeText(this, "Заявка удалена!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка удаления заявки!", Toast.LENGTH_SHORT).show();
        }
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
                        if (day > 31) formatted.replace(0, 2, "31");
                        else if (day < 1 && dayStr.length() == 2) formatted.replace(0, 2, "01");
                    }
                }

                if (formatted.length() >= 5) {
                    String monthStr = formatted.substring(3, 5).replaceAll("[^\\d]", "");
                    if (!monthStr.isEmpty()) {
                        int month = Integer.parseInt(monthStr);
                        if (month > 12) formatted.replace(3, 5, "12");
                        else if (month < 1 && monthStr.length() == 2) formatted.replace(3, 5, "01");
                    }
                }

                if (formatted.length() >= 10) {
                    String yearStr = formatted.substring(6, 10).replaceAll("[^\\d]", "");
                    if (!yearStr.isEmpty()) {
                        int year = Integer.parseInt(yearStr);
                        if (year > 2100) formatted.replace(6, 10, "2100");
                        else if (year < 1900 && yearStr.length() == 4) formatted.replace(6, 10, "1900");
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
        String[] timeSlots = {"12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

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

    private void setupStatusSpinner() {
        String[] statusOptions = {"в работе", "выполнено"};

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
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


                if (position == 0) {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
                } else {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                }return view;
            }
        };

        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
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
}