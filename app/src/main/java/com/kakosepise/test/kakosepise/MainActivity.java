package com.kakosepise.test.kakosepise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button m_toastButton, m_viewAllButton, m_restButton;
    ListView m_list;
    ArrayAdapter m_customerArrayAdapter;
    DatabaseController m_db;
    static int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_list = findViewById(R.id.list_view);
        m_toastButton = findViewById(R.id.button_init);
        m_viewAllButton = findViewById(R.id.button2);
        m_restButton = findViewById(R.id.rest_btn);

        // Database initialization
        // Step 1 - We create an empty database
        m_db = new DatabaseController(MainActivity.this);

        updateDatabase();
        showCustomersInListView();


        m_toastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Entry newEntry = new Entry(++counter, "This is some content", "Test content", "content-test");
                boolean success = m_db.addEntry(newEntry);

                showCustomersInListView();
            }
        });

        m_viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCustomersInListView();

            }
        });

        m_restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://kakosepise.com/wp-json/wp/v2/ksp_rec/?page=1";

                JsonArrayRequest m_jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray m_response) {
                        String m_searchTerm = "";
                        String m_title = "";
                        String m_content = "";
                        int m_ID = 0;
                        int m_i =0;
                        int m_done = 0;
                        String m_cmpDate = "2019-05-17T10:09:28";
                        String m_date = "";
                        try {
                            JSONObject m_term = m_response.getJSONObject(0);
                            m_searchTerm = m_term.getString("slug");
                            JSONObject m_tmpTitle = m_term.getJSONObject("title");
                            m_title = m_tmpTitle.getString("rendered");
                            m_ID = m_term.getInt("id");
                            JSONObject m_tmpContent = m_term.getJSONObject("content");
                            m_content = m_tmpContent.getString("rendered");
                            m_date = m_term.getString("date");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, m_title + m_date, Toast.LENGTH_SHORT).show();

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
                    }
                });

                RESTSingleton.getInstance(MainActivity.this).addToRequestQueue(m_jsonArrayRequest);

            }
        });

        m_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Entry clickedEntry = (Entry) adapterView.getItemAtPosition(position);
                m_db.deleteEntry(clickedEntry);
                showCustomersInListView();
                Toast.makeText(MainActivity.this, "You deleted an item", Toast.LENGTH_SHORT);
            }
        });
    }

    private void updateDatabase() {
        if (!m_db.isFilled()) {
            // Step 2 - We fill it up with the rows from dataInit.sql in the asset folder
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(getAssets().open(DatabaseController.m_INIT_PATH)));

                // do reading, usually loop until end of file reading
                String nextSql;
                while ((nextSql = reader.readLine()) != null) {
                    m_db.execCommand(nextSql.trim());
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
        }
    }

    private void showCustomersInListView() {
        // Defining an adaptor that will fill out the list view
        m_customerArrayAdapter = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, m_db.getAllEntries());
        m_list.setAdapter(m_customerArrayAdapter);
    }

    public void sendMessage(View view) {


    }
}
