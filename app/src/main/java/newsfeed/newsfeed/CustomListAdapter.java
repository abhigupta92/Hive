package newsfeed.newsfeed;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

/**
 * Created by abhishekgupta on 05/08/15.
 */
public class CustomListAdapter extends ArrayAdapter {
    private Context mContext;
    private int id;
    private List<TrendClass.TrendDetails> items;

    public CustomListAdapter(Context context, int customlist, int textViewResourceId, List<TrendClass.TrendDetails> list) {
        super(context, customlist, list);
        mContext = context;
        id = customlist;
        items = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = convertView;

        if (mView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView title = (TextView) mView.findViewById(R.id.lvTitle);
        TextView tvVotes = (TextView) mView.findViewById(R.id.tvVotes);
        final ProfilePictureView ppv = (ProfilePictureView) mView.findViewById(R.id.profilePicID);


        if (items.get(position) != null) {

            title.setTextSize(20);
            title.setGravity(Gravity.CENTER);
            title.setText(items.get(position).title);
            ppv.setProfileId(items.get(position).profilePicId);
            tvVotes.setText(items.get(position).NumOfVotes + " Votes");
        }

        return mView;
    }
}
