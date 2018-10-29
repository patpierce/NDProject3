package com.example.android.pjbakersbuzzin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.pjbakersbuzzin.models.Step;

import java.util.ArrayList;

/**
 * An activity representing a single step detail screen.
 * This activity is only used on narrow width devices to load a fragment.
 * On tablet-size devices, {@link RecipeDetailActivity} presents that fragment
 * side-by-side with the list of steps and the other "recipe details".
 */
public class StepDetailActivity extends AppCompatActivity {

//    private static final String TAG = StepDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Bundle specificStepBundle = getIntent().getBundleExtra("Steps_Bundle");

        String recipeName = getIntent().getStringExtra("Current_Recipe_Name");
        getSupportActionBar().setTitle(recipeName);

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(specificStepBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Add the fragment to its container using a transaction
        fragmentManager.beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment)
                .commit();
    }

}
