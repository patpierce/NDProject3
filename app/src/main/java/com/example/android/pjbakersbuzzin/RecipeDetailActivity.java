package com.example.android.pjbakersbuzzin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.pjbakersbuzzin.adapters.StepListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.models.Recipe;

import java.util.ArrayList;

/**
 * An activity representing a recipe detail screen, possibly with two panes.
 * This activity is used  to load a master fragment recipe_detail_fragment
 * The master fragment shows recipe details plus a step list recycler.
 * On narrow width devices, only the master fragment is loaded,
 * step_list_recycler is used to launch a step_detail_activity to host the step_detail_fragment.
 * On tablet-size devices, this activity presents that master fragment side-by-side with
 * the step_detail_fragment which is controlled by the recycler in the master fragment.
 */
public class RecipeDetailActivity
        extends AppCompatActivity
        implements StepListRecyclerAdapter.ListItemClickListener,
        StepDetailFragment.ButtonClickListener {

    //private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    private Bundle currentRecipeBundle;
    private ArrayList<Recipe> recipe;
    private int clickedItemIndex = 0;
    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            currentRecipeBundle = getIntent().getExtras();
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();

            if (currentRecipeBundle != null && currentRecipeBundle.containsKey("Current_Recipe")) {
                recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");
            }

            // Going to pass that bundle from the MainListActivity right to recipeDetailFragment
            //   then add that master pane fragment to its container
            recipeDetailFragment.setArguments(currentRecipeBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, recipeDetailFragment,"rtag")
                    .commit();

            currentRecipeBundle.putInt("Step_Index", clickedItemIndex);

            // If the screen is wide enough for two-pane, start step detail fragment
            if (findViewById(R.id.divider1) != null) {
                // divider1 will only exist in the two-pane case
                mTwoPane = true;

                // Add bundle as fragment argument, then start fragment in container
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                stepDetailFragment.setArguments(currentRecipeBundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, stepDetailFragment,"stag")
                        .commit();
            }
        } else {
            currentRecipeBundle = savedInstanceState.getBundle("Recipe_Bundle");
            clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            RecipeDetailFragment recipeDetailFragment =
                    (RecipeDetailFragment) getSupportFragmentManager().findFragmentByTag("rtag");
            if (findViewById(R.id.divider1) != null) {
                // divider1 will only exist in the two-pane case
                mTwoPane = true;
                StepDetailFragment stepDetailFragment =
                        (StepDetailFragment) getSupportFragmentManager().findFragmentByTag("stag");
            }
        }

        String recipeName = recipe.get(0).getName();
        getSupportActionBar().setTitle(recipeName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // always reference specific Step by the "clickedItemIndex" which is automatically generated (0-n)
        //  and not the "stepId", which is supplied by API and may skip integers

        // Setup bundle to pass to StepDetailActivity or StepDetailFragment
        currentRecipeBundle.putInt("Step_Index", clickedItemIndex);

        if (mTwoPane) {
            // Add bundle as fragment argument, then start fragment in container
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(currentRecipeBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        } else {
            // Add bundle as intent extra, then start Activity with the intent
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtras(currentRecipeBundle);
            startActivity(intent);
        }
    }

    /**
     * This click listener is for the buttons inside the StepDetailFragment.
     * In twopane mode this activity is the activity that starts StepDetailFragment.
     * With smaller screens, StepDetailFragment will be invoked
     * by StepDetailActivity which will handle the click listener.
     * There is another copy of onButtonClick in that activity.
     * @param targetStepIndex Item Index of Step to open
     */
    @Override
    public void onButtonClick(int targetStepIndex) {
        // always reference specific Step by the "clickedItemIndex" which is automatically generated (0-n)
        //  and not the "stepId", which is supplied by API and may skip integers

        clickedItemIndex = targetStepIndex;
        // targetStepIndex is coming from the callback from StepDetailFragment nav buttons
        currentRecipeBundle.putInt("Step_Index", targetStepIndex);

        // Add bundle as fragment argument, then start fragment in container
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        FragmentManager sFragmentManager = getSupportFragmentManager();
        stepDetailFragment.setArguments(currentRecipeBundle);
        sFragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
        currentState.putBundle("Recipe_Bundle", currentRecipeBundle);
        currentState.putInt("Step_Index", clickedItemIndex);
    }

}
