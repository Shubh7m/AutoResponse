package com.sbm.shubhamchandrakar.autoresponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import database.BaseData;

public class SelectedContact extends Activity {
    ListView cnt_lv;

    public void refresh_listview() {

        cnt_lv = (ListView) findViewById(R.id.contact_list);
        LinkedList<String> link_s1 = BaseData.fatch_db(SelectedContact.this);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(SelectedContact.this, android.R.layout.simple_list_item_1, link_s1);
        cnt_lv.setAdapter(aa);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salected_contact);

        refresh_listview();
        Button b1 = (Button) findViewById(R.id.add_btn);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectedContact.this, AddContact.class);
                startActivity(i);
                SelectedContact.this.finish();
            }
        });
        cnt_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv1 = (TextView) view;
                String no1 = tv1.getText().toString();
                no1 = no1.replaceAll("-", "").trim();
                no1 = no1.replace("+", "").trim();
                no1 = no1.replaceAll(" ", "").trim();
                final String no2 = no1.substring(no1.length() - 10);
                List<String> options = new ArrayList<String>();
                options.add("Delete");
                final CharSequence[] all_option = options.toArray(new String[options.size()]);
                AlertDialog.Builder alert_dialog_profile_mode = new AlertDialog.Builder(SelectedContact.this);
                alert_dialog_profile_mode.setItems(all_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int option_position) {
                        if (option_position == 0) {
                            BaseData.delete_no_db(no2, SelectedContact.this);
                            refresh_listview();
                        }
                    }
                });
                alert_dialog_profile_mode.create();
                alert_dialog_profile_mode.show();
                return false;
            }
        });
    }
}
