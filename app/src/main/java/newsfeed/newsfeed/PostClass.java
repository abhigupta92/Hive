package newsfeed.newsfeed;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;

import java.util.concurrent.ExecutionException;

/**
 * Created by abhishekgupta on 31/07/15.
 */
public class PostClass extends Activity implements View.OnClickListener {


    EditText etTitle, etContent;
    Spinner spCategory;
    Button bSubmit;
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.post);

        profile = Profile.getCurrentProfile();

        if (profile != null) {
            Log.d("Post Class FB name", profile.getName());
            Log.d("Post Class FB id ", profile.getId());
            TextView tvID = (TextView) findViewById(R.id.tvID);
            tvID.setText("Welcome " + profile.getName());

        }


        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        bSubmit = (Button) findViewById(R.id.bSubmit);

        bSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSubmit:

                ContentValues contentValues = new ContentValues();
                contentValues.put("profileID", profile.getId());
                contentValues.put("name", profile.getName());
                contentValues.put("title", etTitle.getText().toString());
                contentValues.put("content", etContent.getText().toString());
                contentValues.put("category", spCategory.getSelectedItem().toString());

                Log.d("Post Class CV", contentValues.toString());
                AsyncTask<Void, Void, Boolean> req;
                req = (new PostRequestConnection(contentValues)).execute();
                try {
                    if (req.get()) {
                        etTitle.setText("");
                        etTitle.setHint("Max 20 chars");
                        etContent.setText("");
                        etContent.setHint("Max 200 chars");
                        hideKeyboard();
                        Toast.makeText(getApplicationContext(), "Post Submitted !", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
