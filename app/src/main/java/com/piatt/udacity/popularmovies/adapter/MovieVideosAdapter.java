package com.piatt.udacity.popularmovies.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieVideosAdapter.MovieVideoViewHolder;
import com.piatt.udacity.popularmovies.event.MovieVideoShareEvent;
import com.piatt.udacity.popularmovies.model.MovieVideo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Setter;

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideoViewHolder> {
    @Setter private List<MovieVideo> movieVideos = new ArrayList<>();

    public MovieVideosAdapter(List<MovieVideo> movieVideos) {
        setMovieVideos(movieVideos);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        EventBus.getDefault().unregister(this);
    }

    /**
     * This event handler is invoked when the share button in the toolbar of a movie detail screen
     * is tapped by a user. Using the first trailer's YouTube URL, a new sharing intent is created,
     * giving the user a dialog from which to choose the app they would like to share the URL with.
     */
    @Subscribe
    public void onMovieVideoShare(MovieVideoShareEvent event) {
        if (!movieVideos.isEmpty()) {
            String videoUrl = movieVideos.get(0).getVideoUrl();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
            shareIntent.setType("text/plain");
            String chooserTitle = MoviesApplication.getApp().getString(R.string.share_chooser_title);
            MoviesApplication.getApp().startActivity(Intent.createChooser(shareIntent, chooserTitle).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public int getItemCount() {
        return movieVideos.size();
    }

    @Override
    public MovieVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_video_item, parent, false);
        return new MovieVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideoViewHolder holder, int position) {
        holder.titleView.setText(movieVideos.get(position).getName());
    }

    public class MovieVideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_view) TextView titleView;

        public MovieVideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * This click handler is invoked when a user taps on any row in the list of trailers
         * on a movie detail screen. Using the trailer's YouTube URL, a new viewing intent is created,
         * which will then launch trailer in the YouTube app, if installed, or else in the browser.
         */
        @OnClick(R.id.play_button)
        public void onPlayButtonClick() {
            Intent playIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieVideos.get(getAdapterPosition()).getVideoUrl()));
            playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MoviesApplication.getApp().startActivity(playIntent);
        }
    }
}