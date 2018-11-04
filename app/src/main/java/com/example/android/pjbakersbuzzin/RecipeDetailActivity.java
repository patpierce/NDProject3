package com.example.android.pjbakersbuzzin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    private Parcelable savedRecyclerLayoutState;

    private Bundle currentRecipeBundle;
    private ArrayList<Recipe> recipe;
    private int clickedItemIndex;
    private boolean mTwoPane;

    private final RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
    private final FragmentManager rFragmentManager = getSupportFragmentManager();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent recipeDetailIntent = getIntent();
        currentRecipeBundle = recipeDetailIntent.getExtras();

        if (currentRecipeBundle != null) {
            if (currentRecipeBundle.containsKey("Current_Recipe")) {
                recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");

                assert recipe != null;
                String recipeName = recipe.get(0).getName();
                getSupportActionBar().setTitle(recipeName);
            }
        }

        // Going to pass that bundle from the MainListActivity right to recipeDetailFragment
        recipeDetailFragment.setArguments(currentRecipeBundle);

        // Add the master pane fragment to its container using a transaction
        rFragmentManager.beginTransaction()
                .replace(R.id.recipe_detail_container, recipeDetailFragment)
                .commit();

        // If the screen is wide enough for two-pane, start step detail fragment
        if (findViewById(R.id.divider1) != null) {
            // divider1 will only exist in the two-pane case
            mTwoPane = true;
            if (savedInstanceState != null) {
                clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
                currentRecipeBundle.putInt("Step_Index", clickedItemIndex);
            }
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
            FragmentManager sFragmentManager = getSupportFragmentManager();
            stepDetailFragment.setArguments(currentRecipeBundle);
            sFragmentManager.beginTransaction()
                    .replace(R.id.step_detail_container, stepDetailFragment)
                    .commit();
        }
        else {
            // Add bundle as intent extra, then start Activity with the intent
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtras(currentRecipeBundle);
            startActivity(intent);
        }
    }

    @Override
    public void onButtonClick(Integer targetStepIndex) {
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
        currentState.putParcelable("recycler_layout", savedRecyclerLayoutState);
        currentState.putInt("Saved_Step_Index", clickedItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            savedRecyclerLayoutState = savedInstanceState.getParcelable("recycler_layout");
            clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
        }
    }

}
