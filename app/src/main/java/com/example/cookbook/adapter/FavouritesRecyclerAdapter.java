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
import androidx.room.Room;

import com.example.cookbook.R;
import com.example.cookbook.activity.DescriptionActivity;
import com.example.cookbook.database.FoodDatabase;
import com.example.cookbook.database.FoodEntity;
import com.example.cookbook.model.FoodItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FavouritesRecyclerAdapter extends RecyclerView.Adapter<FavouritesRecyclerAdapter.ViewHolder> {
    private final List<FoodEntity> itemArrayList;
    private final Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_favorites_single_row,parent
                ,false);
        return new FavouritesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodEntity food= itemArrayList.get(position);
        Picasso.get().load(food.recipeImage).into(holder.imgRecipe);
        holder.txtRecipeName.setText(food.recipeName);
        holder.txtCategory.setText(food.category);
        FoodEntity foodEntity = new FoodEntity(
                food.recipeId,
                food.recipeName,
                food.category,
                food.recipeImage
        );
        holder.btnFav.setImageResource(R.drawable.ic_fav_icon);
        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean res= new DBAsyncTask(context, foodEntity, 3).execute().get();
                    if(res){
                        Toast.makeText(context,"Recipe removed from Favourites",Toast.LENGTH_SHORT).show();
                        holder.btnFav.setImageResource(R.drawable.ic_not_fav_icon);
                    }else{
                        Toast.makeText(context,"Already Removed from Favourites",Toast.LENGTH_SHORT).show();
                        holder.btnFav.setImageResource(R.drawable.ic_not_fav_icon);
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
                intent.putExtra("recipeId",food.recipeId);
                intent.putExtra("recipeName", food.recipeName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

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

    public FavouritesRecyclerAdapter(Context context, List<FoodEntity> itemsList){
        this.itemArrayList = itemsList;
        this.context = context;
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
