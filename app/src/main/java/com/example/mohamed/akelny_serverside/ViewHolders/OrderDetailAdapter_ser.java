package com.example.mohamed.akelny_serverside.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mohamed.akelny_serverside.Model.Order;
import com.example.mohamed.akelny_serverside.R;


import java.util.List;

/**
 * Created by mohamed on 4/13/18.
 */

public class OrderDetailAdapter_ser extends RecyclerView.Adapter<OrderDetailViewHolder_ser> {

    private List<Order> orders;


    public OrderDetailAdapter_ser(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public OrderDetailViewHolder_ser onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_order_details, parent, false);
        return new OrderDetailViewHolder_ser(itemView);
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder_ser holder, final int position) {

        Order order = orders.get(position);

        holder.name.setText(String.format("Name :   %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantity :   %s", order.getQuantity()));
        holder.price.setText(String.format("Price :   $%s", order.getPrice()));
        holder.discount.setText(String.format("Discount :   %s", order.getDiscount()));

    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

}