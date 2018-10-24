package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.pjbakersbuzzin.adapters.IngredientListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.adapters.StepListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.models.Ingredient;
import com.example.android.pjbakersbuzzin.models.Recipe;
import com.example.android.pjbakersbuzzin.models.Step;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity
        extends AppCompatActivity
        implements StepListRecyclerAdapter.ListItemClickListener {

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    private IngredientListRecyclerAdapter mIngredientsAdapter;
    private RecyclerView mIngredientsListRecView;
    private StepListRecyclerAdapter mStepsAdapter;
    private RecyclerView mStepsListRecView;
    private Context context;

    private ArrayList<Recipe> recipe;
    String recipeName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent intentThatStartedThisActivity = getIntent();

        TextView mDetailRecipeNameView = findViewById(R.id.tv_recipe_name);
        TextView mDetailServingsView = findViewById(R.id.tv_detail_num_servings);
        ImageView mDetailImageView = findViewById(R.id.iv_recipe_detail_image);
        Context context = mDetailImageView.getContext();

        if (savedInstanceState == null) {
            final Integer mRecipeId;
            final String mRecipeName;
            final List<Ingredient> mIngredients;
            final List<Step> mSteps;
            final Integer mServings;
            final String mImage;

            Bundle selectedRecipeBundle = getIntent().getExtras();

            if (intentThatStartedThisActivity != null) {
                if (intentThatStartedThisActivity.hasExtra("Selected_Recipe")) {
                    assert selectedRecipeBundle != null;
                    recipe = selectedRecipeBundle.getParcelableArrayList("Selected_Recipe");
                    Log.d(TAG, "onCreate: in intent");

                    // Parse the recipe info that was passed from MainActivity
                    mRecipeId = recipe.get(0).getId();
                    mRecipeName = recipe.get(0).getName();
                    mIngredients = recipe.get(0).getIngredients();
                    mSteps = recipe.get(0).getSteps();
                    mServings = recipe.get(0).getServings();
                    String numServings = "  Serves: " + mServings;
                    mImage = recipe.get(0).getImage();
                    Log.d(TAG, "onCreate: mRecipeName " + mRecipeName);

                    // Display the recipe info into various views
                    Glide.with(context).load(mImage).placeholder(R.drawable.ic_small_oven).into(mDetailImageView);
                    mDetailRecipeNameView.setText(mRecipeName);
                    mDetailServingsView.setText(numServings);

                    // Setup RecyclerView for Ingredients
                    mIngredientsListRecView = findViewById(R.id.rv_ingredients);
                    LinearLayoutManager ilayoutManager = new LinearLayoutManager(context);
                    mIngredientsAdapter = new IngredientListRecyclerAdapter(null);
                    mIngredientsListRecView.setLayoutManager(ilayoutManager);
                    mIngredientsListRecView.setHasFixedSize(true);
                    mIngredientsListRecView.setAdapter(mIngredientsAdapter);
                    Log.d(TAG, "onCreate: mIngredients" + mIngredients);
                    mIngredientsAdapter.setIngredientData(mIngredients, context);

                    // Setup RecyclerView for Steps
                    mStepsListRecView = findViewById(R.id.rv_steps);
                    GridLayoutManager slayoutManager = new GridLayoutManager(this, 1);
                    mStepsAdapter = new StepListRecyclerAdapter(this);
                    mStepsListRecView.setLayoutManager(slayoutManager);
                    mStepsListRecView.setHasFixedSize(true);
                    mStepsListRecView.setAdapter(mStepsAdapter);
                    mStepsAdapter.setStepData(mSteps, context);
                }
            }
        }
    }

    @Override
    public void onListItemClick(Step clickedItemIndex) {
        Bundle specificStepBundle = new Bundle();
        ArrayList<Step> selectedStep = new ArrayList<>();
        selectedStep.add(clickedItemIndex);
        specificStepBundle.putParcelableArrayList("Selected_Step",selectedStep);

        final Intent intent = new Intent(this, StepDetailActivity.class);
        intent.putExtras(specificStepBundle);
        startActivity(intent);
    }

}
