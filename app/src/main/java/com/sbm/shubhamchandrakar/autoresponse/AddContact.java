package com.sbm.shubhamchandrakar.autoresponse;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import database.BaseData;

public class AddContact extends Activity {
    ListView lv;
    EditText search_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final Button search_btn = (Button) findViewById(R.id.search_btn);
        search_et = (EditText) findViewById(R.id.search_edittext);
        lv = (ListView) findViewById(R.id.contact_listview);
        populateList();
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                populateList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String no1 = search_et.getText().toString();
                if (no1 != null) {
                    no1 = no1.replaceAll("-", "").trim();
                    no1 = no1.replace("+", "").trim();
                    no1 = no1.replaceAll(" ", "").trim();
                    String no2;
                    long i;
                    String str = null;
                    try {
                        no2 = no1.substring(no1.length() - 10);
                        i = Long.parseLong(no2);
                        str = Long.toString(i);
                        if (str.length() == 10) {
                            BaseData.insert_db(search_et.getText().toString(), str, AddContact.this);
                            Intent i4 = new Intent(AddContact.this, SelectedContact.class);
                            startActivity(i4);
                            AddContact.this.finish();
                        }
                        Log.e("ASD", "i : " + i);
                    } catch (Exception e) {
                        Toast.makeText(AddContact.this, "Please Salect valid PPHONE-NUMBER", Toast.LENGTH_LONG).show();
                        search_et.setText("");
                        Log.e("ASD", "i : " + e);
                    }
                } else {
                    Toast.makeText(AddContact.this, "Please Salect valid PPHONE-NUMBER", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    private void populateList() {
        LinkedList<String> res = new LinkedList<>();
        String whr = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like ? ";
        String sa[] = new String[1];
        sa[0] = "%" + search_et.getText() + "%";
        Cursor c1 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, whr, sa, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int in1 = c1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int in2 = c1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        while (c1.moveToNext()) {
            String name = c1.getString(in1);
            String no = c1.getString(in2);
            res.add(name + " : " + no);

        }
        ArrayAdapter<String> aa = new ArrayAdapter<String>(AddContact.this, android.R.layout.simple_list_item_1, res);
        lv.setAdapter(aa);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv1 = (TextView) view;
                String no1 = tv1.getText().toString();
                search_et.setText(no1);
            }
        });
    }


}
