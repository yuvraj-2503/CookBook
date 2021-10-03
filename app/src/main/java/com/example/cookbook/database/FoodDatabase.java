package com.example.cookbook.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FoodEntity.class}, version = 1)
public abstract class FoodDatabase extends RoomDatabase {
    public abstract FoodDao foodDao();
}
