package com.example.cookbook.model;

public class FoodItem {
    public int recipeId;
    public String recipeName;
    public String category;
    public String recipeImage;
    public FoodItem(int recipeId, String recipeName, String category, String recipeImage){
        this.recipeId= recipeId;
        this.recipeName= recipeName;
        this.category= category;
        this.recipeImage= recipeImage;
    }
}
