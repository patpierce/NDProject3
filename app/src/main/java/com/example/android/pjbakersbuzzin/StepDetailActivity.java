package com.example.android.pjbakersbuzzin;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.pjbakersbuzzin.models.Recipe;
import com.google.android.exoplayer2.ui.PlayerView;

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
    private int clickedItemIndex;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        context = getApplicationContext();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent StepDetailIntent = getIntent();
        currentRecipeBundle = StepDetailIntent.getExtras();

        if (currentRecipeBundle != null) {
            if (currentRecipeBundle.containsKey("Current_Recipe")) {
                recipe = currentRecipeBundle.getParcelableArrayList("Current_Recipe");
                String recipeName = recipe.get(0).getName();
                getSupportActionBar().setTitle(recipeName);
            }
        }

        if (savedInstanceState != null) {
            clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
        } else {
            clickedItemIndex = 0;
        }
        currentRecipeBundle.putInt("Step_Index", clickedItemIndex);

        Log.d(TAG, "sda.onCreate: recipeName " + recipeName);

        StepDetailFragment stepDetailFragment = new StepDetailFragment();
        stepDetailFragment.setArguments(currentRecipeBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Add the fragment to its container using a transaction
        fragmentManager.beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment)
                .commit();
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
    public void onButtonClick(Integer targetStepIndex) {
        // always reference specific Step by "clickedItemIndex", it is automatically generated (0-n)
        //  and not by the "stepId", which is supplied by API and may skip integers

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
        currentState.putInt("Saved_Step_Index", clickedItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList("Current_Recipe");
            clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        PlayerView exoPlayerView = findViewById(R.id.exo_player_view);

        if (exoPlayerView.getVisibility() == View.VISIBLE) {

            TextView stepTitleView = findViewById(R.id.tv_step_short_description);
            TextView stepInstructionsView = findViewById(R.id.tv_step_description);
            LinearLayout buttonsRowLayout = findViewById(R.id.ll_buttons_row);
            View decorView = getWindow().getDecorView();
            LinearLayout.LayoutParams lLparams =
                    (LinearLayout.LayoutParams) exoPlayerView.getLayoutParams();

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // hide other views
                stepTitleView.setVisibility(View.GONE);
                stepInstructionsView.setVisibility(View.GONE);
                buttonsRowLayout.setVisibility(View.GONE);
                // hide app top toolbar
                if(getSupportActionBar()!=null) { getSupportActionBar().hide(); }
                // hide the home/back/overview buttons (these come back when you touch anywhere)
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                // hide the top android status bar
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

                // set video window match_parent w and h
                lLparams.width = lLparams.MATCH_PARENT;
                lLparams.height = lLparams.MATCH_PARENT;
                exoPlayerView.setLayoutParams(lLparams);
            }
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // set video window back to using "layout_weight" for height
                lLparams.width = lLparams.MATCH_PARENT;
                lLparams.height = 0;
                exoPlayerView.setLayoutParams(lLparams);

                // unhide top android status bar
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                if(getSupportActionBar()!=null) { getSupportActionBar().show(); }
                stepTitleView.setVisibility(View.VISIBLE);
                stepInstructionsView.setVisibility(View.VISIBLE);
                buttonsRowLayout.setVisibility(View.VISIBLE);
            }

        }

    }

}
