package my.assignment.imdbproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import my.assignment.imdbproject.Model.Config;


public class MovieTrailerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {


    Bundle bundle;
    ArrayList<HashMap<String,String>>videoList;
    TextView textView, rateMessage, textView2;
    YouTubePlayerView playerView;
    RatingBar ratingBar,ratingBar2;
    String guest_session_id="";
    HorizontalScrollView hview;
    String videoKey="";
    YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list_item);
        getActionBar();
        bundle = getIntent().getBundleExtra("movie");
        videoList=(ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("videoIdList");
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);

        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(this, 0).show();
            this.finish();
        }
        textView = (TextView) findViewById(R.id.text);
        textView.setText("Offical Trailer:" + bundle.getString("title") + " ("+bundle.getString("year")+")");
        textView2 = (TextView) findViewById(R.id.textView2);
        rateMessage = (TextView) findViewById(R.id.rateMessage);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar2=(RatingBar)findViewById(R.id.ratingBar2);
        hview=(HorizontalScrollView)findViewById(R.id.hscrollview);
        playerView = (YouTubePlayerView) findViewById(R.id.youtube_playerview);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingBar2.setVisibility(View.VISIBLE);
            }
        });



        LinearLayout topLinearLayout = new LinearLayout(this);
        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        Log.i("Hashmap",videoList.toString());
        int i=0;//counter for trailers
        for(HashMap<String, String> map: videoList) {

            for(Map.Entry<String, String> mapEntry: map.entrySet()) {

                String key = mapEntry.getKey();
                final String value = mapEntry.getValue();
                TextView tv=new TextView(this);
                tv.setTextSize(20);
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                Log.i("Value",value+""+i);
                if(i!=videoList.size()-1)
                    tv.setText(key+"  "+"| ");
                else
                    tv.setText(key);
                i++;
                topLinearLayout.addView(tv);
                videoKey=value;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        videoKey=value;
                       player.cueVideo(videoKey);

                       // Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                    }
                });

            }
        }

        hview.addView(topLinearLayout);
        playerView.initialize(Config.YOUTUBE_API_KEY,MovieTrailerActivity.this);


        if (!bundle.getString("rating").equals("0")) {
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(Float.parseFloat(bundle.getString("rating")));
            rateMessage.setText("("+bundle.getString("rating") + "/10"+")");
        }

        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                String ratedValue = String.valueOf(ratingBar.getRating());
                new PostRatingData().execute(ratedValue);
                Toast.makeText(getApplicationContext(),"You have rated the Movie:"+ratedValue+"/10.",Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        Log.i("player", "player is playing");
        if (null == player) return;
        if (videoKey.isEmpty()) {
            Toast.makeText(this, "Trailer is not Available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        // Start buffering
        if (!b) {
            this.player=player;
            player.cueVideo(videoKey);
        }
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
// Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(new PlayerStateChangeListener() {
            @Override
            public void onAdStarted() {
            }

            @Override
            public void onError(ErrorReason arg0) {
            }

            @Override
            public void onLoaded(String arg0) {
            }

            @Override
            public void onLoading() {
            }

            @Override
            public void onVideoEnded() {
            }

            @Override
            public void onVideoStarted() {
            }
        });
        player.setPlaybackEventListener(new PlaybackEventListener() {
            @Override
            public void onBuffering(boolean arg0) {
            }

            @Override
            public void onPaused() {
            }

            @Override
            public void onPlaying() {
            }

            @Override
            public void onSeekTo(int arg0) {
            }

            @Override
            public void onStopped() {
            }
        });


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, getString(R.string.player_error), Toast.LENGTH_LONG).show();
    }


    private class PostRatingData extends AsyncTask<String, Void, String> {

        PostRatingData(){
            new GetSessionId().execute("http://api.themoviedb.org/3/authentication/guest_session/new?api_key=8496be0b2149805afa458ab8ec27560c");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL("https://api.themoviedb.org/3/movie/" + bundle.getString("movieId") + "/rating?api_key=8496be0b2149805afa458ab8ec27560c&guest_session_id="+guest_session_id); // here is your URL path
                Log.i("url", url.toString());

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("value", strings[0]);

                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 201) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }

    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    private class GetSessionId extends AsyncTask<String, Void, String> {
        String error = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader br = null;
            URL url = null;
            String content = "";


            try {

                url = new URL(strings[0]);
                Log.i("Session url",url.toString());
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
            Log.i("Content:",content);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (error != null) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

            } else {
                try {

                    JSONObject respone = new JSONObject(result);
                    String session_id = respone.getString("guest_session_id");
                    guest_session_id=session_id;

                } catch (JSONException ex) {
                    Log.e("Session Info:", "one or more field not found in JSON data");
                    ex.printStackTrace();
                }
            }
        }

    }
}





