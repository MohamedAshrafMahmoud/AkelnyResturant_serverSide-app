package com.example.mohamed.akelny_serverside;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.akelny_serverside.Common.Common;
import com.example.mohamed.akelny_serverside.Interface.ItemClickListner;
import com.example.mohamed.akelny_serverside.Model.Category;
import com.example.mohamed.akelny_serverside.ViewHolders.MenuHomeViewHolder_ser;
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

public class Home_ser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;

    RecyclerView recycler_Menu;

    FirebaseRecyclerAdapter<Category, MenuHomeViewHolder_ser> adapter;

    Category newcategory;

    Uri saveuri;
    private final int pickImageRequest = 71;

    TextView fullname;
    Button select, upload;
    EditText edtName;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_ser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu Management");


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        fullname = (TextView) headerView.findViewById(R.id.textView);
        fullname.setText(Common.currentUser.getName());


        recycler_Menu = (RecyclerView) findViewById(R.id.recycler);
        recycler_Menu.setHasFixedSize(true);
        recycler_Menu.setLayoutManager(new LinearLayoutManager(this));

        loadMenu();
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Category, MenuHomeViewHolder_ser>(Category.class, R.layout.item_menu_ser, MenuHomeViewHolder_ser.class, databaseReference) {
            @Override
            protected void populateViewHolder(MenuHomeViewHolder_ser viewHolder, Category category, int position) {

                viewHolder.menuname.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getImage()).into(viewHolder.menuimag);

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent=new Intent(Home_ser.this,FoodList_ser.class);
                        intent.putExtra("categoryId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();  //refresh data if there is a change
        recycler_Menu.setAdapter(adapter);

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addItemDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_ser.this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_menu_ser, null);

        edtName = view.findViewById(R.id.edtname);
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
                uploadImage();
            }
        });


        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_local_grocery_store_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if (newcategory != null) {
                    databaseReference.push().setValue(newcategory);
                    Snackbar.make(drawer, "New category " + newcategory.getName() + " was Added", Snackbar.LENGTH_LONG).show();
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

    //send data

    private void uploadImage() {

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
                            Toast.makeText(Home_ser.this, "Uploaded ", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newcategory = new Category(edtName.getText().toString(), uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(Home_ser.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Upload  " + progress + "%");
                        }
                    });
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveuri = data.getData();
            select.setText("Image selected  !");  //change text in button when select an image
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void ChooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select picture"), pickImageRequest);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_order) {
            Intent intent=new Intent(Home_ser.this,OrderStatus_ser.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //update and delete in contxt menu when long click in item

    @Override
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
        Toast.makeText(this, " Item "+newcategory.getName() +" deleted", Toast.LENGTH_SHORT).show();

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //functions of update updateDialog and updateUploadImage

    private void updateDialog(final String key, final Category item) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_ser.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_menu_ser, null);

        edtName = view.findViewById(R.id.edtname);
        select = view.findViewById(R.id.btnselect);
        upload = view.findViewById(R.id.btnupload);


        //select default name
        edtName.setText(item.getName());

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
                item.setName(edtName.getText().toString());
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

    //send image with new name

    private void updateUploadImage(final Category item) {

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
                            Toast.makeText(Home_ser.this, "Uploaded ", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Home_ser.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Upload  " + progress + "%");
                        }
                    });
        }
    }
}
