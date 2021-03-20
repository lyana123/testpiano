package com.example.mypiano.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mypiano.R;
import com.example.mypiano.fragement.SongList.SongListActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link resourceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class resourceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView textViewForBeforeCut,textViewForAfterCut;

    public resourceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment resourceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static resourceFragment newInstance(String param1, String param2) {
        resourceFragment fragment = new resourceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resource, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        textViewForBeforeCut=(TextView)getActivity().findViewById(R.id.song_bofore_cut);
        textViewForAfterCut=(TextView)getActivity().findViewById(R.id.song_after_cut);

        textViewForBeforeCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showMidList = new Intent(getActivity().getApplicationContext(), SongListActivity.class);
                showMidList.putExtra("type", "before");
                startActivity(showMidList);
            }
        });
        textViewForAfterCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showMidList = new Intent(getActivity().getApplicationContext(), SongListActivity.class);
                showMidList.putExtra("type", "after");
                startActivity(showMidList);
            }
        });
    }
}
