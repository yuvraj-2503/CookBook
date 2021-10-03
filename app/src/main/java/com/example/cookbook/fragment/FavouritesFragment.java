package com.example.cookbook.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.cookbook.R;
import com.example.cookbook.adapter.FavouritesRecyclerAdapter;
import com.example.cookbook.database.FoodDatabase;
import com.example.cookbook.database.FoodEntity;
import com.example.cookbook.model.FoodItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FavouritesFragment extends Fragment {
    RecyclerView recyclerViewFavourites;
    RelativeLayout progressLayout;
    ProgressBar progressBar;
    FavouritesRecyclerAdapter favouritesRecyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<FoodEntity> foodItemArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_favourites, container, false);
        recyclerViewFavourites = view.findViewById(R.id.recyclerViewFavourites);
        progressLayout= view.findViewById(R.id.progressLayout);
        progressBar = view.findViewById(R.id.progressBar);
        layoutManager = new LinearLayoutManager(getActivity());
        try {
            foodItemArrayList = new DBAsyncTask(getContext()).execute().get();
            progressLayout.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        favouritesRecyclerAdapter = new FavouritesRecyclerAdapter(getContext(), foodItemArrayList);
        recyclerViewFavourites.setAdapter(favouritesRecyclerAdapter);
        recyclerViewFavourites.setLayoutManager(layoutManager);
        favouritesRecyclerAdapter.notifyDataSetChanged();
        return view;
    }

    class DBAsyncTask extends AsyncTask<Void, Void, List<FoodEntity>> {
        private final FoodDatabase db;
        public DBAsyncTask(Context context){
            db= Room.databaseBuilder(context, FoodDatabase.class, "food-db").build();
        }

        @Override
        protected List<FoodEntity> doInBackground(Void... voids) {
            List<FoodEntity> favouriteFoodsList = db.foodDao().getAllFoodItems();
            db.close();
            return favouriteFoodsList;
        }
    }
}