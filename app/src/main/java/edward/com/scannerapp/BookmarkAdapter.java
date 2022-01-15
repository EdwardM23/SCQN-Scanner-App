package edward.com.scannerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edward.com.scannerapp.model.Bookmark;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private BookmarkAdapter.onItemClickListener itemListener;

    public interface onItemClickListener {
        void onItemCopy(int position);
        void onItemDelete(int position);
    }

    public void setOnItemClickListener(BookmarkAdapter.onItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView scanResult;
        public TextView scanTime;
        public ImageButton btnCopyLink, btnDelete;
        public ViewHolder(View itemView) {
            super(itemView);

            scanResult = (TextView) itemView.findViewById(R.id.txtScanResult);
            scanTime = itemView.findViewById(R.id.txtScanTime);
            btnCopyLink = itemView.findViewById(R.id.btnCopy);
            btnDelete = itemView.findViewById(R.id.btnDelete);

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
        public void setData(String res, String time) {
            scanResult.setText(res);
            scanTime.setText(time);
        }
    }

    private List<Bookmark> bookmarkList;
    public BookmarkAdapter(List<Bookmark> bookmarks){
        bookmarkList = bookmarks;
    }

    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.bookmark_adapter, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(BookmarkAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Bookmark bookmark = bookmarkList.get(position);

        // Set item views based on your views and data model
        holder.setData(bookmark.getResult(), bookmark.getDateTime());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }
}
