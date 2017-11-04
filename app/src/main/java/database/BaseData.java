package database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.sbm.shubhamchandrakar.autoresponse.Update;

import java.util.LinkedList;

public class BaseData {
    static SQLiteDatabase prodb_ob;
    static Context co;

    public static void open_or_create_profile_db(Context c) {
        prodb_ob = c.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
        prodb_ob.execSQL("CREATE TABLE IF NOT EXISTS profile_table (p_key INTEGER PRIMARY KEY , profile_name VARCHAR(12)," +
                " profile_switch BOOLEAN, p_mode INTEGER, sms_text VARCHAR(140))");
        prodb_ob.close();
    }

    public static int active_profile(Context cone) {
        int res = 7;
        prodb_ob = cone.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);

        Cursor c = prodb_ob.rawQuery("SELECT * FROM profile_table ", null);

        while (c.moveToNext()) {

            String s1 = c.getString(c.getColumnIndex("profile_switch"));
            Log.d("lg", "show_profile_db: " + s1);
            if (s1.equalsIgnoreCase("1")) {
                Log.d("KK", "this is active " + c.getInt(c.getColumnIndex("p_key")) + " : " + c.getString(c.getColumnIndex("profile_switch")));
                res = c.getInt(c.getColumnIndex("p_key"));
            }
        }
        prodb_ob.close();
        return res;
    }

    public static Update show_profile_db(Context cone, int pro_real_id) {
        Update u1 = new Update();
        prodb_ob = cone.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);

        Cursor c = prodb_ob.rawQuery("SELECT * FROM profile_table where p_key = " + pro_real_id, null);

        while (c.moveToNext()) {
            u1.sms_txt = c.getString(c.getColumnIndex("sms_text"));
            String s1 = c.getString(c.getColumnIndex("profile_switch"));
            Log.d("lg", "show_profile_db: pmode:::::,,,,,,,,,,,,,, " + c.getInt(c.getColumnIndex("p_mode")));
            u1.pr_sw = !s1.equalsIgnoreCase("0");
            u1.profile_mode = c.getInt(c.getColumnIndex("p_mode"));
            u1.pr_name = c.getString(c.getColumnIndex("profile_name"));
        }

        prodb_ob.close();
        return u1;

    }

    public static void insert_profile_db(String pr_name, String sms_txt, int p_mode, boolean pr_sw, int id, Context c) {

        prodb_ob = c.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
        String sql = "insert into Profile_table (profile_name, profile_switch, p_mode, sms_text, p_key) values(?, ?, ?, ?, ?)";
        Object[] ob = new Object[5];
        ob[0] = pr_name;
        ob[1] = pr_sw;
        ob[2] = p_mode;
        ob[3] = sms_txt;
        ob[4] = id;

        prodb_ob.execSQL(sql, ob);
        prodb_ob.close();

    }

    public static boolean update_profile_db(Boolean switch_value_recive, String id, Context c) {
        boolean res = switch_value_recive;
        prodb_ob = c.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
//		boolean b1 = true;
        ContentValues cv = new ContentValues();
        cv.put("profile_switch", switch_value_recive);
        String[] whereArgs = {id};

//		String p_key= "p_key";
        prodb_ob.update("profile_table", cv, "p_key" + "=?", whereArgs);

        Log.d("ASD", "st4");
        prodb_ob.close();
        return res;
    }

    public static void update_sms(String sms, String id, Context c) {

        prodb_ob = c.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues();
        cv.put("sms_text", sms);
        String[] whereArgs = {id};
        prodb_ob.update("profile_table", cv, "p_key" + "=?", whereArgs);
        prodb_ob.close();
    }

    public static void update_profile_mode(int p_mode, String id, Context c) {

        prodb_ob = c.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues();
        cv.put("p_mode", p_mode);
        String[] whereArgs = {id};
        prodb_ob.update("profile_table", cv, "p_key" + "=?", whereArgs);
        prodb_ob.close();
    }

    public static void update_active_profile(Boolean switch_value_recive, String id, Context c) {
        boolean res = switch_value_recive;
        prodb_ob = c.openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues();
        cv.put("profile_switch", switch_value_recive);
        String[] whereArgs = {id};
        prodb_ob.update("profile_table", cv, "p_key" + "=?", whereArgs);
        prodb_ob.close();

    }

    public static void open_or_create_contactList_db(Context c) {
        prodb_ob = c.openOrCreateDatabase("MyDB1", Context.MODE_PRIVATE, null);
        prodb_ob.execSQL("CREATE TABLE IF NOT EXISTS list_table (p_key INTEGER PRIMARY KEY ,  u_name VARCHAR(50), phone_no VARCHAR(15))");
        prodb_ob.close();

    }

    public static void insert_db(String name, String phone_no, Context c) {

        prodb_ob = c.openOrCreateDatabase("MyDB1", Context.MODE_PRIVATE, null);
        String sql = "insert into list_table ( phone_no, u_name) values(?, ?)";
        Object[] ob = new Object[2];
        ob[0] = phone_no;
        ob[1] = name;
        prodb_ob.execSQL(sql, ob);
        prodb_ob.close();
        Toast.makeText(c, "(" + name + ") is added to your salected list", Toast.LENGTH_LONG).show();

    }

    public static LinkedList<String> fatch_db(Context cone) {
        LinkedList<String> res = new LinkedList<>();
        prodb_ob = cone.openOrCreateDatabase("MyDB1", Context.MODE_PRIVATE, null);
        Cursor c = prodb_ob.rawQuery("SELECT * FROM list_table", null);
        while (c.moveToNext()) {
            String s = c.getString(c.getColumnIndex("u_name"));
            res.add(s);
        }
        prodb_ob.close();
        return res;

    }

    public static boolean check_salected_list(Context cone, String num) {
        boolean res = false;
        String no2 = num.substring(num.length() - 10);
        prodb_ob = cone.openOrCreateDatabase("MyDB1", Context.MODE_PRIVATE, null);
        Cursor c = prodb_ob.rawQuery("SELECT * FROM list_table", null);
        while (c.moveToNext()) {
            String s = c.getString(c.getColumnIndex("phone_no"));
            if (s.equalsIgnoreCase(no2)) {
                res = true;
            }

        }
        prodb_ob.close();
        return res;

    }

    public static void delete_no_db(String pas_data, Context c) {
        prodb_ob = c.openOrCreateDatabase("MyDB1", Context.MODE_PRIVATE, null);
        String[] whereArgs = {pas_data.toString()};
        prodb_ob.delete("list_table", "phone_no " + "=?", whereArgs);
        prodb_ob.close();
    }
}
