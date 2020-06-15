package com.example.lab1.Module;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBAccess {
    public static final String DB_NAME = "CoreDB.db";
    public static String DB_PATH;
    private SQLiteDatabase db;

    public DBAccess(String dbPath, boolean allowCreate) throws SQLException {
        db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE | (allowCreate? SQLiteDatabase.CREATE_IF_NECESSARY: 0));
    }
    public DBAccess() throws SQLException {
        this(DB_PATH, false);
    }
    // NOTICE: must call deployDB() before use DB_PATH and DBAccess
    public static void deployDB(Context context) throws IOException {
        DB_PATH = context.getFilesDir().getCanonicalPath() + File.separator + DB_NAME;
        File f = new File(DB_PATH);
        if(!f.exists()) {
            // deploy db file
            InputStream is = context.getAssets().open("CoreDB.db");
            FileOutputStream fos = new FileOutputStream(DB_PATH);

            byte[] buffer = new byte[1024];
            int count = 0;
            while((count = is.read(buffer)) > 0)
                fos.write(buffer, 0, count);
            fos.flush();
            fos.close();
            is.close();
        }
    }

    public SQLiteDatabase getInstance() {
        return db;
    }
}