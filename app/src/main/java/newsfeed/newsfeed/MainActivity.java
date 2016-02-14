package newsfeed.newsfeed;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements View.OnClickListener {

    LoginButton fbLogin;
    CallbackManager callbackManager;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    TabHost tabHost;
    LocalActivityManager mLocalActivityManager;
    static public ArrayList<String> trendingPosts = null;
    int SCREENS[] = {R.id.screen_loggedIN, R.id.screen_login};
    SharedPreferences sharedpreferences;
    boolean first;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);
        switchToScreen(R.id.screen_login);
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        first = sharedpreferences.getBoolean("First", true);
        Log.d("MainActivity first : ", String.valueOf(first));

        if (!first)
            updateWithToken(AccessToken.getCurrentAccessToken());

        if (!first) {
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                           AccessToken currentAccessToken) {
                    Log.d("TRACKER", currentAccessToken.getToken());
                    updateWithToken(currentAccessToken);
                }
            };

            accessTokenTracker.startTracking();
            Log.d("TRACKING", String.valueOf(accessTokenTracker.isTracking()));
        }

        fbLogin = (LoginButton) findViewById(R.id.login_button);
        fbLogin.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_hometown"));

        fbLogin.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.d("MainActivity json", object.toString());

                                ContentValues params = new ContentValues();

                                try {
                                    params.put("FACEBOOKID", object.getString("id"));
                                    params.put("NAME", object.getString("name"));
                                    params.put("EMAIL", object.getString("email"));
                                    params.put("AGE", object.getString("birthday"));
                                    JSONObject hometown = object.getJSONObject("hometown");
                                    params.put("HOMETOWN", hometown.getString("name"));
                                    params.put("NUMOFPOSTS", "0");
                                    params.put("LASTPOSTED", "0");

                                    Log.d("MainActivity", params.toString());
                                    AsyncTask<Void, Void, Boolean> register;
                                    register = (new RegisterUserConnection(params).execute());

                                    if (register.get()) {
                                        Log.d("SUCCESSFUL", "REGISTERED");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,birthday,hometown");
                request.setParameters(parameters);
                request.executeAsync();
                Log.d("FB LOGIN", "SUCCESS");
                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putBoolean("First", false);
                edit.commit();
                switchToScreen(R.id.screen_loggedIN);

            }

            @Override
            public void onCancel() {
                // App code
                Log.d("FB LOGIN", "Fail");
                switchToScreen(R.id.screen_login);
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });


        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        mLocalActivityManager = new LocalActivityManager(this, true);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);

        // Tab for Post
        TabHost.TabSpec postTab = tabHost.newTabSpec("Post");
        postTab.setIndicator("Post");
        final Intent postIntent = new Intent(this, PostClass.class);
        postTab.setContent(postIntent);

        // Tab for Read
        TabHost.TabSpec readTab = tabHost.newTabSpec("Read");
        readTab.setIndicator("Read");
        Intent readIntent = new Intent(this, TrendClass.class);
        readTab.setContent(readIntent);

        //Tab for Profile
        TabHost.TabSpec profileTab = tabHost.newTabSpec("Profile");
        profileTab.setIndicator("Profile");
        Intent profileIntent = new Intent(this, ProfileClass.class);
        profileTab.setContent(profileIntent);

        tabHost.addTab(postTab);
        tabHost.addTab(profileTab);
        tabHost.addTab(readTab);

        if (!first)
            tabHost.setCurrentTab(1);

    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            Log.d("UPDATE WITH TOKEN", "NOT NULL");
            switchToScreen(R.id.screen_loggedIN);
        } else {
            Log.d("UPDATE WITH TOKEN", "NULL");
        }
    }

    public boolean isLoggedIn() {

        if (FacebookSdk.isInitialized()) {
            if (accessToken == null) {
                return false;
            } else {
                return accessToken.getToken() != null;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    void switchToScreen(int screenID) {

        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenID == id ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

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
        trendingPosts = new ArrayList<>();

        if (trend != null) {
            for (int i = 0; i < trend.length(); i++) {
                try {
                    JSONObject j = trend.getJSONObject(i);
                    trendingPosts.add(j.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        // lvTrendingPosts.setAdapter(new CustomListAdapter(TrendClass.this, R.layout.listitem, R.id.wordUsed, trendingPosts));
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    */

    @Override
    public void onPause() {
        super.onPause();
        try {
            mLocalActivityManager.dispatchPause(isFinishing());
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!first)
            switchToScreen(R.id.screen_loggedIN);
        try {
            mLocalActivityManager.dispatchResume();
        } catch (Exception e) {
        }
    }


}
