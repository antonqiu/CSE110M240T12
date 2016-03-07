package com.cyruszhang.cluboard.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.activity.ClubDetail;
import com.cyruszhang.cluboard.activity.Home;
import com.cyruszhang.cluboard.adapter.ClubQueryRecyclerAdapter;
import com.cyruszhang.cluboard.adapter.EventQueryRecyclerAdapter;
import com.cyruszhang.cluboard.parse.Club;
import com.cyruszhang.cluboard.parse.Event;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SwipeRefreshLayout swipeRefresh;
    ParseQueryAdapter<Event> eventsQueryAdapter;
    RecyclerView recommendClubsRecyclerView;
    ClubQueryRecyclerAdapter recommendClubsQueryAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private RecyclerView eventRecyclerView;
    private EventQueryRecyclerAdapter eventRecyclerViewAdapter;
    private View savedView;
    private Bundle savedState;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedView = view;
        savedState = savedInstanceState;

        // swipe refresh
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.home_swiperefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshEventList();
            }
        });

        ParseQuery<Club> query = Club.getQuery();
        query.selectKeys(Arrays.asList("nextIndex"));
        query.getFirstInBackground(new GetCallback<Club>() {
            @Override
            public void done(Club object, ParseException e) {
                int maxIndex = object.getNextIndex();
                setupClubAdapter(maxIndex);
                setupEventAdapter();
                setupClubListview(view);
                setupEventListview(view);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case Home.MENU_ITEM_REFRESH:
                swipeRefresh.setRefreshing(true);
                refreshEventList();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupClubAdapter(final int maxIndex) {
        // random index generator

        Random ranGen = new Random();
        final ArrayList<Integer> list = new ArrayList<>();
        if (maxIndex < 6) {
            for (int i = 1; i <= maxIndex; i++) {
                list.add(i);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                int thisIndex = ranGen.nextInt(maxIndex) + 1;
                if (list.contains(thisIndex)) {
                    i--; continue;
                } else {
                    list.add(thisIndex);
                }
            }
        }

        ParseQueryAdapter.QueryFactory<Club> recommendClubsfactory =
                new ParseQueryAdapter.QueryFactory<Club>() {
                    public ParseQuery<Club> create() {
                        ParseQuery<Club> query = Club.getQuery();
//                        // only query on two keys to save time
//                        query.selectKeys(Arrays.asList("name", "desc"));
                        query.whereContainedIn("clubID", list);
                        query.orderByAscending("UpdatedAt");
                        Log.d(getClass().getSimpleName(), "factory created");
                        return query;
                    }
                };

        recommendClubsQueryAdapter = new ClubQueryRecyclerAdapter<Club, ClubQueryRecyclerAdapter.ListViewHolder>(recommendClubsfactory, true) {
            @Override
            public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                // Inflate the custom layout
                View contactView = inflater.inflate(R.layout.club_card_item, parent, false);

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

    private void setupEventAdapter() {
        ParseQueryAdapter.QueryFactory<Event> upcomingEventsFactory =
                new ParseQueryAdapter.QueryFactory<Event>() {
                    @Override
                    public ParseQuery<Event> create() {
                        ParseQuery<Event> query = Event.getQuery();
                        // only query on two keys to save time
                        query.selectKeys(Arrays.asList("objectId", "name", "following", "fromTime", "toTime", "desc", "location"));
                        query.include("following").include("followingUsers");
                        query.include("following").include("count");
                        query.orderByAscending("fromTime");
                        Date rightNow = Calendar.getInstance().getTime();
                        query.whereGreaterThanOrEqualTo("fromTime", rightNow);
                        Log.d("factory", "factory created");
                        return query;
                    }
                };
        eventRecyclerViewAdapter = new EventQueryRecyclerAdapter(upcomingEventsFactory, true);
    }

    private void setupClubListview(View view) {
        recommendClubsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_home_recommend_club_recycler);
        recommendClubsRecyclerView.setAdapter(recommendClubsQueryAdapter);
        recommendClubsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    private void setupEventListview(View view) {
        eventRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_home_upcoming_events_recycler);
        eventRecyclerView.setAdapter(eventRecyclerViewAdapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void refreshEventList() {
        onViewCreated(savedView, savedState);
        eventRecyclerViewAdapter.loadObjects();
        eventRecyclerViewAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
