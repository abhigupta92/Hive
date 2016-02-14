package newsfeed.newsfeed;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;

/**
 * Created by abhishekgupta on 16/06/15.
 */
public class PostRequestConnection extends AsyncTask<Void, Void, Boolean> {

    ContentValues contentValues;

    public PostRequestConnection(ContentValues contentValues) {
        this.contentValues = contentValues;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            URL url = new URL("http://abhishek.activexenon.com/newsfeed_post.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(contentValues));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            int resCode;
            resCode = conn.getResponseCode();
            InputStream in;

            String data;
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((data = reader.readLine()) != null) {
                    System.out.println("VALUE OF DATA IS : " + data);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", e.toString());
        }

        Log.d("SOMETHING WRONG", "WRONG WRONG");

        return false;
    }


    private String getQuery(ContentValues contentValues) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Set<String> keys = contentValues.keySet();

        for (String key : keys) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) contentValues.get(key), "UTF-8"));
        }

        return result.toString();
    }
}
