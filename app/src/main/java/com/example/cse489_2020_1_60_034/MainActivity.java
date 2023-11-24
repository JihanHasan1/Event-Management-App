package com.example.cse489_2020_1_60_034;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lvEvents;
    private ArrayList<Event> events;
    private CustomEventAdapter adapter;

    private Button btnCreate, btnHistory, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        events = new ArrayList<>();
        lvEvents = findViewById(R.id.listEvents);
        btnCreate = findViewById(R.id.btnCreate);
        btnHistory = findViewById(R.id.btnHistory);
        btnExit = findViewById(R.id.btnExit);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, activity_create_event.class);
                startActivity(i);
                finish();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // handle the click on an event-list item
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //Position = Real Position
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, activity_create_event.class);
                i.putExtra("EVENT_KEY", events.get(position).key);
                //i.putExtra("NEWDBkey",pos_k);
                startActivity(i);
                finish();
            }
        });
        // handle the long-click on an event-list item
        lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Do you want to delete event - "+events.get(position).name +" ?";
                showDialog(message, "Delete Event", events.get(position).key);
                return true;
            }
        });
        adapter = new CustomEventAdapter(this, events);
        lvEvents.setAdapter(adapter);
    }
    private void showDialog(String message, String title, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                KeyValueDB db = new KeyValueDB(getApplicationContext());
                db.deleteDataByKey(key);

                String[] keys = {"action", "id", "semester", "key"};
                String[] values = {"remove", "2020160034", "20231", key};
                httpRequest(keys, values);

                dialog.cancel();
                loadData();
                adapter.notifyDataSetChanged();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadData(){
        events.clear();
        KeyValueDB db = new KeyValueDB(MainActivity.this);
        Cursor rows = db.execute("SELECT * FROM key_value_pairs");
        if (rows.getCount() == 0) {
            System.out.println("Data is going to be loaded from Remote Server");

            String[] keys = {"action", "id", "semester"};
            String[] values = {"restore", "2020160034", "20231"};
            httpRequest(keys, values);
            //return;
        } else {
            //events = new Event[rows.getCount()];
            while (rows.moveToNext()) {
                String key = rows.getString(0);
                String eventData = rows.getString(1);
                String[] fieldValues = eventData.split("---");

                String name = fieldValues[0];
                String place = fieldValues[1];
                String eventType = fieldValues[2];
                String dateTime = fieldValues[3];
                String capacity = fieldValues[4];
                String budget = fieldValues[5];
                String email = fieldValues[6];
                String phone = fieldValues[7];
                String description = fieldValues[8];

                Event e = new Event(key, name, place, eventType, dateTime, capacity, budget, email, phone, description);
                events.add(e);
            }
            adapter.notifyDataSetChanged();
        }
        db.close();
    }


    public void onRestart(){
        super.onRestart();

        loadData();
        String[] keys = {"action", "id", "semester"};
        String[] values = {"restore", "2020160034", "20231"};
        httpRequest(keys, values);
    }

    public void onStart() {
        super.onStart();
        loadData();
    }
    @SuppressLint("StaticFieldLeak")
    private void httpRequest(final String keys[], final String values[]){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data=JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    updateEventListByServerData(data);
                    //Toast.makeText(getApplicationContext(),"Event Information has been Loaded",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    public void updateEventListByServerData(String data){
        try{
            JSONObject jo = new JSONObject(data);
            if(jo.has("events")){
                events.clear();
                JSONArray ja = jo.getJSONArray("events");
                KeyValueDB db = new KeyValueDB(MainActivity.this);
                for(int i=0; i<ja.length(); i++){
                    JSONObject event = ja.getJSONObject(i);
                    String eventKey = event.getString("e_key");
                    String eventValue = event.getString("e_value");
                    System.out.println(eventValue);

                    db.updateValueByKey(eventKey, eventValue); //Insert data into SQLite from server

                }
                db.close();
                loadData();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //System.exit(0);
    }
}