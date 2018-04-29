package com.example.mohamed.akelny_serverside.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.mohamed.akelny_serverside.R;


/**
 * Created by mohamed on 4/13/18.
 */

public class OrderDetailViewHolder_ser extends RecyclerView.ViewHolder {

    public TextView name, quantity, price, discount;

    public OrderDetailViewHolder_ser(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.productName);
        quantity = (TextView) itemView.findViewById(R.id.productQuantity);
        price = (TextView) itemView.findViewById(R.id.productPrice);
        discount = (TextView) itemView.findViewById(R.id.productDiscount);

    }


}