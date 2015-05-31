package com.foreverlifestyle.bcpfocus;

import android.os.AsyncTask;
import android.provider.Settings;
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
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

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
        String deviceId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        items.add(deviceId);
        /*
        Start the AsyncTasks
         */
        /**  Example code, do not run
         *

        try {
            URL url1 = new URL("http://foreverlifestyle.com/download/file1.txt");
            URL url2 = new URL("http://foreverlifestyle.com/download/file2.txt");
            URL url3 = new URL("http://foreverlifestyle.com/download/file3.txt");
            new DownloadFilesTask().execute(url1, url2, url3);
        }catch(Exception e){}
       */


        try{
           // String deviceId = Settings.Secure.getString(this.getContentResolver(),
             //       Settings.Secure.ANDROID_ID);
            new RetrieveUserKeyTask().execute(deviceId);
        }catch(Exception e){}

        /*

        End of AsyncTasks
        */

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

       //items.add("Something from feed");

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


        etNewItem.setText("");
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


    /*
    Internal Class begin

     */







    /**
     *  Sample class for ayncTask to down load files
     */


    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
               //totalSize += Downloader.downloadFile(urls[i]);
                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            //showDialog("Downloaded " + result + " bytes");
        }
    }

    /** Supporting methods for internal class
     *
     *
     */


    /**
     * Fls class for Service Key
     */

    // Create an internal class for AsycTask
    class RetrieveUserKeyTask extends AsyncTask<String, String, String> {

        private ArrayList<String> keys;
        private ArrayAdapter<String> keysAdapter;

        private Exception exception;

        protected String doInBackground(String... urls) {

                //URL url= new URL(urls[0]);
                HttpURLConnection urlConnection;




                try {

                    String base_url="http://www.foreverlifestyle.com/index_1.php?";
                    String response_format = "text";
                    String service_method = "key";
                    String devid = urls[0].toString();
String url_string = base_url + "format=" + response_format + "&method=" + service_method + "&id="  + devid;

                    URL url = new URL(url_string);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    publishProgress("working");
                    urlConnection.disconnect();
                    lastmsg = readStream(in);
                    //itemsAdapter.add(urls[0]);
                   // itemsAdapter.add(lastmsg);
                    return lastmsg;
                } catch (MalformedURLException bad_url) {
                    items.add("Bad URL");
                    return "Bad URL";
                } catch (IOException bad_io) {
                    items.add("Bad Connection");
                    return "Bad Connection";
                }
            }
        protected void onProgressUpdate(Integer... progress) {
            items.add("progress");
        }

        protected void onPostExecute(Long result) {
            items.add("postExecute long");
        }

        protected void  saveKey() {
            File filesDir = getFilesDir();
            File focusFile = new File(filesDir, "bcp.key");
            try {
                FileUtils.writeLines(focusFile, keys);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }
    /*     End of class : RetrieveUserKeyTask    */




    /*
    End of internal classes
     */

    }

