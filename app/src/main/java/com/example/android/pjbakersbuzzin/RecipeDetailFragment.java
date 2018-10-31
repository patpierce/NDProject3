package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.pjbakersbuzzin.adapters.IngredientListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.adapters.StepListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.models.Ingredient;
import com.example.android.pjbakersbuzzin.models.Recipe;
import com.example.android.pjbakersbuzzin.models.Step;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private static final String TAG = RecipeDetailFragment.class.getSimpleName();

    // Strings to store information about the recipe
    private ArrayList<Recipe> recipe;
    private Integer mRecipeId;
    private String mRecipeName;
    private List<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;
    private Integer mServings;
    private String mImage;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View recipeDetailPaneView = inflater.inflate(R.layout.fragment_recipe_detail, viewGroup, false);

        ScrollView sv = recipeDetailPaneView.findViewById(R.id.sv_recipe_detail);
        TextView mDetailRecipeNameView = recipeDetailPaneView.findViewById(R.id.tv_recipe_name);
        TextView mDetailServingsView = recipeDetailPaneView.findViewById(R.id.tv_detail_num_servings);
        ImageView mDetailImageView = recipeDetailPaneView.findViewById(R.id.iv_recipe_detail_image);
        Context context = mDetailImageView.getContext();

        Log.d(TAG, "onCreateView: begin");
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: found savedInstanceState");
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
        } else {
            sv.smoothScrollTo(0, 0);
            Log.d(TAG, "onCreateView: savedInstanceState no");
            recipe =  getArguments().getParcelableArrayList("Current_Recipe");
        }
        if (recipe == null) {
            Log.d(TAG, "onCreate: no recipe bundle found");
        }

        // Parse the recipe info from the bundle from MainListActivity
        mRecipeId = recipe.get(0).getId();
        mRecipeName = recipe.get(0).getName();
        mIngredients = recipe.get(0).getIngredients();
        mSteps = recipe.get(0).getSteps();
        mServings = recipe.get(0).getServings();
        mImage = recipe.get(0).getImage();
        Log.d(TAG, "onCreate: mRecipeName " + mRecipeName);

        // Display the recipe info into various views
        Glide.with(context).load(mImage).placeholder(R.drawable.vg_small_oven).into(mDetailImageView);
        mDetailRecipeNameView.setText(mRecipeName);
        String numServings = "  Serves: " + mServings;
        mDetailServingsView.setText(numServings);

        // Setup RecyclerView for Ingredients
        RecyclerView ingredientsListRecView = recipeDetailPaneView.findViewById(R.id.rv_ingredients);
        LinearLayoutManager ilayoutManager = new LinearLayoutManager(context);
        IngredientListRecyclerAdapter ingredientsAdapter =
                new IngredientListRecyclerAdapter(null);
        ingredientsListRecView.setLayoutManager(ilayoutManager);
        ingredientsListRecView.setHasFixedSize(true);
        ingredientsListRecView.setAdapter(ingredientsAdapter);
        ingredientsAdapter.setIngredientData(mIngredients, context);

        // Setup RecyclerView for Steps wiht clicklistener
        RecyclerView stepsListRecView = recipeDetailPaneView.findViewById(R.id.rv_steps);
        GridLayoutManager sLayoutManager = new GridLayoutManager(context, 1);
        stepsListRecView.setLayoutManager(sLayoutManager);
        StepListRecyclerAdapter stepsAdapter =
                new StepListRecyclerAdapter((RecipeDetailActivity)getActivity());
        stepsListRecView.setHasFixedSize(true);
        stepsListRecView.setAdapter(stepsAdapter);
        stepsAdapter.setStepData(mSteps, context);

        return recipeDetailPaneView;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
    }

}
