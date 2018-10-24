package com.example.android.pjbakersbuzzin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.pjbakersbuzzin.models.Step;

import java.util.ArrayList;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeDetailActivity}.
 */
public class StepDetailActivity extends AppCompatActivity {

    private static final String TAG = StepDetailActivity.class.getSimpleName();
    private ArrayList<Step> step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Intent intentThatStartedThisActivity = getIntent();

        TextView mDetailRecipeNameView = findViewById(R.id.tv_step_description);

        final Integer stepId;
        final String shortDescription;
        final String description;
        final String videoUrl;
        final String thumbnailUrl;

        Bundle selectedStepBundle = getIntent().getExtras();

        step = selectedStepBundle.getParcelableArrayList("Selected_Step");

        stepId = step.get(0).getId();
        shortDescription = step.get(0).getShortDescription();
        description = step.get(0).getDescription();
        videoUrl = step.get(0).getVideoURL();
        thumbnailUrl = step.get(0).getThumbnailURL();

        // temporarily just dumping all the info into one text view
        //  as a starting point for creating the detail view
        String text = stepId.toString() + "\n" +
                shortDescription + "\n" +
                description+ "\n" +
                videoUrl+ "\n" +
                thumbnailUrl;

        mDetailRecipeNameView.setText(text);
    }

}
