package com.example.cookbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cookbook.R;
import com.example.cookbook.adapter.DescriptionRecyclerAdapter;
import com.example.cookbook.database.FoodDatabase;
import com.example.cookbook.database.FoodEntity;
import com.example.cookbook.model.FoodItem;
import com.example.cookbook.model.FoodRecipeModel;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class DescriptionActivity extends AppCompatActivity {

    ImageView imgRecipe;
    TextView txtRecipeName;
    TextView txtCategory;
    ImageView btnFav;
    RecyclerView recipeActivity;
    DescriptionRecyclerAdapter descriptionRecyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    int recipeId;
    String recipeName;
    RelativeLayout progressLayout;
    ProgressBar progressBar;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    ArrayList<String> recipeStepsList;
    FoodEntity foodEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        imgRecipe = findViewById(R.id.imgRecipe);
        txtRecipeName = findViewById(R.id.txtRecipeName);
        txtCategory = findViewById(R.id.txtCategory);
        btnFav = findViewById(R.id.btnFav);
        recipeActivity = findViewById(R.id.recyclerViewRecipeActivity);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        progressLayout = findViewById(R.id.progressLayout);
        toolbar = findViewById(R.id.toolBar);
        progressBar = findViewById(R.id.progressBar);
        recipeId = getIntent().getIntExtra("recipeId", 0);
        recipeName = getIntent().getStringExtra("recipeName");
        recipeStepsList = new ArrayList<>();
        setUpToolbar(recipeName);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.themealdb.com/api/json/v1/1/lookup.php?i="+ recipeId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressLayout.setVisibility(View.INVISIBLE);
                        try{
                            JSONArray data= response.getJSONArray("meals");
                            for(int i=0;i<data.length();i++){
                                JSONObject foodJsonObject= data.getJSONObject(i);
                                FoodRecipeModel foodRecipeModel= new FoodRecipeModel(
                                        Integer.parseInt(foodJsonObject.getString("idMeal")),
                                        foodJsonObject.getString("strMeal"),
                                        foodJsonObject.getString("strCategory"),
                                        foodJsonObject.getString("strMealThumb"),
                                        foodJsonObject.getString("strInstructions")
                                );

                                foodEntity = new FoodEntity(
                                        Integer.parseInt(foodJsonObject.getString("idMeal")),
                                        foodJsonObject.getString("strMeal"),
                                        foodJsonObject.getString("strCategory"),
                                        foodJsonObject.getString("strMealThumb")
                                );
                                txtRecipeName.setText(foodRecipeModel.recipeName);
                                txtCategory.setText(foodRecipeModel.category);
                                Picasso.get().load(foodRecipeModel.recipeImage).into(imgRecipe);
                                boolean check = new DBAsyncTask(getApplicationContext(),
                                        foodEntity, 1).execute().get();
                                if(!check){
                                    btnFav.setImageResource(R.drawable.ic_not_fav_icon);
                                }else{
                                    btnFav.setImageResource(R.drawable.ic_fav_icon);
                                }

                                String[] recipeSteps= foodRecipeModel.recipeInstructions.split("\r\n");
                                recipeStepsList.addAll(Arrays.asList(recipeSteps));
                                descriptionRecyclerAdapter = new DescriptionRecyclerAdapter(recipeStepsList,
                                        getApplicationContext());
                                recipeActivity.setAdapter(descriptionRecyclerAdapter);
                                recipeActivity.setLayoutManager(layoutManager);
                                descriptionRecyclerAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception e){
                            System.out.println(e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(jsonObjectRequest);
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean check = new DBAsyncTask(getApplicationContext(), foodEntity, 1).execute().get();
                    if(!check){
                        boolean res= new DBAsyncTask(getApplicationContext(), foodEntity, 2).execute().get();
                        if(res){
                            Toast.makeText(getApplicationContext(), "Added to Favourites", Toast.LENGTH_SHORT)
                            .show();
                            btnFav.setImageResource(R.drawable.ic_fav_icon);
                        }
                    }else{
                        boolean res= new DBAsyncTask(getApplicationContext(), foodEntity, 3).execute().get();
                        if(res){
                            Toast.makeText(getApplicationContext(), "Removed from Favourites", Toast.LENGTH_SHORT)
                                    .show();
                            btnFav.setImageResource(R.drawable.ic_not_fav_icon);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id== android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setUpToolbar(String title){
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    class DBAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private final FoodEntity foodEntity;
        private final int mode;
        private final FoodDatabase db;
        public DBAsyncTask(Context context, FoodEntity foodEntity, int mode){
            this.context = context;
            this.foodEntity = foodEntity;
            this.mode = mode;
            db= Room.databaseBuilder(context, FoodDatabase.class, "food-db").build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            switch (mode){
                case 1:
                    FoodEntity foodEntity = db.foodDao().getFoodById(String.valueOf(getFoodEntity().recipeId));
                    db.close();
                    return foodEntity != null;
                case 2:
                    db.foodDao().insertFoodItem(getFoodEntity());
                    db.close();
                    return true;
                case 3:
                    db.foodDao().deleteFoodItem(getFoodEntity());
                    db.close();
                    return true;
            }
            return false;
        }

        public Context getContext() {
            return context;
        }

        public FoodEntity getFoodEntity() {
            return foodEntity;
        }
    }
}