package com.cyruszhang.cluboard.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cyruszhang.cluboard.R;
import com.cyruszhang.cluboard.activity.ClubDetail;
import com.cyruszhang.cluboard.activity.Home;
import com.cyruszhang.cluboard.activity.Setting;
import com.cyruszhang.cluboard.parse.Club;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClubCatalogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClubCatalogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClubCatalogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SwipeRefreshLayout swipeRefresh;
    ParseQueryAdapter<Club> clubsQueryAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ClubCatalogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClubCatalogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClubCatalogFragment newInstance(String param1, String param2) {
        ClubCatalogFragment fragment = new ClubCatalogFragment();
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
        getActivity().setTitle("All Clubs");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setupAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_club_catalog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // swipe refresh

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.fragment_club_catablog_swiperefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(getClass().getSimpleName(), "refresh triggered");
                refreshClubList();
            }
        });

        setupClubListview(view);
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
                refreshClubList();
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

    private void setupAdapter() {
        ParseQueryAdapter.QueryFactory<Club> factory =
                new ParseQueryAdapter.QueryFactory<Club>() {
                    public ParseQuery<Club> create() {
                        ParseQuery<Club> query = Club.getQuery();
                        // only query on two keys to save time
                        query.selectKeys(Arrays.asList("name", "desc"));
                        query.orderByDescending("createdAt");
                        Log.d(getClass().getSimpleName(), "factory created");
                        return query;
                    }
                };

        clubsQueryAdapter = new ParseQueryAdapter<Club>(getContext(), factory) {
            @Override
            public View getItemView(Club object, View v, ViewGroup parent) {
                // Local DataStore

                if (v == null) {
                    v = View.inflate(getContext(), R.layout.club_list_item, null);
                }
                Log.d(getClass().getSimpleName(), "setting up item view");
                TextView clubName = (TextView) v.findViewById(R.id.club_list_item_name);
                TextView clubDetail = (TextView) v.findViewById(R.id.club_list_item_desc);
                clubName.setText(object.getClubName());
                clubDetail.setText(object.getClubDesc());
                return v;
            }
        };
    }

    private void setupClubListview(View view) {
        clubsQueryAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Club>() {
            @Override
            public void onLoading() {
                swipeRefresh.setRefreshing(true);
            }

            @Override
            public void onLoaded(List<Club> objects, Exception e) {
                swipeRefresh.setRefreshing(false);
            }
        });

        ListView clubList = (ListView) view.findViewById(R.id.fragment_club_catablog_list);
        clubList.setAdapter(clubsQueryAdapter);

        clubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Club club = clubsQueryAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ClubDetail.class);
                intent.putExtra("OBJECT_ID", club.getObjectId());
                startActivity(intent);
            }
        });
    }

    private void refreshClubList() {
        clubsQueryAdapter.loadObjects();
        clubsQueryAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }
}
