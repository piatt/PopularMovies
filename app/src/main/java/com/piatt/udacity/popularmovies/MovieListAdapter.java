package com.piatt.udacity.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends BaseAdapter {
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<MovieListItem> movieListItems = new ArrayList<>();

    public MovieListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return movieListItems.size();
    }

    @Override
    public MovieListItem getItem(int position) {
        return movieListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMovieListItems(ArrayList<MovieListItem> movieListItems) {
        this.movieListItems = movieListItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView moviePosterView = (ImageView) convertView;

        if (moviePosterView == null) {
            moviePosterView = new ImageView(context);
            moviePosterView.setAdjustViewBounds(true);
        }

        moviePosterView.setTag(getItem(position).getId());
        Picasso.with(context).load(getItem(position).getPosterUrl()).into(moviePosterView);

        return moviePosterView;
    }
}