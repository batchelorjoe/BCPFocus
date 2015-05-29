package com.foreverlifestyle.bcp4access;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by joe on 5/28/15.
 */
public class FlsConnection {
    private static String fls_url = "foreverlifestyle.com/";
    private String fls_key;
    public FlsConnection() {
    }
    public boolean getConnectionStatus() {
        StringBuffer items = new StringBuffer();


        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet("http://www.foreverlifestyle.com/index_1.php?format=text&method=hello");
        String text = null;
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            text = getASCIIContentFromEntity(entity);
            items.append(text);
            return true;

        } catch (Exception e) {
            items.append("No Item");
        }

        return false;
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

}
