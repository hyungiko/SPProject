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
 * Created by hyungiko on 8/31/18.
 */

public class HistoryAdapter extends ArrayAdapter<DataModel> {

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtChattingId;
        TextView txtLastContent;
        TextView txtLastTime;
    }

    public HistoryAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtChattingId = (TextView) convertView.findViewById(R.id.chatterId);
            viewHolder.txtLastContent = (TextView) convertView.findViewById(R.id.chattingContent);
            viewHolder.txtLastTime = (TextView) convertView.findViewById(R.id.chattingTime);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtChattingId.setText(dataModel.getChattingId());
        viewHolder.txtLastContent.setText(dataModel.getLastContent());
        viewHolder.txtLastTime.setText(dataModel.getLastTime());
        // Return the completed view to render on screen
        return convertView;
    }
}
