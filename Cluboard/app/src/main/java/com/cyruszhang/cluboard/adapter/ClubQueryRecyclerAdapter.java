package com.cyruszhang.cluboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public static class GridViewHolder extends RecyclerView.ViewHolder {

        public TextView clubName, clubDetail;
        public ImageView bkg;
        public View thisView;

        public GridViewHolder(View v) {
            super(v);
            thisView = v;
            // TODO: put all columns!
            clubName = (TextView) v.findViewById(R.id.club_list_item_name);
            clubDetail = (TextView) v.findViewById(R.id.club_list_item_desc);
//            bkg = (ImageView) v.findViewById(R.id.club_list_item_background);
        }
    }

}
