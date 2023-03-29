package com.app.androidnewsapp.activities;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SendContactDataTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "SendContactDataTask";
    private static final String SERVER_URL = "http://192.168.22.226:8888/news/contact.php";
    private final Map<String, String> mData;

    public SendContactDataTask(Map<String, String> data) {
        mData = data;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // Create the HTTP connection to the server
            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Add the data to the JSON object
            JSONObject json = new JSONObject();
            for (String key : mData.keySet()) {
                json.put(key, mData.get(key));
            }

            // Convert the JSON object to a string
            String jsonString = json.toString();
            Log.d(TAG, "JSON string: " + jsonString);

            // Set the content type to JSON and write the data to the output stream
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            OutputStream os = conn.getOutputStream();
            os.write(jsonString.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            // Read the response from the server
            int responseCode = conn.getResponseCode();
            InputStream is;
            if (responseCode >= 200 && responseCode < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Check if the server returned a success response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getBoolean("success");
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error sending contact data", e);
            return false;
        }
    }



    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            // Contact data sent successfully
            Log.d(TAG, "Contact data sent successfully");
        } else {
            // Error sending contact data
            Log.e(TAG, "Error sending contact data");
        }
    }
}
