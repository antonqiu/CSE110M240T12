package com.cyruszhang.cluboard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyruszhang.cluboard.activity.ClubDetail;
import com.cyruszhang.cluboard.adapter.ClubQueryRecyclerAdapter;
import com.cyruszhang.cluboard.parse.Club;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageClubsFragment extends Fragment {

    RecyclerView clubRecyclerView;
    ClubQueryRecyclerAdapter clubRecyclerViewAdapter;

    public ManageClubsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_clubs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupClubListview(view);
    }

    private void setupAdapter() {
        ParseQueryAdapter.QueryFactory<Club> factory =
                new ParseQueryAdapter.QueryFactory<Club>() {
                    public ParseQuery<Club> create() {
                        ParseQuery<Club> query = Club.getQuery();
                        query.orderByDescending("createdAt");
                        // skip property item
                        query.whereNotEqualTo("clubID", 0);
                        // owner verify
                        query.whereEqualTo("owner", ParseUser.getCurrentUser());
                        return query;
                    }
                };
        clubRecyclerViewAdapter = new ClubQueryRecyclerAdapter<Club, ClubQueryRecyclerAdapter.ListViewHolder>(factory, true) {
            @Override
            public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                // Inflate the custom layout
                View contactView = inflater.inflate(R.layout.club_list_item, parent, false);

                // Return a new holder instance
                return new ListViewHolder(contactView);
            }

            @Override
            public void onBindViewHolder(final ListViewHolder holder, int position) {
                final Club thisClub = getThisClub(position);
                final TextView clubName = holder.clubName,
                        clubDetail = holder.clubDetail;
                clubName.setText(thisClub.getClubName());
                clubDetail.setText(thisClub.getClubDesc());
                holder.thisView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = holder.thisView.getContext();
                        Intent intent = new Intent(context, ClubDetail.class);
                        intent.putExtra("OBJECT_ID", thisClub.getObjectId());
                        context.startActivity(intent);
                    }
                });
            }
        };
    }

    private void setupClubListview(View view) {

        clubRecyclerView = (RecyclerView) view.findViewById(R.id.manage_clubs_event_recycler);
        clubRecyclerView.setAdapter(clubRecyclerViewAdapter);
        clubRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void refreshClubList() {
        clubRecyclerViewAdapter.loadObjects();
        clubRecyclerViewAdapter.notifyDataSetChanged();
    }
}
