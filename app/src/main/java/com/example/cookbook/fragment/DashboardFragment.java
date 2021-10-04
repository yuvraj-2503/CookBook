package com.example.cookbook.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cookbook.R;
import com.example.cookbook.activity.MainActivity;
import com.example.cookbook.adapter.DashboardRecyclerAdapter;
import com.example.cookbook.model.FoodItem;
import com.example.cookbook.util.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    RecyclerView recyclerViewDashboard;
    RelativeLayout progressLayout;
    ProgressBar progressBar;
    DashboardRecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<FoodItem> foodItemArrayList;
    EditText searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerViewDashboard = view.findViewById(R.id.recyclerViewHomePage);
        progressLayout= view.findViewById(R.id.progressLayout);
        progressBar = view.findViewById(R.id.progressBar);
        layoutManager = new LinearLayoutManager(getActivity());
        foodItemArrayList = new ArrayList<>();
        searchText= view.findViewById(R.id.searchText);
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        int rand= (int) (Math.random() * 26);
        char c= (char) ('a' + rand);
        String url = "https://www.themealdb.com/api/json/v1/1/search.php?f="+ c;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressLayout.setVisibility(View.INVISIBLE);
                        try {
                            JSONArray data = response.getJSONArray("meals");
                            for(int i=0;i< data.length();i++){
                                JSONObject foodJsonObject= data.getJSONObject(i);
                                FoodItem foodObject= new FoodItem(
                                        Integer.parseInt(foodJsonObject.getString("idMeal")),
                                        foodJsonObject.getString("strMeal"),
                                        foodJsonObject.getString("strCategory"),
                                        foodJsonObject.getString("strMealThumb")
                                );
                                foodItemArrayList.add(foodObject);
                                recyclerAdapter = new DashboardRecyclerAdapter(requireActivity(),
                                        foodItemArrayList);
                                recyclerViewDashboard.setAdapter(recyclerAdapter);
                                recyclerViewDashboard.setLayoutManager(layoutManager);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void filter(String text){
        ArrayList<FoodItem> filteredList = new ArrayList<>();
        for(FoodItem item : foodItemArrayList){
            if(item.recipeName.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        recyclerAdapter.filterList(filteredList);
    }
}