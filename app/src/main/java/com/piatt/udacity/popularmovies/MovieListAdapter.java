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
    private ArrayList<MovieDetailItem> movieDetailItems = new ArrayList<>();

    public MovieListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return movieDetailItems.size();
    }

    @Override
    public MovieDetailItem getItem(int position) {
        return movieDetailItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMovieDetailItems(ArrayList<MovieDetailItem> movieDetailItems) {
        this.movieDetailItems = movieDetailItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView moviePosterView = (ImageView) convertView;

        if (moviePosterView == null) {
            moviePosterView = new ImageView(context);
            moviePosterView.setAdjustViewBounds(true);
        }

        moviePosterView.setTag(getItem(position));
        Picasso.with(context).load(getItem(position).getPosterUrl()).into(moviePosterView);

        return moviePosterView;
    }

    /**
     * Called by the MovieListService while in a master/detail flow, this method acts as a programmatic click of an adapter item.
     * This is done to make sure that the detail view displays content either on first launch or on rotation.
     */
    public void getCurrentView(int itemPosition) {
        ((MovieListActivity) context).viewMovieDetails(getItem(itemPosition));
    }
}