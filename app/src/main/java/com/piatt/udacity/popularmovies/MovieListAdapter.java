package com.piatt.udacity.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class MovieListAdapter extends BaseAdapter implements OnClickListener {
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
        ImageView imageView = (ImageView) convertView;

        if (imageView == null) {
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
            imageView.setOnClickListener(this);
        }

        imageView.setTag(getItem(position));
        MovieListService.getPicasso().load(getItem(position).getPosterUrl()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_error).into(imageView);

        return imageView;
    }

    @Override
    public void onClick(View view) {
        ((MovieListActivity) context).viewMovieDetails((MovieDetailItem) view.getTag());
    }

    public void getDefaultView() {
        Log.d(LOG_TAG, "Selected default grid item");
        ((MovieListActivity) context).viewMovieDetails(getItem(0));
    }
}