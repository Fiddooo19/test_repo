package com.example.socialgoodvolunteerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    /**
     * Create ViewHolder class to bind list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvEventName;
        public TextView tvDate;
        public TextView tvCategory;
        public TextView tvLocation;
        public TextView tvDescription;
        public ViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnLongClickListener(this);  //register long click action to this viewholder instance
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    } // close ViewHolder class

    //////////////////////////////////////////////////////////////////////
    // adapter class definitions

    private List<Event> EventListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)
    private String role;

    public EventAdapter(Context context, List<Event> listData) {
        EventListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout

        View view;

        // Inflate layout based on user role
        if ("user".equals(role)) {
            view = inflater.inflate(R.layout.event_list_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.event_list_item_admin, parent, false);
        }

        //View view = inflater.inflate(R.layout.event_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance

        Event m = EventListData.get(position);
        holder.tvEventName.setText(m.getEvent_name());
        holder.tvCategory.setText(m.getCategory());
        holder.tvLocation.setText(m.getLocation());
    }

    @Override
    public int getItemCount() {
        return EventListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public Event getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && EventListData !=null && currentPos<EventListData.size()) {
            return EventListData.get(currentPos);
        }
        return null;
    }

}



