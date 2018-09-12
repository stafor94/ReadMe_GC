package com.example.iternity.gachon_class;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//북마크 DBHelper 클래스
public class DBHelper_Bookmark extends SQLiteOpenHelper {

    Context context;
    SQLiteDatabase db;
    Cursor cursor;

    // Database name
    private static final String DATABASE_NAME = "Gachon.db";
    // Database version
    private static final int DATABASE_VERSION = 1;
    // Table name
    private static final String TABLE_NAME = "bookmark";

    //DBHelper 생성자(Context, DBname, cursor, DBversion)
    public DBHelper_Bookmark(Context context) {
        // 데이터베이스 이름과 버전 정보를 이용하여 상위 생성자 호출
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /* 데이터 베이스 create
     *  - 생성자에서 넘겨 받은 이름의 DB와 버전의 DB가 존재하지 않을 때 호출
     *  - 새로운 데이터 베이스를 생성할때 사용*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* 테이블을 생성하기 위해 sql문으로 작성하여 execSQL 문 실행 */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, lectureRoom TEXT);");
    }

    /* 데이터베이스 Version Upgrade
     *  - DB 가 존재하지만 버전이 다르면 호출됨
     *  - DB를 변경하고, 버전읇 변경할때 여러가지 업그레이드 작업 수행 가능*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* 테이블을 업그레이드 하기 위해 SQL문을 작성하여 execSQL문 실행
         *  - 기존의 테이블을 삭제한 후 테이블 재생성*/
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 테이블의 레코드 insert
    public void insert(String lectureRoom) {
        db = getWritableDatabase();

        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(null, '" + lectureRoom + "');");
        db.close();
    }

    // 테이블의 레코드 delete
    public void delete(String lectureRoom) {
        db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE lectureRoom = '" + lectureRoom + "';");
        db.close();
    }

    // DB에서 데이터를 가져옴
    public String getResult() {
        db = getReadableDatabase();
        String result = "";

        cursor = db.rawQuery("SELECT lectureRoom FROM " + TABLE_NAME + ";", null);
        cursor.moveToFirst();

        while(cursor.moveToNext()) {
            result += cursor.getString(0);
            if (!cursor.isLast()) {
                result += ",";
            }
        }
        cursor.close();

        return result;
    }

    // 중복을 체크한다
    public boolean isExist(String lectureRoom) {
        db = getReadableDatabase();

        cursor = db.rawQuery("SELECT lectureRoom FROM " + TABLE_NAME + ";", null);
        cursor.moveToFirst();

        while(cursor.moveToNext()) {
            if (lectureRoom.equals(cursor.getString(0))) {
                return true;
            }
        }
        cursor.close();

        return false;
    }
}
