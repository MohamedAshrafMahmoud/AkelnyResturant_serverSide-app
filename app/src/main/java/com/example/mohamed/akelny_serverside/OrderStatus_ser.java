package com.example.mohamed.akelny_serverside;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mohamed.akelny_serverside.Common.Common;
import com.example.mohamed.akelny_serverside.Interface.ItemClickListner;
import com.example.mohamed.akelny_serverside.Model.Category;
import com.example.mohamed.akelny_serverside.Model.Request;
import com.example.mohamed.akelny_serverside.ViewHolders.MenuHomeViewHolder_ser;
import com.example.mohamed.akelny_serverside.ViewHolders.OrderStatusViewHolder_ser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

public class OrderStatus_ser extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseRecyclerAdapter<Request, OrderStatusViewHolder_ser> adapter;


    RecyclerView recycler_Menu;
    MaterialSpinner spinner;


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_status_ser);


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Requests");


        recycler_Menu = (RecyclerView) findViewById(R.id.listCart);
        recycler_Menu.setHasFixedSize(true);
        recycler_Menu.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadOrders() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderStatusViewHolder_ser>(Request.class, R.layout.item_order_status_ser, OrderStatusViewHolder_ser.class, databaseReference) {
            @Override
            protected void populateViewHolder(OrderStatusViewHolder_ser viewHolder, final Request model, final int position) {

//                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAdress.setText(model.getAdress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));

                    }
                });

            viewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteOrder(adapter.getRef(position).getKey());

                    }
                });

            viewHolder.details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(OrderStatus_ser.this, OrderDetails_ser.class);
                        Common.currentRequest=model;
                        intent.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();  //refresh data if there is a change
        recycler_Menu.setAdapter(adapter);

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    public boolean onContextItemSelected(MenuItem item) {
//        if (item.getTitle().equals(Common.Update)) {
//            updateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
//        } else if (item.getTitle().equals(Common.Delete)) {
//            deleteOrder(adapter.getRef(item.getOrder()).getKey());
//
//        }
//        return super.onContextItemSelected(item);
//    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //function delete item
    private void deleteOrder(String key) {
        databaseReference.child(key).removeValue();

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //functions of update updateDialog and updateUploadImage
    private void updateDialog(final String key, final Request item) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus_ser.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("please choose one status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.item_order_update_status_ser, null);

        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "on my way", "Shipped");

        alertDialog.setView(view);

        final String localkey = key;

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

               item.setStatus(String.valueOf(spinner.getSelectedIndex()));

               databaseReference.child(localkey).setValue(item);

               adapter.notifyDataSetChanged();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.show();


    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static String convertCodeToStatus(String code) {

        if (code.equals("0"))
            return "Placed";

        else if (code.equals("1"))
            return "On the way";
        else
            return "Shipping";

    }

}
