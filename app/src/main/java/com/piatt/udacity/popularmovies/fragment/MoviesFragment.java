package com.piatt.udacity.popularmovies.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MoviesFragment extends Fragment {
    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private MoviesViewHolder moviesViewHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (moviesViewHolder == null) {
            View view = inflater.inflate(R.layout.movies_fragment, container, false);
            moviesViewHolder = new MoviesViewHolder(view);
        }
        return moviesViewHolder.movieList;
    }

//    /**
//     * On options menu initialization, the spinner containing the sort filters is created
//     * and it's default setting is set based on a stored preference, if it exists.
//     */
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_list, menu);
//        spinnerView = (Spinner) menu.findItem(R.id.action_sort).getActionView();
//        spinnerView.setSelection(ContextManager.getCurrentSortFilterPosition());
//        spinnerView.setOnItemSelectedListener(this);
//    }
//
//    /**
//     * This handler is invoked when a movie poster item is clicked inside of the gridview.
//     * The value of the item clicked is stored as a preference for future reference,
//     * and the appropriate action is taken by the LaunchActivity to show the movie item's details.
//     */
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ContextManager.setCurrentMovieItem(position);
//        ContextManager.getMovieDetails(getActivity(), (int) view.getTag());
//    }
//
//    /**
//     * This handler is invoked when the sort filter dropdown value changes.
//     * Since this handler may be invoked after initial menu creation and on orientation changes,
//     * in addition to user toggling of the sort filter, a request for movie data is only made
//     * when the user has made the change and when it is determined that the new value is different from the current stored preference.
//     */
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        if (view != null && position != ContextManager.getCurrentSortFilterPosition()) {
//            ContextManager.setPrefetched(false);
//            ContextManager.setCurrentSortFilter(position);
//            getMovieList();
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {}
//
//    /**
//     * While in a master/detail flow, a programmatic click of an adapter item is invoked.
//     * This is done to make sure that the detail view displays content either on first launch, list update, or on rotation.
//     */
//    private void doItemClick() {
//        if (ContextManager.isLargeLayout()) {
//            int currentMovieItem = ContextManager.getCurrentMovieItem();
//            gridView.requestFocusFromTouch();
//            gridView.setSelection(currentMovieItem);
//            gridView.performItemClick(gridView.getAdapter().getView(currentMovieItem, null, null), currentMovieItem, currentMovieItem);
//        }
//    }
//
//    /**
//     * Additionally, although it appears to make a network call each time the gridView is created,
//     * the ContextManager handles response caching, delivering the data to the view instantly from cache, if available, even offline.
//     * This eliminates the need for the fragment itself to handle instance state.
//     */
//    /**
//     * Called by the movieListCallback after notifying the MovieListAdapter of a data set change,
//     * this method kicks off prefetching of additional movie detail information if the list of movie items has not yet been cached.
//     * Otherwise, all responses are returned from the cache.
//     */
//    private void doDataPrefetch(ArrayList<Movie> movies) {
//        if (!ContextManager.isPrefetched()) {
//            for (Movie movie : movies) {
//                ContextManager.movieDataService.getMovieDetails(movie.getId(), movieInfoCallback);
//                ContextManager.movieDataService.getMovieExtras(movie.getId(), ContextManager.API_REVIEWS_ENDPOINT, movieInfoCallback);
//                ContextManager.movieDataService.getMovieExtras(movie.getId(), ContextManager.API_VIDEOS_ENDPOINT, movieInfoCallback);
//            }
//            ContextManager.setPrefetched(true);
//        }
//    }
//
//    /**
//     * This method determines which list of data to load into the MoviesFragment based on the current sort filter.
//     * This method is invoked either on initial app launch, or orientation change, or on sort filter change.
//     */
//    private void getMovieList() {
//        if (ContextManager.getCurrentSortFilter().equals(ContextManager.API_FAVORITES_ENDPOINT)) {
//            MovieListAdapter movieListAdapter = (MovieListAdapter) gridView.getAdapter();
//            movieListAdapter.notifyDataSetInvalidated();
//            movieListAdapter.setListItems(new ArrayList<Movie>(ContextManager.getCurrentFavorites().size()));
//
//            for (Integer movieId : ContextManager.getCurrentFavorites()) {
//                ContextManager.movieDataService.getMovieDetails(movieId, movieFavoritesCallback);
//            }
//        } else {
//            ContextManager.movieDataService.getMovieList(ContextManager.getCurrentSortFilter(), movieListCallback);
//        }
//    }
//
//    /**
//     * Upon receipt of data, either from the network or cache,
//     * this callback method parses and sends the Movie objects to the MovieListAdapter.
//     * Additionally, prefetching of MovieDetailItems is done in the background to support performance and offline access.
//     */
//    private Callback<JsonObject> movieListCallback = new Callback<JsonObject>() {
//        @Override
//        public void success(JsonObject jsonObject, Response response) {
//            JsonArray movies = jsonObject.get(ContextManager.API_RESULTS_FILTER).getAsJsonArray();
//            ArrayList<Movie> movieItems = new ArrayList<>();
//            MovieListAdapter movieListAdapter = (MovieListAdapter) gridView.getAdapter();
//
//            for (int i = 0; i < movies.size(); i++) {
//                Movie movie = new Movie(movies.get(i).getAsJsonObject());
//                movieItems.add(movie);
//            }
//
//            movieListAdapter.setListItems(movieItems);
//            movieListAdapter.notifyDataSetChanged();
//            doItemClick();
//            doDataPrefetch(movieItems);
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//            String message = ContextManager.isNetworkAvailable() ? ContextManager.ERROR_MESSAGE : ContextManager.ERROR_NETWORK;
//            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    /**
//     * Called by the ContextManager in response to a request to update the detail view,
//     * this callback method is invoked upon receipt of data either from the network or cache,
//     * and populates a new DetailItem for use in the DetailFragment.
//     */
//    private Callback<JsonObject> movieFavoritesCallback = new Callback<JsonObject>() {
//        @Override
//        public void success(JsonObject jsonObject, Response response) {
//            MovieListAdapter movieListAdapter = (MovieListAdapter) gridView.getAdapter();
//            movieListAdapter.addMovieListItem(new Movie(jsonObject));
//            movieListAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//            Log.d(LOG_TAG, error.getMessage());
//        }
//    };
//
//    /**
//     * This callback method is invoked by the doDataPrefetch method in order to prefetch data for later use
//     * by a new DetailFragment. Since the response only needs to be cached, nothing is done on manually or on the UI on success.
//     */
//    private Callback<JsonObject> movieInfoCallback = new Callback<JsonObject>() {
//        @Override
//        public void success(JsonObject jsonObject, Response response) {}
//
//        @Override
//        public void failure(RetrofitError error) {
//            Log.d(LOG_TAG, error.getMessage());
//        }
//    };

    public class MoviesViewHolder {
        @Bind(R.id.movie_list) RecyclerView movieList;

        public MoviesViewHolder(View view) {
            ButterKnife.bind(this, view);
            movieList.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            movieList.setAdapter(new MovieListAdapter());
        }
    }
}