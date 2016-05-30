package com.piatt.udacity.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieReviewsAdapter.MovieReviewViewHolder;
import com.piatt.udacity.popularmovies.model.MovieReview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewViewHolder> {
    @Setter private List<MovieReview> movieReviews = new ArrayList<>();

    public MovieReviewsAdapter(List<MovieReview> movieReviews) {
        setMovieReviews(movieReviews);
    }

    @Override
    public int getItemCount() {
        return movieReviews.size();
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_item, parent, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        holder.authorView.setText(movieReviews.get(position).getAuthor());
        holder.contentView.setText(movieReviews.get(position).getContent());
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author_view) TextView authorView;
        @BindView(R.id.content_view) TextView contentView;

        public MovieReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}