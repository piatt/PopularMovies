package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    @Bind(R.id.list_grid) GridView gridView;

    public ListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        gridView.setAdapter(new ListAdapter(getActivity()));

        return view;
    }
}