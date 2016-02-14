package newsfeed.newsfeed;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abhishekgupta on 05/08/15.
 */
public class GetTrendingPostsConnection extends AsyncTask<Void, Void, JSONArray> {


    @Override
    protected JSONArray doInBackground(Void... params) {

        try {

            URL url = new URL("http://abhishek.activexenon.com/newsfeed_trendingPosts.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();

            int resCode;

            resCode = conn.getResponseCode();
            InputStream in;

            String data;
            JSONArray details = null;
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((data = reader.readLine()) != null) {
                    details = new JSONArray(data);
                }
            }

            if (details != null) {
                return details;
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
