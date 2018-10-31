package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pjbakersbuzzin.adapters.MainListRecyclerAdapter;
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

    private static final String TAG = MainListActivity.class.getSimpleName();

    private MainListRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        // "progress bar" circle
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // TextView to display errors, hidden if no errors
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        float dens = resources.getDisplayMetrics().density;
        // Note, screenHeightDp isn't reliable
        // (it seems to be too small by the height of the status bar),
        // but we assume screenWidthDp is reliable.
        int screenWidthDp = config.screenWidthDp;
//        Log.d(TAG, "screenWidthDp: " + screenWidthDp);
        int screenWidthPx = (int) (screenWidthDp * dens);
//        Log.d(TAG, "screenWidthPx: " + screenWidthPx);
        int itemWidthPx = (int) (resources.getDimension(R.dimen.recipe_card_width));
        int numberOfColumns = (screenWidthPx / itemWidthPx);
//        Log.d(TAG, "itemWidthPx: " + itemWidthPx);
//        Log.d(TAG, "numberOfColumns: " + numberOfColumns);

        // Setup RecyclerView for Recipes
        recyclerView = findViewById(R.id.rv_main_list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        final MainListRecyclerAdapter adapter = new MainListRecyclerAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // Create handle for the RetrofitInstance interface
        GetDataService service = RetrofitClientInstance.getRecipejson();
        Call<ArrayList<Recipe>> call = service.getAllRecipes();

        // Start Retrofit api call (async with enqueue)
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call,
                                   Response<ArrayList<Recipe>> response) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
                Integer statusCode = response.code();
                Log.v("status code: ", statusCode.toString());

                ArrayList<Recipe> recipes = response.body();

                adapter.setRecipeData(recipes, context);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Toast.makeText(MainListActivity.this,
                        "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onListItemClick(Recipe clickedRecipeCard) {
        Bundle specificRecipeBundle = new Bundle();
        ArrayList<Recipe> selectedRecipe = new ArrayList<>();
        selectedRecipe.add(clickedRecipeCard);
        specificRecipeBundle.putParcelableArrayList("Current_Recipe",selectedRecipe);

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(specificRecipeBundle);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
