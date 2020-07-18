package com.kakosepise.test.kakosepise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button m_toastButton, m_viewAllButton;
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
