package edward.com.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;
            public ImageButton btnCopyLink;
            public ViewHolder(View itemView) {
                super(itemView);

                text = (TextView) itemView.findViewById(R.id.text);
                btnCopyLink = itemView.findViewById(R.id.btnCopy);
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
        View contactView = inflater.inflate(R.layout.history_list, parent, false);

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
        TextView textView = holder.text;
        textView.setText((CharSequence) history.getText());
        ImageButton btnCopyLink = holder.btnCopyLink;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
