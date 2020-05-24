package wada.programmics.thewada.SqLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

import wada.programmics.thewada.ObjectClass.NotificationData;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_title = "usernotification.db";
    public static final String notification_TABLE_title = "notificationtbl";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String DATE = "date";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_title , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table notificationtbl " +
                        "(id integer primary key, title text,message text,date text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS notificationtbl");
        onCreate(db);
    }

    public boolean insertNotification (String title, String message, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("message", message);
        contentValues.put("date", date);

        db.insert("notificationtbl", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notification where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, notification_TABLE_title);
        return numRows;
    }

    public boolean updateNotification (Integer id, String title, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("message", message);

        db.update("notificationtbl", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteNotification (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("notification",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<NotificationData> getAllNotification() {
        ArrayList<NotificationData> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from notificationtbl", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new NotificationData(res.getInt(res.getColumnIndex(ID)),res.getString(res.getColumnIndex(TITLE)),res.getString(res.getColumnIndex(MESSAGE)), res.getString(res.getColumnIndex(DATE))));
            res.moveToNext();
        }
        return array_list;
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ notification_TABLE_title);
    }

}
