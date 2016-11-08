package my.assignment.imdbproject;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

import my.assignment.imdbproject.CustomeAdapter.Pager;

public class TabActivity extends AppCompatActivity {
    ViewPager vp;
    Pager adapter;
    TabLayout tabLay;
    UpcomingFragment upcomingFragment;
    NowPlayingFragment nowPlayingFragment;
    PopularFragment popularFragment;
    TopRatedFragment topRatedFragment;
    LatestFragment latestFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Adding ToolBar to the Activity
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.movie_icon_1);

        //initializing the tab layout
        tabLay=(TabLayout)findViewById(R.id.tablayout);

        //Creating our pager adapter
        vp=(ViewPager)findViewById(R.id.viewPager);
        adapter = new Pager(getSupportFragmentManager(), tabLay.getTabCount());
        upcomingFragment=new UpcomingFragment();
        nowPlayingFragment=new NowPlayingFragment();
        popularFragment=new PopularFragment();
        topRatedFragment=new TopRatedFragment();
        latestFragment=new LatestFragment();

        adapter.addFragment(upcomingFragment, "Coming Soon");
        adapter.addFragment(nowPlayingFragment, "Now Playing");
       // adapter.addFragment(latestFragment, "Latest");
        adapter.addFragment(popularFragment, "Most Popular");
        adapter.addFragment(topRatedFragment, "Top Rated");

        //Adding adapter to pager
        vp.setAdapter(adapter);
        tabLay.setupWithViewPager(vp);
        createTab();
        Toast.makeText(this,"Click on Movie to play the trailer",Toast.LENGTH_LONG).show();

    }
    public void createTab(){
        TextView tabOne=(TextView) LayoutInflater.from(this).inflate(R.layout.custome_tab,null);
        tabOne.setText("Coming Soon");
        tabLay.getTabAt(0).setCustomView(tabOne);
        TextView tabTwo=(TextView) LayoutInflater.from(this).inflate(R.layout.custome_tab,null);
        tabTwo.setText("Now Playing");
        tabLay.getTabAt(1).setCustomView(tabTwo);
      //  TextView tabThree=(TextView) LayoutInflater.from(this).inflate(R.layout.custome_tab,null);
        //tabThree.setText("Latest");
        //tabLay.getTabAt(2).setCustomView(tabThree);
        TextView tabFour=(TextView) LayoutInflater.from(this).inflate(R.layout.custome_tab,null);
        tabFour.setText("Most Popular");
        tabLay.getTabAt(2).setCustomView(tabFour);
        TextView tabFive=(TextView) LayoutInflater.from(this).inflate(R.layout.custome_tab,null);
        tabFive.setText("Top Rated");
        tabLay.getTabAt(3).setCustomView(tabFive);
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refreshmenu){
        switch (tabLay.getSelectedTabPosition()){
            case 0:
                upcomingFragment.refreshList();
                break;
            case 1:
                nowPlayingFragment.refreshList();
                break;
            case 2:
                popularFragment.refreshList();
                break;
            case 3:
                topRatedFragment.refreshList();
                break;
            case 4:
               // LatestFragment.refreshList();
                break;
        }

        }
        return super.onOptionsItemSelected(item);
    }

}
