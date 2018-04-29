package com.example.mohamed.akelny_serverside;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mohamed.akelny_serverside.Common.Common;
import com.example.mohamed.akelny_serverside.Interface.ItemClickListner;
import com.example.mohamed.akelny_serverside.Model.Foods;
import com.example.mohamed.akelny_serverside.ViewHolders.FoodListViewHolder_ser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList_ser extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;

    Foods newfood;

    FirebaseRecyclerAdapter<Foods, FoodListViewHolder_ser> adapter;

    RecyclerView recycler_Menu;
    FloatingActionButton fab;
    RelativeLayout rootLayout;
    Button select, upload;
    EditText edtname,edtdescription,edtprice,editdiscount;

    Uri saveuri;
    private final int pickImageRequest = 71;

    String categoryId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_ser);


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        recycler_Menu = (RecyclerView) findViewById(R.id.recyclerFoods);
        recycler_Menu.setHasFixedSize(true);
        recycler_Menu.setLayoutManager(new LinearLayoutManager(this));

        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);

         fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 addItemDialog();

             }
         });


        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("categoryId");
        }
        if (!categoryId.isEmpty() && categoryId != null) {
            loadListFood(categoryId);
        }
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Foods, FoodListViewHolder_ser>(Foods.class, R.layout.item_food_list_ser, FoodListViewHolder_ser.class
                , databaseReference.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodListViewHolder_ser viewHolder, Foods model, int position) {

                viewHolder.foodname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.foodimag);

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        addItemDialog();

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recycler_Menu.setAdapter(adapter);

    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.Update)) {
            updateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.Delete)) {
            deleteItem(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //function delete item
    private void deleteItem(String key) {
        databaseReference.child(key).removeValue();
        Toast.makeText(this, " Item "+newfood.getName() +" deleted", Toast.LENGTH_SHORT).show();

    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //functions of update updateDialog and updateUploadImage
    private void updateDialog(final String key, final Foods item) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList_ser.this);
        alertDialog.setTitle("Update food");
        alertDialog.setMessage("please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_food_list_ser, null);

        edtname = view.findViewById(R.id.edtnamee);
        edtdescription = view.findViewById(R.id.edtdiscount);
        edtprice = view.findViewById(R.id.edtprice);
        editdiscount = view.findViewById(R.id.edtdiscount);

        select = view.findViewById(R.id.btnselect);
        upload = view.findViewById(R.id.btnupload);


        //select default name
        edtname.setText(item.getName());
        edtdescription.setText(item.getDescription());
        edtprice.setText(item.getPrice());
        editdiscount.setText(item.getDiscount());

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();   //let user choose image and save uri of it
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUploadImage(item);
            }
        });

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_local_grocery_store_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //update information
                item.setName(edtname.getText().toString());
                item.setDescription(edtdescription.getText().toString());
                item.setPrice(edtprice.getText().toString());
                item.setDiscount(editdiscount.getText().toString());

                databaseReference.child(key).setValue(item);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveuri = data.getData();
            select.setText("Image selected  !");
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addItemDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList_ser.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_food_list_ser, null);

        edtname = view.findViewById(R.id.edtnamee);
        edtdescription = view.findViewById(R.id.edtdesc);
        edtprice = view.findViewById(R.id.edtprice);
        editdiscount = view.findViewById(R.id.edtdiscount);
        select = view.findViewById(R.id.btnselect);
        upload = view.findViewById(R.id.btnupload);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();   //let user choose image and save uri of it
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_local_grocery_store_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if (newfood != null) {
                    databaseReference.push().setValue(newfood);
                    Snackbar.make(rootLayout, "New category " + newfood.getName() + " was Added", Snackbar.LENGTH_LONG).show();
                }

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

    private void UploadImage() {

        if (saveuri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading ...");
            dialog.show();

            String imgName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imgName);
            imageFolder.putFile(saveuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList_ser.this, "Uploaded ", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newfood = new Foods();
                                    newfood.setName(edtname.getText().toString());
                                    newfood.setDescription(edtdescription.getText().toString());
                                    newfood.setPrice(edtprice.getText().toString());
                                    newfood.setDiscount(editdiscount.getText().toString());
                                    newfood.setMenuId(categoryId);
                                    newfood.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(FoodList_ser.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded" + progress + "%");
                        }
                    });
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateUploadImage(final Foods item) {

        if (saveuri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading ...");
            dialog.show();

            String imgName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imgName);
            imageFolder.putFile(saveuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList_ser.this, "Uploaded ", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(FoodList_ser.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Upload " + progress + "%");
                        }
                    });
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void ChooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select picture"), pickImageRequest);
    }
}
