package com.example.cookbook.model;

public class FoodRecipeModel {
    public int recipeId;
    public String recipeName;
    public String category;
    public String recipeImage;
    public String recipeInstructions;
    public FoodRecipeModel(int recipeId, String recipeName, String category,
                           String recipeImage, String recipeInstructions){
        this.recipeId = recipeId;
        this.recipeName= recipeName;
        this.category = category;
        this.recipeImage = recipeImage;
        this.recipeInstructions = recipeInstructions;
    }
}
