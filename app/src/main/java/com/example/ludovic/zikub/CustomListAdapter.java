package com.example.ludovic.zikub;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 12/10/2017.
 */

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
