package com.ubb.mylicenseapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Licenta";
    private final static String TAG = "DatabaseHelper";

    public final static String table_name_competition = "competitions";
    public final static String competitionId     = "competitionId";
    public final static String competitionTitle ="competitionTitle";
    public final static String competitionReward ="competitionReward";
    public final static String isRegistered ="isRegistered";

    public final static String table_name_steps = "steps";
    public final static String id     = "ID";
    public final static String number = "number";
    public final static String fetchStepsQuery ="SELECT * FROM "+ table_name_steps;
    public final static String fetchCompetitionsQuery = "SELECT * FROM "+table_name_competition;
    private final static String createStepTable = "CREATE TABLE IF NOT EXISTS " + table_name_steps + " (ID INTEGER , "+
                                               number + " INTEGER DEFAULT 0);";
    private final static String createCompetitionTable ="CREATE TABLE IF NOT EXISTS " + table_name_competition + " (competitionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "                                            " + competitionTitle + " TEXT, " + competitionReward + " TEXT, " + isRegistered + " TEXT);";
    private final static String onUpdatesteps = "DROP TABLE IF EXISTS " + table_name_steps;
    private final static String onUpdatecompetitions = "DROP TABLE IF EXISTS " + table_name_competition;

    public SQLiteDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createStepTable);
        db.execSQL(createCompetitionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(onUpdatesteps);
        db.execSQL(onUpdatecompetitions);
        onCreate(db);
    }
}
