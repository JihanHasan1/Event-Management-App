package com.example.cse489_2020_1_60_034;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class activity_create_event extends AppCompatActivity {

    private RadioButton outdoor, indoor, online;
    private EditText tfName, tfPlace, tfDateTime, tfCapacity, tfBudget, tfEmail, tfPhone, tfDescription;
    private TextView errorTV;

    private Button btnCancel, btnView, btnSave;
    //private Button btnView;

    private String existingKey = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        outdoor = findViewById(R.id.rdOutdoor);
        indoor = findViewById(R.id.rdIndoor);
        online = findViewById(R.id.rdOnline);
        errorTV = findViewById(R.id.tvError);

        tfName = findViewById(R.id.tfName);
        tfPlace = findViewById(R.id.tfPlace);
        tfDateTime = findViewById(R.id.tfDateTime);
        tfCapacity = findViewById(R.id.tfCapacity);
        tfBudget = findViewById(R.id.tfBudget);
        tfEmail = findViewById(R.id.tfEmail);
        tfPhone = findViewById(R.id.tfPhone);
        tfDescription = findViewById(R.id.tfDescription);

        Intent i = getIntent();
        if(i.hasExtra("EVENT_KEY")){
            existingKey = i.getStringExtra("EVENT_KEY");
            KeyValueDB db = new KeyValueDB(activity_create_event.this);
            String value = db.getValueByKey(existingKey);

            String values[] = value.split("---");
            tfName.setText(values[0]);
            tfPlace.setText(values[1]);
            if(values[2].equals("Outdoor")){
                outdoor.setChecked(true);
            }else if(values[2].equals("Indoor")){
                indoor.setChecked(true);
            }else if(values[2].equals("Online")){
                online.setChecked(true);
            }
            tfDateTime.setText(values[3]);
            tfCapacity.setText(values[4]);
            tfBudget.setText(values[5]);
            tfEmail.setText(values[6]);
            tfPhone.setText(values[7]);
            tfDescription.setText(values[8]);

            db.close();
        }

        btnCancel = findViewById(R.id.btnCancel);
        btnView = findViewById(R.id.btnView);
        btnSave = findViewById(R.id.btnSave);

        SharedPreferences sp = getSharedPreferences("user_account", MODE_PRIVATE);
        String storedUserId = sp.getString("userId", "");
        String storedEmail = sp.getString("email", "");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorMsg = "";

                boolean out = outdoor.isChecked();
                boolean in = indoor.isChecked();
                boolean on = online.isChecked();

                String type = "";
                if(out){
                    type = "Outdoor";
                }else if(in){
                    type = "Indoor";
                }else if(on){
                    type = "Online";
                }

                String name = tfName.getText().toString();
                String place = tfPlace.getText().toString();
                String dateTime = tfDateTime.getText().toString();
                String capacity = tfCapacity.getText().toString();
                String budget = tfBudget.getText().toString();
                String email = tfEmail.getText().toString();
                String phone = tfPhone.getText().toString();
                String description = tfDescription.getText().toString();

                if(name.equals("")){
                    errorMsg += "Event Name Missing !\n";
                }
                if(place.equals("")){
                    errorMsg += "Event Place Missing !\n";
                }
                if(out==false && in==false && on==false){
                    errorMsg += "Event Type was not selected !\n";
                }
                if(dateTime.equals("")){
                    errorMsg += "Event Date & Time Missing !\n";
                }
                if(capacity.equals("")){
                    errorMsg += "Event Capacity Missing !\n";
                }
                if(budget.equals("")){
                    errorMsg += "Event Budget Missing !\n";
                }
                if(email.equals("")){
                    errorMsg += "Event Email Missing !\n";
                }
                if(phone.equals("")){
                    errorMsg += "Event Phone No. Missing !\n";
                }
                if(description.equals("")){
                    errorMsg += "Event Description Missing !\n";
                }
                if(errorMsg.length()>0){
                    errorTV.setText(errorMsg);
                } else {
                    String value = name+"---"+place+"---"+type+"---"+dateTime+"---"+capacity+"---"+budget+"---"+email+"---"+phone+"---"+description+"---";
                    KeyValueDB db = new KeyValueDB(activity_create_event.this);
                    // Generate a unique id
                    if(existingKey.length()==0){
                        String key = name + System.currentTimeMillis();
                        existingKey = key;

                        db.insertKeyValue(key, value); //Using SQLite

                    }else{
                        db.updateValueByKey(existingKey, value);
                    }
                    db.close();

                    // Implementation of Remote Database
                    String[] keys = {"action", "id", "semester", "key", "event"};
                    String[] values = {"backup", "2020160034", "20231", existingKey, value};
                    httpRequest(keys, values);

                    Toast.makeText(activity_create_event.this, "Save Successful", Toast.LENGTH_SHORT).show();


                    Intent i = new Intent(activity_create_event.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                finish();
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity_create_event.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void httpRequest(final String keys[], final String values[]){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://muthosoft.com/univ/cse489/index.php";
                String data="";
                try {
                    data = JSONParser.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    System.out.println(data);
                    //updateEventListByServerData(data);
                    Toast.makeText(getApplicationContext(),"Event Information has been Saved",Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void onStart(){
        super.onStart();
    }
}