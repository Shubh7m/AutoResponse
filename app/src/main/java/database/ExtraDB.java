package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sbm.shubhamchandrakar.autoresponse.Update;

/**
 * Created by Shubham Chnadrakar on 23/07/2017.
 */

public class ExtraDB {
    static SQLiteDatabase prodb_ob;
    static Context co;

    public static void open_or_create_repeat_db(Context c) {
        prodb_ob = c.openOrCreateDatabase("MyDB2", Context.MODE_PRIVATE, null);
        prodb_ob.execSQL("CREATE TABLE IF NOT EXISTS repeat_table (ph_no VARCHAR(15)," +
                " time VARCHAR(25), count INTEGER)");
        prodb_ob.close();
    }

    public static void insert_repeat_db(String ph_no, int count, Context c) {
        prodb_ob = c.openOrCreateDatabase("MyDB2", Context.MODE_PRIVATE, null);
        String sql = "insert into repeat_table (ph_no, time, count) values(?, datetime('now'), ?)";
        Object[] ob = new Object[2];
        ob[0] = ph_no;
        ob[1] = count;
        prodb_ob.execSQL(sql, ob);
        prodb_ob.close();

    }
    public  static  int cmp(String num, Context c){
        int res = 0;
        prodb_ob = c.openOrCreateDatabase("MyDB2", Context.MODE_PRIVATE, null);
        String sql = "SELECT (strftime('%s','now') - strftime('%s',`time`)) /60 diff ,strftime('%s',`time`) b,strftime('%s','now') a from repeat_table where ph_no = '"+ num+"'";
        Cursor cur = prodb_ob.rawQuery(sql, null);

        while (cur.moveToNext()){
            res = cur.getInt(cur.getColumnIndex("diff"));
            Log.d("diff", "cmp: "+ res);
            Log.d("diff", "a: "+ cur.getString(cur.getColumnIndex("a")));
            Log.d("diff", "b: "+ cur.getString(cur.getColumnIndex("b")));
        }
        return res;
    }

    public static void update_repeat_db(String ph_no, int count, Context c) {
        prodb_ob = c.openOrCreateDatabase("MyDB2", Context.MODE_PRIVATE, null);

        if (count==1 || count == 2){
            String sql = "update repeat_table set `time` = datetime('now'), count = ? where ph_no = '"+ ph_no+"'";
            Object[] ob = new Object[1];
            ob[0] = count;
            prodb_ob.execSQL(sql, ob);
            prodb_ob.close();

        }else{
            ContentValues cv = new ContentValues();
            cv.put("count", count);
            String[] whereArgs = {ph_no};
            prodb_ob.update("repeat_table", cv, "ph_no" + "=?", whereArgs);
            prodb_ob.close();
        }



    }

    public static Update show_repeat_db(Context cone, String ph_no) {
        Update u1 = new Update();
        prodb_ob = cone.openOrCreateDatabase("MyDB2", Context.MODE_PRIVATE, null);
        Cursor c = prodb_ob.rawQuery("SELECT *  FROM repeat_table where ph_no = '" + ph_no+"'", null);
        while (c.moveToNext()) {
            u1.count = c.getInt(c.getColumnIndex("count"));
            u1.time = c.getString(c.getColumnIndex("time"));
            u1.phone_no = c.getString(c.getColumnIndex("ph_no"));
        }
        prodb_ob.close();
        return u1;

    }
}
