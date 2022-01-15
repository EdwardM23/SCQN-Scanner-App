package edward.com.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edward.com.scannerapp.model.History;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private HistoryAdapter.onItemClickListener itemListener;

    public interface onItemClickListener {
        void onItemBookmark(int position);
        void onItemCopy(int position);
        void onItemDelete(int position);
    }

    public void setOnItemClickListener(HistoryAdapter.onItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView scanResult;
            public TextView scanTime;
            public ImageButton btnCopyLink, btnDelete, btnBookmark;
            public ViewHolder(View itemView) {
                super(itemView);

                scanResult = (TextView) itemView.findViewById(R.id.txtScanResult);
                scanTime = itemView.findViewById(R.id.txtScanTime);
                btnCopyLink = itemView.findViewById(R.id.btnCopy);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnBookmark = itemView.findViewById(R.id.btnBookmark);

                btnBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int itemPosition = getAdapterPosition();
                        if (itemPosition != RecyclerView.NO_POSITION) {
                            itemListener.onItemBookmark(itemPosition);
                        }
                    }
                });

                btnCopyLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemListener != null) {
                            int itemPosition = getAdapterPosition();
                            if (itemPosition != RecyclerView.NO_POSITION) {
                                itemListener.onItemCopy(itemPosition);
                            }
                        }
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemListener != null) {
                            int itemPosition = getAdapterPosition();
                            if (itemPosition != RecyclerView.NO_POSITION) {
                                itemListener.onItemDelete(itemPosition);
                            }
                        }
                    }
                });
            }
            public void setData(String res, String time, boolean isBookmarked) {
                scanResult.setText(res);
                scanTime.setText(time);

                // Untuk menentukan suatu item bookmarked/gk
                if (isBookmarked) {
                    btnBookmark.setImageResource(R.drawable.btn_bookmark);
                }
                else {
                    btnBookmark.setImageResource(R.drawable.btn_bookmark_border);
                }
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
        holder.setData(history.getResult(), history.getDateTime(), history.isBookmarked());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return historyList.size();
    }
}
