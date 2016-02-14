package newsfeed.newsfeed;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by abhishekgupta on 31/07/15.
 */
public class TrendClass extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {


    int SCREENS[] = {R.id.screen_list, R.id.screen_post_selected};
    ListView lvTrendingPosts = null;
    int CurrentScreenID = -1;

    ProfilePictureView fbPostPic;
    TextView tvPostTitle, tvPostContent, tvPostVotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.trend);

        switchToScreen(R.id.screen_list);

        lvTrendingPosts = (ListView) findViewById(R.id.lvTrendingPosts);
        lvTrendingPosts.setOnItemClickListener(this);

        getTrendingPosts();

        /*
        MainActivity.handler = new Handler();
        MainActivity.timer = new Timer();
        MainActivity.doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                MainActivity.handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            getTrendingPosts();
                            Log.d("setting", "trending posts");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };

        //MainActivity.timer.schedule(MainActivity.doAsynchronousTask, 0, 5000);
        */

    }

    /*
    private void setTrendingPosts() {
        lvTrendingPosts.setAdapter(new CustomListAdapter(TrendClass.this, R.layout.listitem, R.id.wordUsed, trendingPosts));
    }
    */

    private void getTrendingPosts() {

        AsyncTask<Void, Void, JSONArray> trendingPost;
        trendingPost = (new GetTrendingPostsConnection()).execute();
        JSONArray trend = null;

        try {
            trend = trendingPost.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ArrayList<TrendDetails> trendingPosts = new ArrayList<>();

        if (trend != null) {
            for (int i = 0; i < trend.length(); i++) {
                try {
                    JSONObject j = trend.getJSONObject(i);
                    int postID = j.getInt("POSTID");
                    String title = j.getString("TITLE");
                    String profilepicid = j.getString("FACEBOOKID");
                    int NumOfVotes = j.getInt("NUMOFVOTES");
                    TrendDetails td = new TrendDetails(title, profilepicid, NumOfVotes, postID);
                    trendingPosts.add(td);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d("ArrayList TrendPosts", trendingPosts.toString());

        lvTrendingPosts.setAdapter(new CustomListAdapter(TrendClass.this, R.layout.listitem, R.id.tvTitle, trendingPosts));
    }

    void switchToScreen(int screenID) {


        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenID == id ? View.VISIBLE : View.GONE);
        }

        CurrentScreenID = screenID;

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TrendClass", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TrendClass", "OnResume");
        getTrendingPosts();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TrendDetails td = (TrendDetails) parent.getItemAtPosition(position);

        Log.d("TRENDCLASS FBID", td.profilePicId);
        switchToScreen(R.id.screen_post_selected);
        initializePostSelectedScreen(td.postID, td.profilePicId, td.title, td.NumOfVotes);
    }

    private void initializePostSelectedScreen(int postID, String profilePicId, String title, int numOfVotes) {
        fbPostPic = (ProfilePictureView) findViewById(R.id.fbPostPic);
        fbPostPic.setProfileId(profilePicId);

        tvPostTitle = (TextView) findViewById(R.id.tvPostTitle);
        tvPostTitle.setText(title);

        tvPostVotes = (TextView) findViewById(R.id.tvPostVotes);
        tvPostVotes.setText(numOfVotes + " Votes");

        tvPostContent = (TextView) findViewById(R.id.tvPostContent);
        tvPostContent.setText(getPostContent(postID));
    }

    private String getPostContent(int postID) {


        AsyncTask<Void, Void, String> getContentDetails;
        getContentDetails = (new GetContentConnection(postID)).execute();

        String contentDetails = "No Content";

        try {
            contentDetails = getContentDetails.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (contentDetails != null) {
            return contentDetails;
        } else {
            return "No Content";
        }

    }

    @Override
    public void onBackPressed() {

        if (CurrentScreenID == R.id.screen_post_selected) {
            switchToScreen(R.id.screen_list);
        } else {
            super.onBackPressed();
        }
    }

    class TrendDetails {

        String title;
        String profilePicId;
        int NumOfVotes;
        int postID;

        public TrendDetails(String title, String profilePicId, int NumOfVotes, int postID) {
            this.postID = postID;
            this.title = title;
            this.profilePicId = profilePicId;
            this.NumOfVotes = NumOfVotes;
        }

    }

}
