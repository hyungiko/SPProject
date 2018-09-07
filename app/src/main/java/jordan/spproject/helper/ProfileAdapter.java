package jordan.spproject.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jordan.spproject.R;

/**
 * Created by hyungiko on 9/1/18.
 */

public class ProfileAdapter extends ArrayAdapter<DataModelProfile>  {
    private ArrayList<DataModelProfile> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtProfileFeature;
        TextView txtProfileContent;
    }

    public ProfileAdapter(ArrayList<DataModelProfile> data, Context context) {
        super(context, R.layout.row_item_profile, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelProfile dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_profile, parent, false);
            viewHolder.txtProfileFeature = (TextView) convertView.findViewById(R.id.profile_feature);
            viewHolder.txtProfileContent = (TextView) convertView.findViewById(R.id.profile_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtProfileFeature.setText(dataModel.getProfileFeature());
        viewHolder.txtProfileContent.setText(dataModel.getProfileContent());
        // Return the completed view to render on screen
        return convertView;
    }
}
