package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pjbakersbuzzin.adapters.StepListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.models.Recipe;
import com.example.android.pjbakersbuzzin.models.Step;

import java.util.ArrayList;

public class RecipeDetailActivity
        extends AppCompatActivity
        implements StepListRecyclerAdapter.ListItemClickListener {

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    private Parcelable savedRecyclerLayoutState;
    private Context context;

    private ArrayList<Recipe> recipe;
    String recipeName;
    private boolean mTwoPane;

    RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
    FragmentManager rFragmentManager = getSupportFragmentManager();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        context = getApplicationContext();

        Intent recipeDetailIntent = getIntent();
        Bundle currentRecipeBundle = recipeDetailIntent.getExtras();
        recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");

        recipeDetailFragment.setArguments(currentRecipeBundle);

        // Add the master pane fragment to its container using a transaction
        rFragmentManager.beginTransaction()
                .replace(R.id.recipe_detail_container, recipeDetailFragment)
                .commit();

        // If the screen is wide enough for two-pane, start step detail fragment
        if (findViewById(R.id.divider1) != null) {
            // divider1 will only exist in the two-pane case
            mTwoPane = true;
            Log.d(TAG, "onCreate: in two pane mode");
            if (savedInstanceState == null) {

                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                FragmentManager sFragmentManager = getSupportFragmentManager();

                // Setup bundle for fragment argument
                stepDetailFragment.setArguments(currentRecipeBundle);

                // Add the fragment to its container using a transaction
                sFragmentManager.beginTransaction()
                        .replace(R.id.step_detail_container, stepDetailFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onListItemClick(ArrayList<Step> steps,int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: steps size " + steps.size());
        Log.d(TAG, "onListItemClick: clickedItemIndex " + clickedItemIndex);
        Toast.makeText(context, "clickedItemIndex: " + clickedItemIndex, Toast.LENGTH_SHORT).show();

        Bundle specificStepBundle = new Bundle();

        // Setup bundle to pass to StepDetailActivity or StepDetailActivityFragment
        specificStepBundle.putParcelableArrayList("Steps_Bundle", steps);
        specificStepBundle.putInt("Step_Index", clickedItemIndex);
        specificStepBundle.putString("Title", recipeName);

        if (mTwoPane) {
            // Add bundle as fragment argument, then start fragment in container
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            FragmentManager sFragmentManager = getSupportFragmentManager();
            stepDetailFragment.setArguments(specificStepBundle);
            sFragmentManager.beginTransaction()
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
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
        currentState.putParcelable("recycler_layout", savedRecyclerLayoutState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            savedRecyclerLayoutState = savedInstanceState.getParcelable("recycler_layout");
        }
    }

}
