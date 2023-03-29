package com.app.androidnewsapp.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.androidnewsapp.R;
import com.app.androidnewsapp.utils.Constant;
import com.app.androidnewsapp.utils.EmailSender;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ActivityContact extends AppCompatActivity {
    private Spinner mCountrySpinner;
    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText field4;
    private EditText field5;
    private EditText field6;
    private EditText field7;
    private EditText field8;
    private EditText field9;
    private EditText field10;
    private SwitchCompat mSubscribeSwitch;
    private RadioButton mMaleRadioButton;
    private RadioButton mFemaleRadioButton;
    private Button mSubmitButton;
    private RadioGroup contactRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mNameEditText = findViewById(R.id.name_field);
        mEmailEditText = findViewById(R.id.email_field);
        mPhoneEditText = findViewById(R.id.message_field);
        mCountrySpinner = findViewById(R.id.country_spinner);
        mSubscribeSwitch = findViewById(R.id.contact_switch);
        mMaleRadioButton = findViewById(R.id.email_radio_button);
        mFemaleRadioButton = findViewById(R.id.mail_radio_button);
        mSubmitButton = findViewById(R.id.submit_button);
        field4 = findViewById(R.id.field4);
        field5 = findViewById(R.id.field5);
        field6 = findViewById(R.id.field6);
        field7 = findViewById(R.id.field7);
        field8 = findViewById(R.id.field8);
        field9 = findViewById(R.id.field9);
        field10 = findViewById(R.id.field10);
        contactRadioGroup = findViewById(R.id.contact_radio_group);

        //Fetch dropdown list data
        new FetchDropdownValuesTask().execute();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateControls()) {
                    submit(mNameEditText.getText().toString(), mEmailEditText.getText().toString(),
                            mPhoneEditText.getText().toString(), mSubscribeSwitch.isChecked() ? "1" : "0", mMaleRadioButton.isChecked() ? "Email" : "Mail",
                            mCountrySpinner.getSelectedItem().toString());
                }
            }
        });
    }

    public void submit(String name, String email, String message, String phone, String prefmethod, String country) {
        String url = "http://1326:8888/news/contacts.php";
//Send Email
        EmailSender.sendEmail(email, "Contact Support", message);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"Request Submitted", Toast.LENGTH_LONG).show();
                        // Handle server response here
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error here
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("message", message);
                params.put("phone", phone);
                params.put("prefmethod", prefmethod);
                params.put("country", country);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ActivityContact.this);
        queue.add(stringRequest);
    }

    //fetch dropdown list data to set in spinner
    private class FetchDropdownValuesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("/news/fetchdropdown.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error fetching dropdown values: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    ArrayList<String> dropdownValues = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String value = jsonObject.getString("value");
                        dropdownValues.add(value);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ActivityContact.this, android.R.layout.simple_spinner_item, dropdownValues);
                    mCountrySpinner.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing dropdown values: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Error fetching dropdown values: result is null");
            }
        }
    }


    public boolean validateControls() {

        if (mNameEditText.getText().toString().isEmpty()) {
            mNameEditText.setError("Name is required.");
            return false;
        }

        if (mEmailEditText.getText().toString().isEmpty()) {
            mEmailEditText.setError("Email is required.");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText().toString()).matches()) {
            mEmailEditText.setError("Invalid email address.");
            return false;
        }

        if (mPhoneEditText.getText().toString().isEmpty()) {
            mPhoneEditText.setError("Message is required.");
            return false;
        }

        if (field4.getText().toString().isEmpty()) {
            field4.setError("Field 4 is required.");
            return false;
        }

        if (field5.getText().toString().isEmpty()) {
            field5.setError("Field 5 is required.");
            return false;
        }

        if (field6.getText().toString().isEmpty()) {
            field6.setError("Field 6 is required.");
            return false;
        }

        if (field7.getText().toString().isEmpty()) {
            field7.setError("Field 7 is required.");
            return false;
        }

        if (field8.getText().toString().isEmpty()) {
            field8.setError("Field 8 is required.");
            return false;
        }

        if (field9.getText().toString().isEmpty()) {
            field9.setError("Field 9 is required.");
            return false;
        }

        if (field10.getText().toString().isEmpty()) {
            field10.setError("Field 10 is required.");
            return false;
        }

        if (mCountrySpinner.getSelectedItemPosition() == 0) {
            ((TextView) mCountrySpinner.getSelectedView()).setError("Country is required.");
            return false;
        }

        if (!mSubscribeSwitch.isChecked()) { // if contact switch is off
            Toast.makeText(getApplicationContext(), "Please Select Phone switch Preffered Method",Toast.LENGTH_LONG).show();
            return false;
        }
        if (contactRadioGroup.getCheckedRadioButtonId() == -1) { // and no radio button is selected
            Toast.makeText(getApplicationContext(), "Please Select Email or Mail Preffered Method",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
