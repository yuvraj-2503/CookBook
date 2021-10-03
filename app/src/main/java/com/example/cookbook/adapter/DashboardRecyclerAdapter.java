package com.example.cookbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;

import com.example.cookbook.R;
import com.example.cookbook.activity.DescriptionActivity;
import com.example.cookbook.database.FoodDatabase;
import com.example.cookbook.database.FoodEntity;
import com.example.cookbook.model.FoodItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.ViewHolder>{
    private final ArrayList<FoodItem> itemArrayList;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout foodInfo;
        private final ImageView imgRecipe;
        private final TextView txtRecipeName;
        private final TextView txtCategory;
        private final ImageView btnFav;
        public ViewHolder(View view){
            super(view);
            foodInfo= view.findViewById(R.id.foodInfo);
            imgRecipe= view.findViewById(R.id.imgRecipe);
            txtRecipeName = view.findViewById(R.id.txtRecipeName);
            txtCategory = view.findViewById(R.id.txtCategory);
            btnFav= view.findViewById(R.id.btnFav);
        }
    }

    public DashboardRecyclerAdapter(Context context, ArrayList<FoodItem> itemsList){
        this.itemArrayList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_home_page_single_row,parent
                ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardRecyclerAdapter.ViewHolder holder, int position) {
        FoodItem foodItem= itemArrayList.get(position);
        Picasso.get().load(foodItem.recipeImage).into(holder.imgRecipe);
        holder.txtRecipeName.setText(foodItem.recipeName);
        holder.txtCategory.setText(foodItem.category);
        FoodEntity foodEntity = new FoodEntity(
                foodItem.recipeId,
                foodItem.recipeName,
                foodItem.category,
                foodItem.recipeImage
        );

        try {
            boolean check = new DBAsyncTask(context, foodEntity, 1).execute().get();
            if(!check){
                holder.btnFav.setImageResource(R.drawable.ic_not_fav_icon);
            }else
                holder.btnFav.setImageResource(R.drawable.ic_fav_icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean checkFav = new DBAsyncTask(context, foodEntity, 1).execute().get();
                    if(!checkFav){
                        boolean res= new DBAsyncTask(context, foodEntity, 2).execute().get();
                        if(res){
                            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_LONG).show();
                            holder.btnFav.setImageResource(R.drawable.ic_fav_icon);
                        }
                    }else{
                        boolean res= new DBAsyncTask(context, foodEntity, 3).execute().get();
                        if(res){
                            Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_LONG).show();
                            holder.btnFav.setImageResource(R.drawable.ic_not_fav_icon);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.foodInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DescriptionActivity.class);
                intent.putExtra("recipeId",foodItem.recipeId);
                intent.putExtra("recipeName", foodItem.recipeName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    class DBAsyncTask extends AsyncTask<Void, Void, Boolean>{
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
