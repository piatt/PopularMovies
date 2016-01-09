package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieListFragment extends Fragment implements OnItemClickListener, OnItemSelectedListener {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @Bind(R.id.list_grid) GridView gridView;
    private Spinner spinnerView;

    public MovieListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Called on fragment creation or rotation, this method smartly reuses the gridView if it exists.
     * Additionally, although it appears to make a network call each time the gridView is created,
     * the MovieListService handles response caching, delivering the data to the view instantly from cache, if available, even offline.
     * This eliminates the need for the fragment itself to handle instance state.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (gridView == null) {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, view);

            gridView.setAdapter(new MovieListAdapter(getActivity()));
            gridView.setOnItemClickListener(this);

            getMovieList();
        }

        return gridView;
    }

    /**
     * On options menu initialization, the spinner containing the sort filters is created
     * and it's default setting is set based on a stored preference, if it exists.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
        spinnerView = (Spinner) menu.findItem(R.id.action_sort).getActionView();
        spinnerView.setSelection(MovieListService.getCurrentSortFilterPosition());
        spinnerView.setOnItemSelectedListener(this);
    }

    /**
     * This handler is invoked when a movie poster item is clicked inside of the gridview.
     * The value of the item clicked is stored as a preference for future reference,
     * and the appropriate action is taken by the MovieListActivity to show the movie item's details.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieListService.setCurrentMovieItem(position);
        MovieListService.getMovieDetails(getActivity(), (int) view.getTag());
    }

    /**
     * This handler is invoked when the sort filter dropdown value changes.
     * Since this handler may be invoked after initial menu creation and on orientation changes,
     * in addition to user toggling of the sort filter, a request for movie data is only made
     * when the user has made the change and when it is determined that the new value is different from the current stored preference.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null && position != MovieListService.getCurrentSortFilterPosition()) {
            MovieListService.setPrefetched(false);
            MovieListService.setCurrentSortFilter(position);
            getMovieList();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * While in a master/detail flow, a programmatic click of an adapter item is invoked.
     * This is done to make sure that the detail view displays content either on first launch, list update, or on rotation.
     */
    private void doItemClick() {
        if (MovieListService.isDualPane()) {
            int currentMovieItem = MovieListService.getCurrentMovieItem();
            gridView.requestFocusFromTouch();
            gridView.setSelection(currentMovieItem);
            gridView.performItemClick(gridView.getAdapter().getView(currentMovieItem, null, null), currentMovieItem, currentMovieItem);
        }
    }

    /**
     * Called by the movieListCallback after notifying the MovieListAdapter of a data set change,
     * this method kicks off prefetching of additional movie detail information if the list of movie items has not yet been cached.
     * Otherwise, all responses are returned from the cache.
     */
    private void doDataPrefetch(ArrayList<MovieListItem> movieListItems) {
        if (!MovieListService.isPrefetched()) {
            for (MovieListItem movieListItem : movieListItems) {
                MovieListService.movieDataService.getMovieDetails(movieListItem.getId(), movieInfoCallback);
                MovieListService.movieDataService.getMovieExtras(movieListItem.getId(), MovieListService.API_REVIEWS_ENDPOINT, movieInfoCallback);
                MovieListService.movieDataService.getMovieExtras(movieListItem.getId(), MovieListService.API_VIDEOS_ENDPOINT, movieInfoCallback);
            }
            MovieListService.setPrefetched(true);
        }
    }

    /**
     * This method determines which list of data to load into the MovieListFragment based on the current sort filter.
     * This method is invoked either on initial app launch, or orientation change, or on sort filter change.
     */
    private void getMovieList() {
        if (MovieListService.getCurrentSortFilter().equals(MovieListService.API_FAVORITES_ENDPOINT)) {
            MovieListAdapter movieListAdapter = (MovieListAdapter) gridView.getAdapter();
            movieListAdapter.notifyDataSetInvalidated();
            movieListAdapter.setMovieListItems(new ArrayList<MovieListItem>(MovieListService.getCurrentFavorites().size()));

            for (Integer movieId : MovieListService.getCurrentFavorites()) {
                MovieListService.movieDataService.getMovieDetails(movieId, movieFavoritesCallback);
            }
        } else {
            MovieListService.movieDataService.getMovieList(MovieListService.getCurrentSortFilter(), movieListCallback);
        }
    }

    /**
     * Upon receipt of data, either from the network or cache,
     * this callback method parses and sends the MovieListItem objects to the MovieListAdapter.
     * Additionally, prefetching of MovieDetailItems is done in the background to support performance and offline access.
     */
    private Callback<JsonObject> movieListCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            JsonArray movies = jsonObject.get(MovieListService.API_RESULTS_FILTER).getAsJsonArray();
            ArrayList<MovieListItem> movieListItems = new ArrayList<>();
            MovieListAdapter movieListAdapter = (MovieListAdapter) gridView.getAdapter();

            for (int i = 0; i < movies.size(); i++) {
                MovieListItem movieListItem = new MovieListItem(movies.get(i).getAsJsonObject());
                movieListItems.add(movieListItem);
            }

            movieListAdapter.setMovieListItems(movieListItems);
            movieListAdapter.notifyDataSetChanged();
            doItemClick();
            doDataPrefetch(movieListItems);
        }

        @Override
        public void failure(RetrofitError error) {
            String message = MovieListService.isNetworkAvailable() ? MovieListService.ERROR_MESSAGE : MovieListService.ERROR_NETWORK;
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Called by the MovieListService in response to a request to update the detail view,
     * this callback method is invoked upon receipt of data either from the network or cache,
     * and populates a new MovieDetailItem for use in the MovieDetailFragment.
     */
    private Callback<JsonObject> movieFavoritesCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            MovieListAdapter movieListAdapter = (MovieListAdapter) gridView.getAdapter();
            movieListAdapter.addMovieListItem(new MovieListItem(jsonObject));
            movieListAdapter.notifyDataSetChanged();
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, error.getMessage());
        }
    };

    /**
     * This callback method is invoked by the doDataPrefetch method in order to prefetch data for later use
     * by a new MovieDetailFragment. Since the response only needs to be cached, nothing is done on manually or on the UI on success.
     */
    private Callback<JsonObject> movieInfoCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {}

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, error.getMessage());
        }
    };
}