package my.assignment.imdbproject;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import my.assignment.imdbproject.CustomeAdapter.ListAdapter;
import my.assignment.imdbproject.Model.Movie;


/**
 * A simple {@link Fragment} subclass.
 */
public class LatestFragment extends Fragment {


    public LatestFragment() {
        // Required empty public constructor
    }

    ListView listView;
    String videoId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_latest, container, false);
        listView = (ListView) rooView.findViewById(R.id.list_latest_item);
        new FetchLatestMoviesData("LATEST").execute("http://api.themoviedb.org/3/movie/latest?api_key=8496be0b2149805afa458ab8ec27560c");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] s = new String[5];

                Movie m = (Movie) listView.getItemAtPosition(i);
                s[0] = m.getTitle();
                String urlstring = "http://api.themoviedb.org/3/movie/" + m.getMovieId() + "/videos?api_key=8496be0b2149805afa458ab8ec27560c";
                s[1] = urlstring.replace(" ", "%20");
                s[2] = m.getRating();
                s[3]=m.getMovieId();
                s[4]= m.getReleaseDate();
                new FetchLatestMoviesData("TRAILER").execute(s);

            }
        });
        return rooView;
    }

    private class FetchLatestMoviesData extends AsyncTask<String, Void, Void> {
        String tag;
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        String content;
        String error;
        String title = "";
        String rating = "";
        String movieId="";
        String year="";
        Movie m;
        ArrayList<Movie> movieArrayList = new ArrayList<Movie>();
        ListAdapter mAdapter;

        public FetchLatestMoviesData(String s) {
            tag = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... s) {
            BufferedReader br = null;
            URL url = null;


            try {
                if (tag.equals("LATEST"))
                    url = new URL(s[0]);
                if (tag.equals("TRAILER")) {
                    url = new URL(s[1]);
                    title = s[0];
                    rating = s[2];
                    movieId=s[3];
                    year=s[4].substring(0,4);
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                Log.d("Status", responseCode + "");

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");

                }
                content = sb.toString();

            } catch (MalformedURLException e) {
                error = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                error = e.getMessage();
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    error = e.getMessage();
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();

            } else {
                if (tag.equals("LATEST")) {
                    try {
                        JSONObject respone = new JSONObject(content);
                        JSONArray jsonArray = respone.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String title = jsonArray.getJSONObject(i).getString("title");
                            String posterPath = jsonArray.getJSONObject(i).getString("poster_path");
                            String description = jsonArray.getJSONObject(i).getString("overview");
                            String releaseDate = jsonArray.getJSONObject(i).getString("release_date");
                            String language = jsonArray.getJSONObject(i).getString("original_language");
                            String movieId = jsonArray.getJSONObject(i).getString("id");
                            String rate = jsonArray.getJSONObject(i).getString("vote_average");
                          //  Log.i("Async Task", posterPath);
                            m = new Movie(title.trim(), description.trim(), releaseDate.trim(), language.trim(), "http://image.tmdb.org/t/p/w500" + posterPath, movieId.trim(), rate.trim());
                            movieArrayList.add(m);
                        }
                        mAdapter = new ListAdapter(getContext(), movieArrayList);
                        listView.setAdapter(mAdapter);

                    } catch (JSONException ex) {
                        Log.e("Movie Info:", "one or more field not found in JSON data");
                        ex.printStackTrace();
                    }
                }
                if (tag.equals("TRAILER")) {
                    try {
                        ArrayList<HashMap<String,String>>videoList;
                        JSONObject respone = new JSONObject(content);
                        JSONArray jsonArray = respone.getJSONArray("results");
                        videoList=new ArrayList<HashMap<String,String>>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String type = jsonArray.getJSONObject(i).getString("type");
                            String key = jsonArray.getJSONObject(i).getString("key");

                            HashMap<String,String> map=new HashMap<>();
                            map.put(type,key);
                            videoList.add(map);

                        }

                        Bundle movieBundle=new Bundle();
                        movieBundle.putString("title",title);
                        movieBundle.putString("rating",rating);
                        movieBundle.putString("movieId",movieId);
                        movieBundle.putString("year",year);

                        Intent trailerIntent=new Intent(getContext(),MovieTrailerActivity.class);
                        trailerIntent.putExtra("movie",movieBundle);
                        trailerIntent.putExtra("videoIdList",videoList);
                        startActivity(trailerIntent);

                    } catch (JSONException ex) {
                        Log.e("Movie Info:", "one or more field not found in JSON data");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    public void refreshList(){
        listView.setAdapter(null);
        new FetchLatestMoviesData("LATEST").execute("http://api.themoviedb.org/3/movie/latest?api_key=8496be0b2149805afa458ab8ec27560c");


    }

}
