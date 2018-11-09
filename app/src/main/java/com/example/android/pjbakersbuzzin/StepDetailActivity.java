package com.example.android.pjbakersbuzzin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.pjbakersbuzzin.models.Recipe;

import java.util.ArrayList;

/**
 * An activity representing a single step detail screen.
 * This activity is only used on narrow width devices to load a fragment.
 * On tablet-size devices, {@link RecipeDetailActivity} presents that fragment
 * side-by-side with the list of steps and the other "recipe details".
 */
public class StepDetailActivity extends AppCompatActivity
        implements StepDetailFragment.ButtonClickListener {

    //private static final String TAG = StepDetailActivity.class.getSimpleName();
    private Bundle currentRecipeBundle;
    private ArrayList<Recipe> recipe;
    private Integer clickedItemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            currentRecipeBundle = getIntent().getExtras();
            if (currentRecipeBundle != null) {
                if (currentRecipeBundle.containsKey("Step_Index")) {
                    clickedItemIndex = currentRecipeBundle.getInt("Step_Index");
                }
                if (currentRecipeBundle.containsKey("Current_Recipe")) {
                    recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");
                }
            }

            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(currentRecipeBundle);
            // Add the fragment to its container using a transaction
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.step_detail_container, stepDetailFragment, "stag").
                    commit();
        } else {
            clickedItemIndex = savedInstanceState.getInt("Step_Index");
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            currentRecipeBundle = new Bundle();
            currentRecipeBundle.putParcelableArrayList("Current_Recipe", recipe);
            currentRecipeBundle.putInt("Step_Index", clickedItemIndex);
            StepDetailFragment stepDetailFragment =
                    (StepDetailFragment) getSupportFragmentManager().findFragmentByTag("stag");
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

    /**
     * This click listener is for the buttons inside the StepDetailFragment.
     * With smaller screens, StepDetailFragment will be invoked
     * by this activity which will handle the click listener
     * In twopane mode RecipeDetailActivity is the activity that starts StepDetailFragment.
     * There is another copy of onButtonClick in that activity.
     * @param targetStepIndex Item Index of Step to open
     */
    @Override
    public void onButtonClick(int targetStepIndex) {
        // always reference specific Step by "clickedItemIndex", it is automatically generated (0-n)
        //  and not by the "stepId", which is supplied by API and may skip integers

        clickedItemIndex = targetStepIndex;
        // targetStepIndex is coming from the callback from StepDetailFragment nav buttons
        currentRecipeBundle.putInt("Step_Index", targetStepIndex);

        // Add bundle as fragment argument, then start fragment in container
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(currentRecipeBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment, "stag")
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Current_Recipe", recipe);
        currentState.putInt("Step_Index", clickedItemIndex);
    }

}
