package com.ubb.mylicenseapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ubb.mylicenseapplication.model.CompetitionModel;
import com.ubb.mylicenseapplication.model.StepModel;

import java.util.ArrayList;

public class DatabaseManager {

    private static DatabaseManager databaseInstance;
    private Context context;
    private SQLiteDatabase database;
    private SQLiteDatabaseHelper dbHelper;

    public static void init(Context context) {
        if (databaseInstance == null) {
            context = context.getApplicationContext();
            databaseInstance = new DatabaseManager(context);
        }
    }

    private DatabaseManager(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public static DatabaseManager getDatabaseInstance(){
        return databaseInstance;
    }

    public DatabaseManager open() throws SQLException {
        this.dbHelper = new SQLiteDatabaseHelper(this.context);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    public ArrayList<StepModel> fetchSteps() {
        ArrayList<StepModel> arrayList = new ArrayList<>();
        open();
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        Cursor data = db.rawQuery(SQLiteDatabaseHelper.fetchStepsQuery,null);
        if (data.getCount() != 0){
            if (data.moveToFirst()){
                do {
                    StepModel stepModel = new StepModel(data.getInt(data.getColumnIndex(SQLiteDatabaseHelper.id)),
                            data.getInt(data.getColumnIndex(SQLiteDatabaseHelper.number)));
                    arrayList.add(stepModel);
                }while (data.moveToNext());
            }
        }
        data.close();
        close();
        return arrayList;
    }

    public ArrayList<CompetitionModel> fetchCompetitions(){
        ArrayList<CompetitionModel> arrayList = new ArrayList<>();
        open();
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        Cursor data = db.rawQuery(SQLiteDatabaseHelper.fetchCompetitionsQuery,null);
        if (data.getCount() != 0){
            if (data.moveToFirst()){
                do {
                    CompetitionModel competitionModel = new CompetitionModel(data.getInt(data.getColumnIndex(SQLiteDatabaseHelper.competitionId)),
                            data.getString(data.getColumnIndex(SQLiteDatabaseHelper.competitionTitle)),data.getString(data.getColumnIndex(SQLiteDatabaseHelper.competitionReward)),
                            data.getString(data.getColumnIndex(SQLiteDatabaseHelper.isRegistered)));
                    arrayList.add(competitionModel);
                }while (data.moveToNext());
            }
        }
        data.close();
        close();
        return arrayList;
    }

    private void insertSteps(Integer steps) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLiteDatabaseHelper.number, steps);
        this.database.insert(SQLiteDatabaseHelper.table_name_steps, null, contentValue);
        System.out.println("S-a adaugat pasul in db!");
    }

    public void insertCompetition(CompetitionModel competitionModel) {
        open();
        ContentValues contentValue = new ContentValues();
        contentValue.put(SQLiteDatabaseHelper.competitionId,competitionModel.getIdCompetition());
        contentValue.put(SQLiteDatabaseHelper.competitionTitle,competitionModel.getCompetitionTitle());
        contentValue.put(SQLiteDatabaseHelper.competitionReward,competitionModel.getCompetitionReward());
        contentValue.put(SQLiteDatabaseHelper.isRegistered,competitionModel.getIsRegistered());
        this.database.insert(SQLiteDatabaseHelper.table_name_competition, null, contentValue);
        System.out.println("S-a adaugat o competitie in db!");
        close();
    }

    public int updateSteps(int steps){
        ArrayList<StepModel> arrayList = fetchSteps();
        open();
        if (arrayList.size() != 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(SQLiteDatabaseHelper.number, steps);
            return this.database.update(SQLiteDatabaseHelper.table_name_steps, contentValues, "ID = " + arrayList.get(0).getID(), null);
        }
        else{
            insertSteps(steps);
        }
        close();
        return steps;
    }

    public void deleteCompetition() {
        open();
        this.database.delete(SQLiteDatabaseHelper.table_name_competition, null, null);
        close();
    }

    public void deleteSteps() {
        open();
        this.database.delete(SQLiteDatabaseHelper.table_name_steps, null, null);
        close();
    }


}
