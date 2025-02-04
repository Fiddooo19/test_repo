package com.example.socialgoodvolunteerapp.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialgoodvolunteerapp.R;
import com.example.socialgoodvolunteerapp.model.Event;
import com.example.socialgoodvolunteerapp.model.Participation;

import java.util.List;

public class ParticipationAdapter extends RecyclerView.Adapter<ParticipationAdapter.ViewHolder> {

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

    // adapter class definitions
    private List<Participation> participationListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public ParticipationAdapter(Context context, List<Participation> listData) {
        participationListData = listData;
        mContext = context;
    }


    private Context getmContext() {
        return mContext;
    }

    @Override
    public ParticipationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout
        View view = inflater.inflate(R.layout.activity_event_list, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Participation p = participationListData.get(position);
        holder.tvEventName.setText(p.getEvent().getEvent_name());
        holder.tvCategory.setText(p.getEvent().getcategory());
        holder.tvLocation.setText(p.getEvent().getlocation());
    }


    @Override
    public int getItemCount() {
        return participationListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public Participation getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if(currentPos>=0 && participationListData !=null && currentPos<participationListData.size()) {
            return participationListData.get(currentPos);
        }
        return null;
    }
}
