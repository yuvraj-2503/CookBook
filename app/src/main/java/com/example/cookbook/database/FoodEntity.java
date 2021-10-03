package com.example.cookbook.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "foodItems")
public class FoodEntity {
    @PrimaryKey @ColumnInfo(name = "recipe_id") public int recipeId;
    @ColumnInfo(name = "recipe_name") public String recipeName;
    @ColumnInfo(name = "category") public String category;
    @ColumnInfo(name = "recipe_image") public String recipeImage;
    public FoodEntity(int recipeId, String recipeName, String category, String recipeImage){
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.category = category;
        this.recipeImage = recipeImage;
    }
}
