package edward.com.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView scanResult;
            public TextView scanTime;
            public ImageButton btnCopyLink;
            public ViewHolder(View itemView) {
                super(itemView);

                scanResult = (TextView) itemView.findViewById(R.id.txtScanResult);
                scanTime = itemView.findViewById(R.id.txtScanTime);
                btnCopyLink = itemView.findViewById(R.id.btnCopy);
            }
            public void setData(String res, String time) {
                scanResult.setText(res);
                scanTime.setText(time);
            }
        }

        private List<History> historyList;
        public HistoryAdapter(List<History> histories){
            historyList = histories;
        }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.history_adapter, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        History history = historyList.get(position);

        // Set item views based on your views and data model
        holder.setData(history.getResult(), history.getDateTime());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
