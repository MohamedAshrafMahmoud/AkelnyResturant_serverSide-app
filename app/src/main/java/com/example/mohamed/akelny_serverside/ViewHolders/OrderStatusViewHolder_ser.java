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
 * Created by mohamed on 3/24/18.
 */

public class OrderStatusViewHolder_ser extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAdress;
    public TextView edit, remove, details;
    // public ImageView menuimag;

    private ItemClickListner itemClickListner;


    public OrderStatusViewHolder_ser(View itemView) {
        super(itemView);

        // txtOrderId = (TextView) itemView.findViewById(R.id.orederid);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.orederstatus);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.orederphone);
        txtOrderAdress = (TextView) itemView.findViewById(R.id.orederadress);
        edit = (TextView) itemView.findViewById(R.id.edit);
        remove = (TextView) itemView.findViewById(R.id.remove);
        details = (TextView) itemView.findViewById(R.id.details);
        //    menuimag = (ImageView) itemView.findViewById(R.id.image_menu);

       // itemView.setOnClickListener(this);  //for action in items
       // itemView.setOnCreateContextMenuListener(this); //for menu show when long click

    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }


}
