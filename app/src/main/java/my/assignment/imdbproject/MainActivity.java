package my.assignment.imdbproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.net.URLConnection;
import java.util.ArrayList;

import my.assignment.imdbproject.CustomeAdapter.ListAdapter;
import my.assignment.imdbproject.Model.Movie;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String videoId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();
        listView=(ListView)findViewById(R.id.list_item);
        new FetchUpcomingMoviesData("UPCOMING").execute("http://api.themoviedb.org/3/movie/upcoming?api_key=8496be0b2149805afa458ab8ec27560c");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String []s=new String[3];

                Movie m =(Movie)listView.getItemAtPosition(i);
                s[0]=m.getTitle();
                String urlstring="http://api.themoviedb.org/3/movie/"+m.getMovieId()+"/videos?api_key=8496be0b2149805afa458ab8ec27560c";
                        s[1]=urlstring.replace(" ","%20");
                s[2]=m.getRating();
                new FetchUpcomingMoviesData("TRAILER").execute(s);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refreshmenu){
            listView.setAdapter(null);
            new FetchUpcomingMoviesData("UPCOMING").execute("http://api.themoviedb.org/3/movie/upcoming?api_key=8496be0b2149805afa458ab8ec27560c");


        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchUpcomingMoviesData extends AsyncTask<String,Void,Void>{
        String tag;
        ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        String content;
        String error;
        String title="";
        String rating="";
        Movie m;
        ArrayList<Movie>movieArrayList=new ArrayList<Movie>();
        ListAdapter  mAdapter;

        public FetchUpcomingMoviesData(String s){
            tag=s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... s) {
            BufferedReader br=null;
            URL url=null;


            try {
                if(tag.equals("UPCOMING"))
                url=new URL(s[0]);
                if(tag.equals("TRAILER")) {
                    url = new URL(s[1]);
                    title=s[0];
                    rating=s[2];
                }
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                Log.d("Status",responseCode+"");

                br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line=null;

                while ((line=br.readLine())!=null){
                    sb.append(line+"\n");

                }
                content=sb.toString();

            } catch (MalformedURLException e) {
                error=e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                error=e.getMessage();
                e.printStackTrace();
            }finally {
                try {
                    br.close();
                } catch (IOException e) {
                    error=e.getMessage();
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(error!=null){
                Toast.makeText(MainActivity.this,error,Toast.LENGTH_LONG).show();

            }else{
                if(tag.equals("UPCOMING")) {
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
                            String rate=jsonArray.getJSONObject(i).getString("vote_average");
                            Log.i("Async Task", posterPath);
                            m = new Movie(title.trim(), description.trim(), releaseDate.trim(), language.trim(), "http://image.tmdb.org/t/p/w500" + posterPath, movieId.trim(),rate.trim());
                            movieArrayList.add(m);
                        }
                        mAdapter = new ListAdapter(MainActivity.this, movieArrayList);
                        listView.setAdapter(mAdapter);

                    } catch (JSONException ex) {
                        Log.e("Movie Info:", "one or more field not found in JSON data");
                        ex.printStackTrace();
                    }
                }
                if(tag.equals("TRAILER")){
                    try {
                        videoId="";
                        int count=0;
                        JSONObject respone = new JSONObject(content);
                        JSONArray jsonArray = respone.getJSONArray("results");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String type = jsonArray.getJSONObject(i).getString("type");
                            String key = jsonArray.getJSONObject(i).getString("key");
                            Log.i("Async Task", type+" "+key);
                            if(type.trim().equals("Trailer")) {
                                videoId=key;
                                count++;
                                break;
                            }

                        }
                        if(count==0){
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String type = jsonArray.getJSONObject(i).getString("type");
                                String key = jsonArray.getJSONObject(i).getString("key");
                                Log.i("Async Task", type+" "+key);
                                if(type.trim().equals("Teaser")) {
                                    videoId=key;
                                    count++;
                                    break;
                                }

                            }

                        }
                        if(count==0){
                            videoId="";
                        }
                        Bundle movieBundle=new Bundle();
                        movieBundle.putString("videoId",videoId);
                        movieBundle.putString("title",title);
                        movieBundle.putString("rating",rating);
                        Intent trailerIntent=new Intent(getApplicationContext(),MovieTrailerActivity.class);
                        trailerIntent.putExtra("movie",movieBundle);
                        startActivity(trailerIntent);



                    }catch (JSONException ex){
                        Log.e("Movie Info:","one or more field not found in JSON data");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


}
