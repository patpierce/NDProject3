package com.example.android.pjbakersbuzzin.network;

import com.example.android.pjbakersbuzzin.models.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/android-baking-app-json")
    Call<ArrayList<Recipe>> getAllRecipes();

}
