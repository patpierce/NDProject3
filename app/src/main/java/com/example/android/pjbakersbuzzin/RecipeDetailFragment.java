package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.pjbakersbuzzin.adapters.IngredientListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.adapters.StepListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.models.Ingredient;
import com.example.android.pjbakersbuzzin.models.Recipe;
import com.example.android.pjbakersbuzzin.models.Step;
import com.example.android.pjbakersbuzzin.widget.UpdateBakingWidgetService;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    //private static final String TAG = RecipeDetailFragment.class.getSimpleName();

    // Strings to store information about the recipe
    private ArrayList<Recipe> recipe;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View recipeDetailPaneView = inflater.inflate(R.layout.fragment_recipe_detail, viewGroup, false);

        ScrollView sv = (ScrollView) recipeDetailPaneView.findViewById(R.id.sv_recipe_detail);
        TextView mDetailRecipeNameView = (TextView) recipeDetailPaneView.findViewById(R.id.tv_recipe_name);
        TextView mDetailServingsView = (TextView) recipeDetailPaneView.findViewById(R.id.tv_detail_num_servings);
        ImageView mDetailImageView = (ImageView) recipeDetailPaneView.findViewById(R.id.iv_recipe_detail_image);
        Context context = mDetailImageView.getContext();

        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
        } else {
            sv.smoothScrollTo(0, 0);
            recipe = getArguments().getParcelableArrayList("Current_Recipe");
        }
        if (recipe == null) {
            Toast.makeText(getActivity(),
                    R.string.bundle_missing_error, Toast.LENGTH_SHORT).show();
            return null;
        }

        // Parse the recipe info from the bundle from MainListActivity
        // Integer mRecipeId = recipe.get(0).getId();
        String mRecipeName = recipe.get(0).getName();
        List<Ingredient> mIngredients = recipe.get(0).getIngredients();
        ArrayList<Step> mSteps = recipe.get(0).getSteps();
        Integer mServings = recipe.get(0).getServings();
        String mImage = recipe.get(0).getImage();

        // Display the recipe info into various views
        Glide.with(context).load(mImage).placeholder(R.drawable.vg_small_oven).into(mDetailImageView);
        mDetailRecipeNameView.setText(mRecipeName);
        String numServings = "  " + getString(R.string.serves_header) + ": " + mServings;
        mDetailServingsView.setText(numServings);

        // Setup RecyclerView for Ingredients
        RecyclerView ingredientsListRecView = (RecyclerView) recipeDetailPaneView.findViewById(R.id.rv_ingredients);
        LinearLayoutManager iLayoutManager = new LinearLayoutManager(context);
        IngredientListRecyclerAdapter ingredientsAdapter =
                new IngredientListRecyclerAdapter();
        ingredientsListRecView.setLayoutManager(iLayoutManager);
        ingredientsListRecView.setHasFixedSize(true);
        ingredientsListRecView.setAdapter(ingredientsAdapter);
        ingredientsAdapter.setIngredientData(mIngredients);

        // Setup RecyclerView for Steps with clickListener
        RecyclerView stepsListRecView = (RecyclerView) recipeDetailPaneView.findViewById(R.id.rv_steps);
        GridLayoutManager sLayoutManager = new GridLayoutManager(context, 1);
        stepsListRecView.setLayoutManager(sLayoutManager);
        StepListRecyclerAdapter stepsAdapter =
                new StepListRecyclerAdapter((RecipeDetailActivity) getActivity());
        stepsListRecView.setHasFixedSize(true);
        stepsListRecView.setAdapter(stepsAdapter);
        stepsAdapter.setStepData(mSteps, context);

        ArrayList<String> ingredientsForWidgets = new ArrayList<>();
        ingredientsForWidgets.add(" " +
                getResources().getString(R.string.app_name) + "\n" + mRecipeName +
                " " + getString(R.string.ingredients_header) + ":");
        for (Ingredient a : mIngredients) {
            ingredientsForWidgets.add(" " +
                    a.getQuantity().toString() + " " +
                    a.getMeasure() + " " +
                    a.getIngredient()
            );
        }

        // This widget code does not work with "targetSdkVersion 27" in build.gradle
        //   changed it to "targetSdkVersion 25" and it works
        UpdateBakingWidgetService.startBakingService(getContext(), ingredientsForWidgets);

        return recipeDetailPaneView;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
    }

}
