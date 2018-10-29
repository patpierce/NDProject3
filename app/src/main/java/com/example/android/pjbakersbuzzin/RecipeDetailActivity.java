package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager;
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
    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent recipeDetailIntent = getIntent();

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle stepsBundle = new Bundle();

        TextView mDetailRecipeNameView = findViewById(R.id.tv_recipe_name);
        TextView mDetailServingsView = findViewById(R.id.tv_detail_num_servings);
        ImageView mDetailImageView = findViewById(R.id.iv_recipe_detail_image);
        Context context = mDetailImageView.getContext();

        final Integer mRecipeId;
        final String mRecipeName;
        final List<Ingredient> mIngredients;
        final ArrayList<Step> mSteps;
        final Integer mServings;
        final String mImage;

        if (savedInstanceState == null) {
            if (recipeDetailIntent != null) {
                if (recipeDetailIntent.hasExtra("Current_Recipe")) {
                    Bundle currentRecipeBundle = recipeDetailIntent.getExtras();

//                    assert currentRecipeBundle != null;
                    recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");
                    Log.d(TAG, "onCreate: in intent");

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
                    mIngredientsListRecView = findViewById(R.id.rv_ingredients);
                    LinearLayoutManager ilayoutManager = new LinearLayoutManager(context);
                    mIngredientsAdapter = new IngredientListRecyclerAdapter(null);
                    mIngredientsListRecView.setLayoutManager(ilayoutManager);
                    mIngredientsListRecView.setHasFixedSize(true);
                    mIngredientsListRecView.setAdapter(mIngredientsAdapter);
                    mIngredientsAdapter.setIngredientData(mIngredients, context);

                    // Setup RecyclerView for Steps
                    mStepsListRecView = findViewById(R.id.rv_steps);
                    GridLayoutManager slayoutManager = new GridLayoutManager(this, 1);
                    mStepsAdapter = new StepListRecyclerAdapter(this);
                    mStepsListRecView.setLayoutManager(slayoutManager);
                    mStepsListRecView.setHasFixedSize(true);
                    mStepsListRecView.setAdapter(mStepsAdapter);
                    mStepsAdapter.setStepData(mSteps, context);

                    // If the screen is wide enough for two-pane, start step detail fragment
                    if (findViewById(R.id.divider1) != null) {
                        // divider1 will only exist in the two-pane case
                        mTwoPane = true;
                        Log.d(TAG, "onCreate: in two pane mode");
                        if (savedInstanceState == null) {

                            // Setup bundle for fragment argument
                            stepsBundle.putParcelableArrayList("Steps_Bundle", mSteps);
                            stepDetailFragment.setArguments(stepsBundle);

                            // Add the fragment to its container using a transaction
                            fragmentManager.beginTransaction()
                                    .add(R.id.step_detail_container, stepDetailFragment)
                                    .commit();

                        }
                    }
                }
            }
        }
        else {
            Log.d(TAG, "onCreate: found savedInstanceState");
            // load stuff from savedInstanceState?
        }
    }

    @Override
    public void onListItemClick(ArrayList<Step> mSteps, int clickedItemIndex) {

        Bundle specificStepBundle = new Bundle();

        Log.d(TAG, "onListItemClick: mSteps.size " + mSteps.size());

        // Setup bundle to pass to StepDetailActivity or StepDetailActivityFragment
        specificStepBundle.putParcelableArrayList("Steps_Bundle", mSteps);
        specificStepBundle.putInt("Step_Index", clickedItemIndex);
        specificStepBundle.putString("Title", recipeName);

        if (mTwoPane) {
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();

            Log.d(TAG, "onListItemClick: in two pane mode");
            // Add bundle as fragment argument, then start fragment in container
            stepDetailFragment.setArguments(specificStepBundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }
        else {
            // Add bundle as intent extra, then start Activity with the intent
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtra("Steps_Bundle", specificStepBundle);
            intent.putExtra("Current_Recipe_Name", recipe.get(0).getName());
            startActivity(intent);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
