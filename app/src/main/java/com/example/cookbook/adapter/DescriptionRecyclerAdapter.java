package com.example.cookbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.R;

import java.util.ArrayList;

public class DescriptionRecyclerAdapter extends RecyclerView.Adapter<DescriptionRecyclerAdapter.ViewHolder> {
    private final ArrayList<String> recipeSteps;
    private final Context context;
    public DescriptionRecyclerAdapter(ArrayList<String> recipeSteps, Context context){
        this.recipeSteps = recipeSteps;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_description_single_row, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtRecipeStepNo.setText(String.valueOf(position+1));
        holder.txtRecipeStep.setText(recipeSteps.get(position));
    }

    @Override
    public int getItemCount() {
        return recipeSteps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout recipeItem;
        private final TextView txtRecipeStepNo;
        private final TextView txtRecipeStep;
        public  ViewHolder(View view){
            super(view);
            recipeItem = view.findViewById(R.id.recipeItem);
            txtRecipeStepNo = view.findViewById(R.id.recipeStepNo);
            txtRecipeStep = view.findViewById(R.id.recipeStep);
        }
    }
}
