package my.assignment.imdbproject.CustomeAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.zip.Inflater;

import my.assignment.imdbproject.Model.Movie;
import my.assignment.imdbproject.R;

/**
 * Created by root on 10/14/16.
 */

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Movie> data;
    private LayoutInflater inflater;
    private String tag=null;

    public ListAdapter(Context c,ArrayList<Movie> movieArrayList){
        context=c;
        data=movieArrayList;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tag="movie";
    }
    public ListAdapter(Context c,ArrayList<Movie> movieArrayList,String tag){
        context=c;
        data=movieArrayList;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tag=tag;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View rowView, ViewGroup viewGroup) {
        Holder holder =null;
        if(rowView==null) {
            holder=new Holder();
            rowView = inflater.inflate(R.layout.list_view, null);
            holder.imgV = (ImageView) rowView.findViewById(R.id.imageView);
            holder.tv=(TextView)rowView.findViewById(R.id.titletxt);
            holder.tv1=(TextView)rowView.findViewById(R.id.overviewtxt);
            holder.tv2=(TextView)rowView.findViewById(R.id.languagetxt);
            holder.tv3=(TextView)rowView.findViewById(R.id.relesedatetxt);
            holder.tv4=(TextView)rowView.findViewById(R.id.releaseTxt);
            rowView.setTag(holder);
        }else{
            holder=(Holder) rowView.getTag();
        }
        holder.tv.setText(data.get(i).getTitle());
        holder.tv1.setText(data.get(i).getDescription());
        holder.tv2.setText(data.get(i).getLang());
        holder.tv3.setText(data.get(i).getReleaseDate());
        // parse the URL
        final Uri uri = Uri.parse(data.get(i).getImagePath());

        // Glide part
        Glide.with(context)
                .load(uri)
                .override(300,400)
                .placeholder(R.mipmap.ic_launcher_imdb)
                .crossFade()
                .into(holder.imgV);

        if(tag.equals("UPCOMING"))
            holder.tv4.setText("Releasing On:");
        else
            holder.tv4.setText("Released On:");


        return rowView;
    }

    public  class Holder{
        ImageView imgV;
        TextView tv;
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
    }
}
