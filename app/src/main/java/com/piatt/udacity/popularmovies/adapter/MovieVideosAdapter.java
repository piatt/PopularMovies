package com.piatt.udacity.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieVideosAdapter.MovieVideoViewHolder;
import com.piatt.udacity.popularmovies.model.MovieVideo;

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
            Toast.makeText(itemView.getContext(), movieVideos.get(getAdapterPosition()).getVideoUrl(), Toast.LENGTH_SHORT).show();
        }
    }
}