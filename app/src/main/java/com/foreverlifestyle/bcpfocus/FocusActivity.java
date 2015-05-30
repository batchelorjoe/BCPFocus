package com.foreverlifestyle.bcpfocus;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.foreverlifestyle.bcp4access.FlsConnection;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FocusActivity extends ActionBarActivity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    FlsConnection flsConnection;
    String lastmsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        flsConnection = new FlsConnection();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();

    }
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                saveItems();
                return true;
            }
        });

    }

    private void readItems() {
        File filesDir = getFilesDir();
        File focusFile = new File(filesDir, ".bcp.txt");

        try {
            items = new ArrayList<String>(FileUtils.readLines(focusFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
            e.printStackTrace();
        }
        new RetrieveUserKeyTask().execute("rlToRssFeed");
       items.add("Something from feed");

    }





    private void  saveItems() {
        File filesDir = getFilesDir();
        File focusFile = new File(filesDir, ".bcp.txt");
        try {
            FileUtils.writeLines(focusFile, items);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addFocusItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        itemsAdapter.add(etNewItem.getText().toString());
        etNewItem.setText(lastmsg);
        saveItems();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_focus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Create an internal class for AsycTask
    class RetrieveUserKeyTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {

                //URL url= new URL(urls[0]);
                HttpURLConnection urlConnection;

                try {
                    URL url = new URL("http://www.foreverlifestyle.com/index_1.php?format=text&method=hello");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    urlConnection.disconnect();
                    lastmsg = readStream(in);
                    return lastmsg;
                } catch (MalformedURLException bad_url) {
                    return "Bad URL";
                } catch (IOException bad_io) {
                    return "Bad Connection";
                }
            }
        }

        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }

        protected String readStream(InputStream is) {

            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

    }

