package com.example.mohamed.akelny_serverside.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohamed.akelny_serverside.Common.Common;
import com.example.mohamed.akelny_serverside.Interface.ItemClickListner;
import com.example.mohamed.akelny_serverside.R;


/**
 * Created by mohamed on 3/18/18.
 */

public class FoodListViewHolder_ser extends RecyclerView.ViewHolder implements
        View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView foodname;
    public ImageView foodimag;

    private ItemClickListner itemClickListner;


    public FoodListViewHolder_ser(View itemView) {
        super(itemView);

        foodname = (TextView) itemView.findViewById(R.id.food_name);
        foodimag = (ImageView) itemView.findViewById(R.id.image_food);

        itemView.setOnClickListener(this);  //for action in items
        itemView.setOnCreateContextMenuListener(this); //for menu show when long click

    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle(" (choose one) ");

        menu.add(0,0,getAdapterPosition(), Common.Update);
        menu.add(0,1,getAdapterPosition(), Common.Delete);
    }
}

