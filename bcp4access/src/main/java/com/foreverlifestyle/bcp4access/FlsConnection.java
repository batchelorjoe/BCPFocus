package com.foreverlifestyle.bcp4access;

import android.content.ServiceConnection;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by joe on 5/28/15.
 */
public class FlsConnection extends AsyncTask<String,Integer,String>
{
    private static String fls_url = "foreverlifestyle.com/";
    private String fls_key;
    public FlsConnection() {
    }

    @Override
    protected String doInBackground(String... strings) {
        if (strings == null || strings.length == 0) {
            //System.out.println("We got nothing");
            return null;
        } else {
            return readServiceDescription((String) strings[0]);
            }
    }


    private String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n>0) out.append(new String(b,0,n));

        }
        return out.toString();
    }
    private String readServiceDescription(String url_string) {
        HttpURLConnection urlConnection;
           try {
               URL url = new URL("http://www.foreverlifestyle.com/index_1.php?format=text&method=hello");
               urlConnection = (HttpURLConnection) url.openConnection();
               InputStream in = new BufferedInputStream(urlConnection.getInputStream());
               urlConnection.disconnect();
               return readStream(in);
               }catch (MalformedURLException bad_url) {  return "Bad URL";
               }catch (IOException bad_io) { return "Bad Connection"; }
        finally {
           }

    }
    private String readStream(InputStream is) {
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
