package com.piatt.udacity.popularmovies.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieVideosAdapter.MovieVideoViewHolder;
import com.piatt.udacity.popularmovies.event.EventBusUnregisterEvent;
import com.piatt.udacity.popularmovies.event.MovieShareEvent;
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
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void unregisterEventBus(EventBusUnregisterEvent event) {
        Log.d(getClass().getSimpleName(), "EventBusUnregisterEvent");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void shareMovieVideo(MovieShareEvent event) {
        if (!movieVideos.isEmpty()) {
            String videoUrl = movieVideos.get(0).getVideoUrl();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUrl);
            shareIntent.setType("text/plain");
            MoviesApplication.getApp().startActivity(Intent.createChooser(shareIntent, "Share link").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

        @OnClick(R.id.play_button)
        public void onPlayButtonClick() {
            Intent playIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieVideos.get(getAdapterPosition()).getVideoUrl()));
            playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MoviesApplication.getApp().startActivity(playIntent);
        }
    }
}