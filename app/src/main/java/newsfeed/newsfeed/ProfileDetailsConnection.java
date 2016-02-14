package newsfeed.newsfeed;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by abhishekgupta on 19/08/15.
 */
public class ProfileDetailsConnection extends AsyncTask<Void, Void, JSONObject> {

    String fbID;

    public ProfileDetailsConnection(String fbID) {

        this.fbID = fbID;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        try {
            URL url = new URL("http://abhishek.activexenon.com/newsfeed_getProfileDetails.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            StringBuilder result = new StringBuilder();

            result.append(URLEncoder.encode("FACEBOOKID", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(fbID, "UTF-8"));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            int resCode;

            resCode = conn.getResponseCode();
            InputStream in;

            String data;
            JSONObject details = null;
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((data = reader.readLine()) != null) {
                    System.out.println("VALUE OF DATA IS : " + data);
                    details = new JSONObject(data);
                }
            }

            if (details != null) {
                return details;
            } else {
                return null;
            }
        } catch (Exception e) {

        }

        return null;
    }
}
