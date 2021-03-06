package com.example.android.pjbakersbuzzin;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.pjbakersbuzzin.adapters.MainListRecyclerAdapter;
import com.example.android.pjbakersbuzzin.ir4test.SimpleIdlingResource;
import com.example.android.pjbakersbuzzin.models.Recipe;
import com.example.android.pjbakersbuzzin.network.GetDataService;
import com.example.android.pjbakersbuzzin.network.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainListActivity
        extends AppCompatActivity
        implements MainListRecyclerAdapter.ListItemClickListener {

    //private static final String TAG = MainListActivity.class.getSimpleName();

    //private MainListRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ArrayList<Recipe> recipes;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    private void setIdlingResource(Boolean in){
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(in);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        // Get the IdlingResource instance
        getIdlingResource();

        // "progress bar" circle
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // Layout to display error if recipes cant be retrieved, hidden if no errors
        mErrorMessageDisplay = (LinearLayout) findViewById(R.id.ll_error_message_display);
        Button mRetryButton = (Button) findViewById(R.id.retry_button);
        mRetryButton.setOnClickListener(
                new OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }
        );

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        float dens = resources.getDisplayMetrics().density;
        // Note, screenHeightDp isn't reliable
        // (it seems to be too small by the height of the status bar),
        // but we assume screenWidthDp is reliable.
        int screenWidthDp = config.screenWidthDp;
        //Log.d(TAG, "screenWidthDp: " + screenWidthDp);
        int screenWidthPx = (int) (screenWidthDp * dens);
        //Log.d(TAG, "screenWidthPx: " + screenWidthPx);
        int itemWidthPx = (int) (resources.getDimension(R.dimen.recipe_card_width));
        int numberOfColumns = (screenWidthPx / itemWidthPx);
        //Log.d(TAG, "itemWidthPx: " + itemWidthPx);
        //Log.d(TAG, "numberOfColumns: " + numberOfColumns);

        // Setup RecyclerView for Recipes
        recyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        final MainListRecyclerAdapter adapter = new MainListRecyclerAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState == null) {
            // Create handle for the RetrofitInstance interface
            GetDataService service = RetrofitClientInstance.getRecipejson();
            Call<ArrayList<Recipe>> call = service.getAllRecipes();

            setIdlingResource(false);

            // Start Retrofit api call (async with enqueue)
            call.enqueue(new Callback<ArrayList<Recipe>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Recipe>> call,
                                       @NonNull Response<ArrayList<Recipe>> response) {
                    //Integer statusCode = response.code();
                    recipes = response.body();
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setRecipeData(recipes);
                    setIdlingResource(true);
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.VISIBLE);
                    Toast.makeText(MainListActivity.this,
                            R.string.network_call_error_toast_message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            recipes = savedInstanceState.getParcelableArrayList("All_recipes");
            if (recipes == null) {
                savedInstanceState = null;
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            } else {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setRecipeData(recipes);
            }
        }

    }

    @Override
    public void onListItemClick(Recipe clickedRecipeCard) {
        ArrayList<Recipe> selectedRecipe = new ArrayList<>();
        selectedRecipe.add(clickedRecipeCard);
        Bundle specificRecipeBundle = new Bundle();
        specificRecipeBundle.putParcelableArrayList("Current_Recipe", selectedRecipe);

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(specificRecipeBundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_credits:
                // Pass in this as the ListItemClickListener to the MovieListRecyclerAdapter constructor
                Intent startAboutCreditsActivity = new Intent(this,
                        AboutCreditsActivity.class);
                startActivity(startAboutCreditsActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("All_recipes", recipes);
    }

}
