package com.example.newprojectmiracle;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RequestsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REPAIRS = "RepairRequests";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_DEVICE_MODEL = "device_model";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    public static final String TABLE_DETAILING = "DetailingRequests";
    public static final String COLUMN_CAR_MODEL = "car_model";

    private static final String CREATE_TABLE_REPAIRS =
            "CREATE TABLE " + TABLE_REPAIRS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PHONE + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_DEVICE_MODEL + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_STATUS + " TEXT DEFAULT 'в работе'" + ");";

    private static final String CREATE_TABLE_DETAILING =
            "CREATE TABLE " + TABLE_DETAILING + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PHONE + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_CAR_MODEL + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_STATUS + " TEXT DEFAULT 'в работе'" + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_REPAIRS);
        db.execSQL(CREATE_TABLE_DETAILING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPAIRS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILING);
        onCreate(db);
    }

    public long addRepairRequest(String name, String phone, String email,
                                 String deviceModel, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_DEVICE_MODEL, deviceModel);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);

        long id = db.insert(TABLE_REPAIRS, null, values);
        db.close();
        return id;
    }

    public long addDetailingRequest(String name, String phone, String email,
                                    String carModel, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_CAR_MODEL, carModel);
        values.put(COLUMN_DATE, date);

        long id = db.insert(TABLE_DETAILING, null, values);
        db.close();
        return id;
    }

    @SuppressLint("Range")
    public RepairRequest getRepairRequest(long id) {SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REPAIRS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        RepairRequest request = null;
        if (cursor != null && cursor.moveToFirst()) {
            request = new RepairRequest();
            request.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            request.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            request.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            request.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            request.setDeviceModel(cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_MODEL)));
            request.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
            request.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            request.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
            cursor.close();
        }
        db.close();
        return request;
    }

    @SuppressLint("Range")
    public DetailingRequest getDetailingRequest(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DETAILING, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        DetailingRequest request = null;
        if (cursor != null && cursor.moveToFirst()) {
            request = new DetailingRequest();
            request.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            request.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            request.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            request.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            request.setCarModel(cursor.getString(cursor.getColumnIndex(COLUMN_CAR_MODEL)));
            request.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
            request.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
            cursor.close();
        }
        db.close();
        return request;
    }

    public boolean updateRepairRequest(long id, String name, String phone, String email,
                                       String deviceModel, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_DEVICE_MODEL, deviceModel);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);

        int rowsAffected = db.update(TABLE_REPAIRS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean updateDetailingRequest(long id, String name, String phone, String email,
                                          String carModel, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_CAR_MODEL, carModel);
        values.put(COLUMN_DATE, date);

        int rowsAffected = db.update(TABLE_DETAILING, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean updateRepairStatus(long id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int rowsAffected = db.update(TABLE_REPAIRS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean updateDetailingStatus(long id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int rowsAffected = db.update(TABLE_DETAILING, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public String getRepairStatus(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String status = "";

        Cursor cursor = db.query(TABLE_REPAIRS,
                new String[]{COLUMN_STATUS},
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));
            cursor.close();
        }
        db.close();
        return status;
    }

    @SuppressLint("Range")
    public String getDetailingStatus(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String status = "";

        Cursor cursor = db.query(TABLE_DETAILING,
                new String[]{COLUMN_STATUS},
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));
            cursor.close();
        }
        db.close();
        return status;
    }

    public boolean deleteRepairRequest(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_REPAIRS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean deleteDetailingRequest(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_DETAILING, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public List<String> getAllRepairRequests() {
        List<String> requestsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REPAIRS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_EMAIL,
                        COLUMN_DEVICE_MODEL, COLUMN_DATE, COLUMN_DESCRIPTION, COLUMN_STATUS},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String deviceModel = cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_MODEL));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

                String requestInfo = "ID: " + id + ", Имя: " + name +
                        ", Телефон: " + phone + ", Устройство: " + deviceModel +
                        ", Дата: " + date + ", Статус: " + status;
                requestsList.add(requestInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return requestsList;
    }

    @SuppressLint("Range")
    public List<RequestInfo> getActiveRepairRequestsWithId() {
        List<RequestInfo> requestsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REPAIRS,new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_DATE},
                COLUMN_STATUS + " = ?", new String[]{"в работе"},
                null, null, COLUMN_ID + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));

                RequestInfo requestInfo = new RequestInfo(id, name + " | " + date + " | " + phone);
                requestsList.add(requestInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return requestsList;
    }

    @SuppressLint("Range")
    public List<RequestInfo> getActiveDetailingRequestsWithId() {
        List<RequestInfo> requestsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DETAILING,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_DATE},
                COLUMN_STATUS + " = ?", new String[]{"в работе"},
                null, null, COLUMN_ID + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));

                RequestInfo requestInfo = new RequestInfo(id, name + " | " + date + " | " + phone);
                requestsList.add(requestInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return requestsList;
    }

    @SuppressLint("Range")
    public List<String> getAllDetailingRequests() {
        List<String> requestsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DETAILING,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_EMAIL,
                        COLUMN_CAR_MODEL, COLUMN_DATE, COLUMN_STATUS},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String carModel = cursor.getString(cursor.getColumnIndex(COLUMN_CAR_MODEL));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

                String requestInfo = "ID: " + id + ", Имя: " + name +
                        ", Телефон: " + phone + ", Авто: " + carModel +
                        ", Дата: " + date + ", Статус: " + status;
                requestsList.add(requestInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return requestsList;
    }

    public int getRepairRequestsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REPAIRS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public int getDetailingRequestsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DETAILING, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public int getRequestsInProgressCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName +
                " WHERE " + COLUMN_STATUS + " = 'в работе'", null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public int getCompletedRequestsCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName +
                " WHERE " + COLUMN_STATUS + " = 'выполнено'", null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }


    @SuppressLint("Range")
    public List<RequestInfo> getAllRepairRequestsWithId() {
        List<RequestInfo> requestsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REPAIRS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_DATE, COLUMN_STATUS},
                null, null, null, null, COLUMN_ID + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

                RequestInfo requestInfo = new RequestInfo(id, name + " | " + date + " | " + phone + " | " + status);
                requestsList.add(requestInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return requestsList;
    }

    @SuppressLint("Range")
    public List<RequestInfo> getAllDetailingRequestsWithId() {
        List<RequestInfo> requestsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DETAILING,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE, COLUMN_DATE, COLUMN_STATUS},
                null, null, null, null, COLUMN_ID + " DESC", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

                RequestInfo requestInfo = new RequestInfo(id, name + " | " + date + " | " + phone + " | " + status);
                requestsList.add(requestInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return requestsList;
    }
}