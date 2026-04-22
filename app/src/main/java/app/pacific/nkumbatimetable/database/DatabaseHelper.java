package app.pacific.nkumbatimetable.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import app.pacific.nkumbatimetable.models.Course;
import app.pacific.nkumbatimetable.models.Lecturer;
import app.pacific.nkumbatimetable.models.Schedule;
import app.pacific.nkumbatimetable.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "nkumba_timetable.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_USER_EMAIL = "email";

    // Course Table
    private static final String TABLE_COURSES = "courses";
    private static final String COL_COURSE_ID = "id";
    private static final String COL_COURSE_CODE = "course_code";
    private static final String COL_COURSE_NAME = "course_name";
    private static final String COL_CREDIT_HOURS = "credit_hours";
    private static final String COL_DEPARTMENT = "department";
    private static final String COL_DESCRIPTION = "description";

    // Lecturer Table
    private static final String TABLE_LECTURERS = "lecturers";
    private static final String COL_LECTURER_ID = "id";
    private static final String COL_LECTURER_NAME = "name";
    private static final String COL_LECTURER_EMAIL = "email";
    private static final String COL_PHONE = "phone";
    private static final String COL_LECTURER_DEPT = "department";
    private static final String COL_SPECIALIZATION = "specialization";

    // Schedule Table
    private static final String TABLE_SCHEDULES = "schedules";
    private static final String COL_SCHEDULE_ID = "id";
    private static final String COL_SCHEDULE_COURSE_ID = "course_id";
    private static final String COL_SCHEDULE_LECTURER_ID = "lecturer_id";
    private static final String COL_DAY_OF_WEEK = "day_of_week";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_END_TIME = "end_time";
    private static final String COL_ROOM = "room";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COL_PASSWORD + " TEXT NOT NULL, "
                + COL_FULL_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT)";

        String createCoursesTable = "CREATE TABLE " + TABLE_COURSES + " ("
                + COL_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_COURSE_CODE + " TEXT UNIQUE NOT NULL, "
                + COL_COURSE_NAME + " TEXT NOT NULL, "
                + COL_CREDIT_HOURS + " INTEGER DEFAULT 3, "
                + COL_DEPARTMENT + " TEXT, "
                + COL_DESCRIPTION + " TEXT)";

        String createLecturersTable = "CREATE TABLE " + TABLE_LECTURERS + " ("
                + COL_LECTURER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LECTURER_NAME + " TEXT NOT NULL, "
                + COL_LECTURER_EMAIL + " TEXT, "
                + COL_PHONE + " TEXT, "
                + COL_LECTURER_DEPT + " TEXT, "
                + COL_SPECIALIZATION + " TEXT)";

        String createSchedulesTable = "CREATE TABLE " + TABLE_SCHEDULES + " ("
                + COL_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SCHEDULE_COURSE_ID + " INTEGER NOT NULL, "
                + COL_SCHEDULE_LECTURER_ID + " INTEGER NOT NULL, "
                + COL_DAY_OF_WEEK + " TEXT NOT NULL, "
                + COL_START_TIME + " TEXT NOT NULL, "
                + COL_END_TIME + " TEXT NOT NULL, "
                + COL_ROOM + " TEXT, "
                + "FOREIGN KEY (" + COL_SCHEDULE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COL_COURSE_ID + "), "
                + "FOREIGN KEY (" + COL_SCHEDULE_LECTURER_ID + ") REFERENCES " + TABLE_LECTURERS + "(" + COL_LECTURER_ID + "))";

        db.execSQL(createUsersTable);
        db.execSQL(createCoursesTable);
        db.execSQL(createLecturersTable);
        db.execSQL(createSchedulesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==================== USER OPERATIONS ====================

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, user.getUsername());
        values.put(COL_PASSWORD, user.getPassword());
        values.put(COL_FULL_NAME, user.getFullName());
        values.put(COL_USER_EMAIL, user.getEmail());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public String getUserFullName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_FULL_NAME};
        String selection = COL_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        String fullName = "";
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME));
        }
        cursor.close();
        db.close();
        return fullName;
    }

    // ==================== COURSE OPERATIONS ====================

    public long insertCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COURSE_CODE, course.getCourseCode());
        values.put(COL_COURSE_NAME, course.getCourseName());
        values.put(COL_CREDIT_HOURS, course.getCreditHours());
        values.put(COL_DEPARTMENT, course.getDepartment());
        values.put(COL_DESCRIPTION, course.getDescription());
        long result = db.insert(TABLE_COURSES, null, values);
        db.close();
        return result;
    }

    public int updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COURSE_CODE, course.getCourseCode());
        values.put(COL_COURSE_NAME, course.getCourseName());
        values.put(COL_CREDIT_HOURS, course.getCreditHours());
        values.put(COL_DEPARTMENT, course.getDepartment());
        values.put(COL_DESCRIPTION, course.getDescription());
        int result = db.update(TABLE_COURSES, values, COL_COURSE_ID + " = ?",
                new String[]{String.valueOf(course.getId())});
        db.close();
        return result;
    }

    public void deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, COL_SCHEDULE_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)});
        db.delete(TABLE_COURSES, COL_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)});
        db.close();
    }

    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURSES + " ORDER BY " + COL_COURSE_CODE, null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_COURSE_ID)));
                course.setCourseCode(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_CODE)));
                course.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_NAME)));
                course.setCreditHours(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CREDIT_HOURS)));
                course.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(COL_DEPARTMENT)));
                course.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
                courseList.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return courseList;
    }

    public Course getCourseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSES, null, COL_COURSE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Course course = null;
        if (cursor.moveToFirst()) {
            course = new Course();
            course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_COURSE_ID)));
            course.setCourseCode(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_CODE)));
            course.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_NAME)));
            course.setCreditHours(cursor.getInt(cursor.getColumnIndexOrThrow(COL_CREDIT_HOURS)));
            course.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(COL_DEPARTMENT)));
            course.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
        }
        cursor.close();
        db.close();
        return course;
    }

    public int getCoursesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_COURSES, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // ==================== LECTURER OPERATIONS ====================

    public long insertLecturer(Lecturer lecturer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LECTURER_NAME, lecturer.getName());
        values.put(COL_LECTURER_EMAIL, lecturer.getEmail());
        values.put(COL_PHONE, lecturer.getPhone());
        values.put(COL_LECTURER_DEPT, lecturer.getDepartment());
        values.put(COL_SPECIALIZATION, lecturer.getSpecialization());
        long result = db.insert(TABLE_LECTURERS, null, values);
        db.close();
        return result;
    }

    public int updateLecturer(Lecturer lecturer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LECTURER_NAME, lecturer.getName());
        values.put(COL_LECTURER_EMAIL, lecturer.getEmail());
        values.put(COL_PHONE, lecturer.getPhone());
        values.put(COL_LECTURER_DEPT, lecturer.getDepartment());
        values.put(COL_SPECIALIZATION, lecturer.getSpecialization());
        int result = db.update(TABLE_LECTURERS, values, COL_LECTURER_ID + " = ?",
                new String[]{String.valueOf(lecturer.getId())});
        db.close();
        return result;
    }

    public void deleteLecturer(int lecturerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, COL_SCHEDULE_LECTURER_ID + " = ?",
                new String[]{String.valueOf(lecturerId)});
        db.delete(TABLE_LECTURERS, COL_LECTURER_ID + " = ?",
                new String[]{String.valueOf(lecturerId)});
        db.close();
    }

    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LECTURERS + " ORDER BY " + COL_LECTURER_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Lecturer lecturer = new Lecturer();
                lecturer.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LECTURER_ID)));
                lecturer.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LECTURER_NAME)));
                lecturer.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_LECTURER_EMAIL)));
                lecturer.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)));
                lecturer.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(COL_LECTURER_DEPT)));
                lecturer.setSpecialization(cursor.getString(cursor.getColumnIndexOrThrow(COL_SPECIALIZATION)));
                lecturerList.add(lecturer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lecturerList;
    }

    public int getLecturersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LECTURERS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // ==================== SCHEDULE OPERATIONS ====================

    public long insertSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SCHEDULE_COURSE_ID, schedule.getCourseId());
        values.put(COL_SCHEDULE_LECTURER_ID, schedule.getLecturerId());
        values.put(COL_DAY_OF_WEEK, schedule.getDayOfWeek());
        values.put(COL_START_TIME, schedule.getStartTime());
        values.put(COL_END_TIME, schedule.getEndTime());
        values.put(COL_ROOM, schedule.getRoom());
        long result = db.insert(TABLE_SCHEDULES, null, values);
        db.close();
        return result;
    }

    public int updateSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SCHEDULE_COURSE_ID, schedule.getCourseId());
        values.put(COL_SCHEDULE_LECTURER_ID, schedule.getLecturerId());
        values.put(COL_DAY_OF_WEEK, schedule.getDayOfWeek());
        values.put(COL_START_TIME, schedule.getStartTime());
        values.put(COL_END_TIME, schedule.getEndTime());
        values.put(COL_ROOM, schedule.getRoom());
        int result = db.update(TABLE_SCHEDULES, values, COL_SCHEDULE_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
        db.close();
        return result;
    }

    public void deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, COL_SCHEDULE_ID + " = ?",
                new String[]{String.valueOf(scheduleId)});
        db.close();
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> scheduleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.*, c." + COL_COURSE_NAME + ", c." + COL_COURSE_CODE
                + ", l." + COL_LECTURER_NAME
                + " FROM " + TABLE_SCHEDULES + " s"
                + " INNER JOIN " + TABLE_COURSES + " c ON s." + COL_SCHEDULE_COURSE_ID + " = c." + COL_COURSE_ID
                + " INNER JOIN " + TABLE_LECTURERS + " l ON s." + COL_SCHEDULE_LECTURER_ID + " = l." + COL_LECTURER_ID
                + " ORDER BY CASE s." + COL_DAY_OF_WEEK
                + " WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2 WHEN 'Wednesday' THEN 3"
                + " WHEN 'Thursday' THEN 4 WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6 END,"
                + " s." + COL_START_TIME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCHEDULE_ID)));
                schedule.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCHEDULE_COURSE_ID)));
                schedule.setLecturerId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCHEDULE_LECTURER_ID)));
                schedule.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(COL_DAY_OF_WEEK)));
                schedule.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_START_TIME)));
                schedule.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_END_TIME)));
                schedule.setRoom(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM)));
                schedule.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_NAME)));
                schedule.setCourseCode(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_CODE)));
                schedule.setLecturerName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LECTURER_NAME)));
                scheduleList.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scheduleList;
    }

    public List<Schedule> getSchedulesByDay(String day) {
        List<Schedule> scheduleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.*, c." + COL_COURSE_NAME + ", c." + COL_COURSE_CODE
                + ", l." + COL_LECTURER_NAME
                + " FROM " + TABLE_SCHEDULES + " s"
                + " INNER JOIN " + TABLE_COURSES + " c ON s." + COL_SCHEDULE_COURSE_ID + " = c." + COL_COURSE_ID
                + " INNER JOIN " + TABLE_LECTURERS + " l ON s." + COL_SCHEDULE_LECTURER_ID + " = l." + COL_LECTURER_ID
                + " WHERE s." + COL_DAY_OF_WEEK + " = ?"
                + " ORDER BY s." + COL_START_TIME;
        Cursor cursor = db.rawQuery(query, new String[]{day});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCHEDULE_ID)));
                schedule.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCHEDULE_COURSE_ID)));
                schedule.setLecturerId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCHEDULE_LECTURER_ID)));
                schedule.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(COL_DAY_OF_WEEK)));
                schedule.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_START_TIME)));
                schedule.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_END_TIME)));
                schedule.setRoom(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROOM)));
                schedule.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_NAME)));
                schedule.setCourseCode(cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE_CODE)));
                schedule.setLecturerName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LECTURER_NAME)));
                scheduleList.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scheduleList;
    }

    public int getSchedulesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SCHEDULES, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}
