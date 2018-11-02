package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    private static final String TAG = StepDetailActivity.class.getSimpleName();
    Bundle currentRecipeBundle;
    private ArrayList<Recipe> recipe;

    private String recipeName;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        context = getApplicationContext();
        Intent StepDetailIntent = getIntent();

        currentRecipeBundle = StepDetailIntent.getExtras();
        recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");
        String recipeName = recipe.get(0).getName();

        Log.d(TAG, "sda.onCreate: recipeName " + recipeName);
        getSupportActionBar().setTitle(recipeName);

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(currentRecipeBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Add the fragment to its container using a transaction
        fragmentManager.beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment)
                .commit();
    }

    @Override
    public void onButtonClick(Integer targetStepIndex) {
        currentRecipeBundle.putInt("Step_Index", targetStepIndex);

        // Add bundle as fragment argument, then start fragment in container
        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        FragmentManager sFragmentManager = getSupportFragmentManager();
        stepDetailFragment.setArguments(currentRecipeBundle);
        sFragmentManager.beginTransaction()
                .replace(R.id.step_detail_container, stepDetailFragment)
                .commit();

    }

}
