package newsfeed.newsfeed;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by abhishekgupta on 18/08/15.
 */
public class ProfileClass extends Activity implements OnMapReadyCallback{


    Profile profile;
    ProfilePictureView profilePic;
    ImageButton imgBtn;
    TextView tvProfileDetails, tvProfilePostDetails, tvStatusDetails, tvScreen;
    GoogleMap map;
    public View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.profile);

        profile = Profile.getCurrentProfile();
        initialiseViews();

        getProfileDetails(profile.getId());

        tvProfilePostDetails.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    public void onGlobalLayout() {
                        Log.d("GLOBAL LOADING","LOADED");
                        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                        Bitmap preview = getScreenShot(rootView);
                        imgBtn.setImageBitmap(preview);
                    }
                });

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this;
    }

    private void getProfileDetails(String fbID) {

        AsyncTask<Void, Void, JSONObject> getProfileDetails;
        getProfileDetails = (new ProfileDetailsConnection(fbID)).execute();
        JSONObject profileDetails = null;

        try {
            profileDetails = getProfileDetails.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (profileDetails != null) {
            try {
                String dob = profileDetails.getString("BIRTHDAY");
                profilePic.setProfileId(profileDetails.getString("FACEBOOKID"));
                Date date = new SimpleDateFormat("MM/dd/yyyy").parse(dob);
                int age = getAge(date);
                tvProfileDetails.setText(profileDetails.getString("NAME") + "," + age + "," + profileDetails.getString("HOMETOWN"));
                tvProfilePostDetails.setText("Total Posts : " + profileDetails.getString("NUMOFPOSTS") + "\n" + "Last Posted : " + profileDetails.getString("LASTPOSTED"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public static int getAge(Date dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
            age--;
        }

        return age;
    }

    private void initialiseViews() {
        profilePic = (ProfilePictureView) findViewById(R.id.profilePic);
        tvProfileDetails = (TextView) findViewById(R.id.tvProfileDetails);
        tvProfilePostDetails = (TextView) findViewById(R.id.tvProfilePostDetails);
        tvStatusDetails = (TextView) findViewById(R.id.tvStatusDetails);
        imgBtn = (ImageButton) findViewById(R.id.imgBtn);
        //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileDetails(profile.getId());
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private class screenShot extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... p) {
            final Bitmap[] preview = new Bitmap[1];
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // this code will be executed after 2 seconds
                    preview[0] = getScreenShot(rootView);
                    Log.d("Loaded","LOADED THE SCREEN");
                }
            }, 2000);

            return preview[0];
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

}
