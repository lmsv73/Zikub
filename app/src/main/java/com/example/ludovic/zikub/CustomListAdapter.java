package com.example.ludovic.zikub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomListAdapter extends ArrayAdapter<Search>{
    ArrayList<Search> searches;
    Context context;
    int resource;

    public CustomListAdapter(Context context, int resource, ArrayList<Search> searches) {
        super(context, resource, searches);
        this.searches = searches;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.liste_video, null, true);
        }
        Search search = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        Picasso.with(context).load(search.getImage()).into(imageView);

        TextView txtName = (TextView) convertView.findViewById(R.id.title);
        txtName.setText(search.getTitle());

        return convertView;

    }
}
