package com.example.gautham.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivityFragment extends Fragment {

    public String movieJsonStr = null;
    final String backgroundPathBase = "http://image.tmdb.org/t/p/w185";
    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public static int numberFilms =0;
    public static String[] finalBackdropPathStrArray;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        return rootView;
    }

    private void updateMovies() {
        fetchMovieTask fetchInfo = new fetchMovieTask();
        String prefTemp = "popularity.desc";
        fetchInfo.execute(prefTemp);

    }

    @Override
    public void onStart() {

        super.onStart();
        updateMovies();

    }

    public class fetchMovieTask extends AsyncTask<String, Void, String[]> {

        public String[] getMovieDataFromJson(String movieStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String OBJ_BACKDROP_PATH = "backdrop_path";
            final String OBJ_TITLE = "original_title";
            final String OBJ_RESULTS = "results";
            final String OBJ_OVERVIEW = "overview";
            final String OBJ_RELEASE_DATE = "release_date";
            final String OBJ_USER_RATING = "vote_average";

            JSONObject filmJsonSorter = new JSONObject(movieStr);
            JSONArray resultsArray = filmJsonSorter.getJSONArray(OBJ_RESULTS);
            numberFilms =resultsArray.length();

            String[] titleStrArray = new String[resultsArray.length()];
            String[] backdropPathStrArray = new String[resultsArray.length()];
             finalBackdropPathStrArray = new String[resultsArray.length()];
            String[] releaseDateStrArray = new String[resultsArray.length()];
            String[] userRatingStrArray = new String[resultsArray.length()];
            String[] overviewStrArray = new String[resultsArray.length()];


            for(int y=0; y<resultsArray.length();y++) {

                JSONObject movieInfo = resultsArray.getJSONObject(y);

                String bdp = movieInfo.getString(OBJ_BACKDROP_PATH);
                backdropPathStrArray[y] = bdp;
                //Log.e(LOG_TAG,"backdrop path:"+bdp);

                String mtitle = movieInfo.getString(OBJ_TITLE);
                titleStrArray[y] = mtitle;
                //Log.e(LOG_TAG,"title:"+tit);

                String mdescription = movieInfo.getString(OBJ_OVERVIEW);
                overviewStrArray[y] = mdescription;
               //Log.e(LOG_TAG,"description:"+description);

                String reldat = movieInfo.getString(OBJ_RELEASE_DATE);
                releaseDateStrArray[y] = reldat;
                //Log.e(LOG_TAG,"release date:"+reldat);

                String ur = movieInfo.getString(OBJ_USER_RATING);
                userRatingStrArray[y] = ur;
                //Log.e(LOG_TAG,"user rating:"+ur);

            } //loop end

            for(int z=0; z<resultsArray.length();z++){
                String temp = backgroundPathBase+backdropPathStrArray[z];
               finalBackdropPathStrArray[z] = temp;

            }

            return null;

        }


        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {

                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String FORECAST_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String SORT_MODE = "sort_by";
                final String KEY = "ed2d4035999bef82e38aeab256677ccf";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_MODE, params[0])
                        .appendQueryParameter(API_KEY, KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "URL: " + url);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG,"json:"+movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error with internet ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    Log.e(LOG_TAG,"problem with internet");
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


    }
}
