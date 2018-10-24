package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

        // This TextView is used to display errors and will be hidden if there are no errors
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        // Setup RecyclerView for Recipes
        recyclerView = findViewById(R.id.rv_main_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
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
                // Take response from recipe api call put it in a bundle
//                Bundle recipesBundle = new Bundle();
//                recipesBundle.putParcelableArrayList("All_Recipes", recipes);

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
    public void onListItemClick(Recipe clickedItemIndex) {
        Bundle specificRecipeBundle = new Bundle();
        ArrayList<Recipe> selectedRecipe = new ArrayList<>();
        selectedRecipe.add(clickedItemIndex);
        specificRecipeBundle.putParcelableArrayList("Selected_Recipe",selectedRecipe);

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(specificRecipeBundle);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
