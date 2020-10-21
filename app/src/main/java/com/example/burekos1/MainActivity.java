package com.example.burekos1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView_helloUser)
    TextView textView_helloUser;
    @BindView(R.id.recyclerView_main)
    RecyclerView recyclerView_main;
    @BindView(R.id.toolbarMain)
    Toolbar toolbarMain;
    @BindView(R.id.bottom_sheet)
    View bottom_sheet;
    @BindView(R.id.recyclerView_bakeries)
    RecyclerView recyclerView_bakeries;

    private BottomSheetBehavior bottomSheetBehavior;


    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private Query query, query2;

    MainAdapter adapter;
    MainAdapterClickListener mainAdapterClickListener = new MainAdapterClickListener() {
        @Override
        public void onClick(CityBlock cityBlock) {
            showBakeries(cityBlock);
        }
    };

    SheetAdapter sheetAdapter;
    SheetAdapterClickListener sheetAdapterClickListener = new SheetAdapterClickListener() {
        @Override
        public void onClick(Bakery bakery) {
            openBakery(bakery);
        }
    };

    private List<CityBlock> cityBlockList;
    private List<Bakery> bakeryList;

    private CityBlock cityBlock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        setFirebase();
        setMainRecycler();
        setSheetRecycler();
        getCities();


        setSupportActionBar(toolbarMain);
        setToolbar();
    }


    private void setToolbar() {
        currentUser = auth.getCurrentUser();
        String userID = currentUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        query = reference.orderByChild(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getChildren();
                    textView_helloUser.setText(dataSnapshot.getValue(User.class).getUsername());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.button_logout)
    public void logoutUser() {

        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }


    private void setFirebase() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("cityBlocks");
        reference.keepSynced(true);
    }

    private void getCities() {

        query = reference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    fetchData(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setMainRecycler() {

        cityBlockList = new ArrayList<>();
        adapter = new MainAdapter(mainAdapterClickListener, cityBlockList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_main.setLayoutManager(linearLayoutManager);
        recyclerView_main.setItemAnimator(new DefaultItemAnimator());
        recyclerView_main.setAdapter(adapter);

    }

    private void setSheetRecycler() {
        bakeryList = new ArrayList<>();

        sheetAdapter = new SheetAdapter(sheetAdapterClickListener, bakeryList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_bakeries.setLayoutManager(linearLayoutManager);
        recyclerView_bakeries.setItemAnimator(new DefaultItemAnimator());
        recyclerView_bakeries.setAdapter(sheetAdapter);

    }


    private void fetchData(DataSnapshot dataSnapshot) {

        if (dataSnapshot != null) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                cityBlock = new CityBlock();
                String cityBlockName = ds.getKey();
                cityBlock.setCityBlockName(cityBlockName);
                cityBlockList.add(cityBlock);
            }
            adapter = new MainAdapter(mainAdapterClickListener, cityBlockList);
            recyclerView_main.setAdapter(adapter);
        }


    }


    private void showBakeries(CityBlock cityBlock) {

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottom_sheet.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setHideable(true);
            cityBlock.setOpened(true);
            setData(cityBlock.getCityBlockName());
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            if (!cityBlock.isOpened()) {
                bottom_sheet.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setHideable(true);
                cityBlock.setOpened(true);
                setData(cityBlock.getCityBlockName());
                cityBlock.setOpened(false);
            } else {
                cityBlock.setOpened(false);
            }
        } else {
            bottom_sheet.setVisibility(View.GONE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            cityBlock.setOpened(false);
        }


    }

    public void setData(String cityBlockName) {


        final List<Bakery> bakeryList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("cityBlocks").child(cityBlockName);
        query2 = reference;
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Bakery newBakery = new Bakery();
                        String bakeryName = ds.child("name").getValue(String.class);
                        String votesSum = ds.child("votesSum").getValue(String.class);
                        String noVotes = ds.child("noVotes").getValue(String.class);
                        String cityBlockName = ds.child("cityBlockName").getValue(String.class);
                        String longitude = ds.child("longitude").getValue(String.class);
                        String latitude = ds.child("latitude").getValue(String.class);
                        newBakery.setName(bakeryName);
                        newBakery.setVotesSum(votesSum);
                        newBakery.setNoVotes(noVotes);
                        newBakery.setCityBlockName(cityBlockName);
                        newBakery.setLatitude(latitude);
                        newBakery.setLongitude(longitude);
                        bakeryList.add(newBakery);
                    }
                    sheetAdapter = new SheetAdapter(sheetAdapterClickListener, bakeryList);
                    recyclerView_bakeries.setAdapter(sheetAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openBakery(Bakery bakery) {

        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.bakeryName, bakery.getName());
        bundle.putString(Constants.cityBlockName, bakery.getCityBlockName());
        bundle.putString(Constants.latitude, bakery.getLatitude());
        bundle.putString(Constants.longitude, bakery.getLongitude());
        bundle.putString(Constants.noVotes, bakery.getNoVotes());
        bundle.putString(Constants.votesSum, bakery.getVotesSum());
        intent.putExtras(bundle);
        startActivity(intent);

    }


}
