package bignerdranch2nded.com.criminalintent2nded.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juan on 9/14/2015.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "CrimeBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + CrimeDbSchema.CrimeTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        CrimeDbSchema.CrimeTable.Cols.UUID + ", " +
                        CrimeDbSchema.CrimeTable.Cols.TITLE + ", " +
                        CrimeDbSchema.CrimeTable.Cols.DATE + ", " +
                        CrimeDbSchema.CrimeTable.Cols.SOLVED + ", " +
                        CrimeDbSchema.CrimeTable.Cols.SUSPECT +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //For now we just have one VERSION so we can ignore this for now
    }
}
