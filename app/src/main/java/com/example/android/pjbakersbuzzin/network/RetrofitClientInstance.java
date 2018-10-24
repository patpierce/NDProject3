package com.example.android.pjbakersbuzzin.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClientInstance {

    private static GetDataService recipeList;
    private static final String BASE_URL = "http://go.udacity.com";

    public static GetDataService getRecipejson() {

        Gson gson = new GsonBuilder().create();

        if (recipeList == null) {
            recipeList = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build().create(GetDataService.class);
        }
        return recipeList;
    }

}
