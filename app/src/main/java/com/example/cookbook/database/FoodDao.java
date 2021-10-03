package com.example.cookbook.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FoodDao {
    @Insert
    void insertFoodItem(FoodEntity foodEntity);

    @Delete
    void deleteFoodItem(FoodEntity foodEntity);

    @Query("SELECT * FROM foodItems")
    List<FoodEntity> getAllFoodItems();

    @Query("SELECT * FROM foodItems WHERE recipe_id = :recipeId")
    FoodEntity getFoodById(String recipeId);

}
