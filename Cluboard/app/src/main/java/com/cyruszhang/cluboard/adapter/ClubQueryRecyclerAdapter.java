package com.cyruszhang.cluboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.activity.ClubDetail;
import com.cyruszhang.cluboard.parse.Club;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Created by AntonioQ on 3/4/16.
 */
public abstract class ClubQueryRecyclerAdapter<T extends ParseObject, U extends RecyclerView.ViewHolder>
        extends ParseRecyclerQueryAdapter<T, U> {

    private Context context;
    SwipeRefreshLayout swipeRefresh;

    public ClubQueryRecyclerAdapter(ParseQueryAdapter.QueryFactory factory, boolean hasStableIds) {
        super(factory, hasStableIds);
    }

    public ClubQueryRecyclerAdapter(String className, boolean hasStableIds) {
        super(className, hasStableIds);
    }

    public Club getThisClub(int position) {
        return (Club) getItem(position);
    }

    public ParseObject getClubRelation(int position) {
        // TODO: ?
        return null;
    }

//    @Override
//    public U onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        // Inflate the custom layout
//        View contactView = inflater.inflate(R.layout.club_list_item, parent, false);
//
//        // Return a new holder instance
//        return new ListViewHolder(contactView);
//    }

//    @Override
//    public void onBindViewHolder(U viewHolder, int position) {
//        final Club thisClub = getThisClub(position);
//        final U holder = viewHolder;
//        final TextView clubName = holder.clubName,
//                clubDetail =holder.clubDetail;
//        clubName.setText(thisClub.getClubName());
//        clubDetail.setText(thisClub.getClubDesc());
//        holder.thisView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = holder.thisView.getContext();
//                Intent intent = new Intent(context, ClubDetail.class);
//                intent.putExtra("OBJECT_ID", thisClub.getObjectId());
//                context.startActivity(intent);
//            }
//        });
//    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView clubName, clubDetail;
        public View thisView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ListViewHolder(View v) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ListViewHolder instance.
            super(v);
            thisView = v;
            // TODO: put all columns!
            clubName = (TextView) v.findViewById(R.id.club_list_item_name);
            clubDetail = (TextView) v.findViewById(R.id.club_list_item_desc);
        }
    }
}
