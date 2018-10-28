package com.example.android.pjbakersbuzzin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pjbakersbuzzin.models.Step;

import java.util.ArrayList;

public class StepDetailFragment extends Fragment {

    private static final String TAG = StepDetailFragment.class.getSimpleName();

    // Strings to store information about the list of steps and list index
    private ArrayList<Step> steps;
    private Integer currentStep;
    private Integer stepId;
    private String recipeName;
    private String videoUrl;
    private String shortDescription;
    private String description;
    private String thumbnailURL;
    private String stepString;

    public StepDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: begin");
        steps = getArguments().getParcelableArrayList("Selected_Step");
        Log.d(TAG, "onCreateView: steps.size " + steps.size());
        currentStep = getArguments().getInt("Step_Index");
        Log.d(TAG, "onCreateView: currentStep " + currentStep);

//        recipeName = getArguments().getString("Title");
        stepId = steps.get(0).getId();
        shortDescription = steps.get(0).getShortDescription();
        description = steps.get(0).getDescription();
        videoUrl = steps.get(0).getVideoURL();
        thumbnailURL = steps.get(0).getThumbnailURL();

        View rootView = inflater.inflate(R.layout.fragment_step_detail, viewGroup, false);
        TextView stepTitleView = (TextView) rootView.findViewById(R.id.tv_step_short_description);
        TextView stepInstructionsView = (TextView) rootView.findViewById(R.id.tv_step_description);

        stepString = (stepId < 1) ? "" : "Step " + stepId.toString() + ": ";
        stepString = stepString + shortDescription;
        stepTitleView.setText(stepString);

        if (!description.equals(shortDescription)) {
            stepInstructionsView.setText(description);
        }

//        stepInstructionsView.setText("do all the stuff!");
//        textView.setVisibility(View.VISIBLE);

//        TextView textView;
//        mainHandler = new Handler();
//        bandwidthMeter = new DefaultBandwidthMeter();

//        itemClickListener =(RecipeDetailActivity)getActivity();

//        recipe = new ArrayList<>();

//        if(savedInstanceState == null) {
//            steps =getArguments().getParcelableArrayList("Selected_Steps");
//            if (steps!=null) {
//                steps =getArguments().getParcelableArrayList("Selected_Steps");
//                currentStep=getArguments().getInt("current");
//                recipeName=getArguments().getString("Title");
//            }
//            else {
//                recipe =getArguments().getParcelableArrayList(SELECTED_RECIPES);
//                //casting List to ArrayList
//                steps=(ArrayList<Step>)recipe.get(0).getSteps();
//                currentStep=0;
//            }
//
//        }
//        else
//        {
//            steps = savedInstanceState.getParcelableArrayList("Selected_Steps");
//            currentStep = savedInstanceState.getInt("current");
//            recipeName = savedInstanceState.getString("Title");
//
//
//        }


//        simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);
//        simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//
//        String videoURL = steps.get(selectedIndex).getVideoURL();
//
//        if (rootView.findViewWithTag("sw600dp-port-recipe_step_detail")!=null) {
//            recipeName=((RecipeDetailActivity) getActivity()).recipeName;
//            ((RecipeDetailActivity) getActivity()).getSupportActionBar().setTitle(recipeName);
//        }
//
//        String imageUrl=steps.get(selectedIndex).getThumbnailURL();
//        if (imageUrl!="") {
//            Uri builtUri = Uri.parse(imageUrl).buildUpon().build();
//            ImageView thumbImage = (ImageView) rootView.findViewById(R.id.thumbImage);
//            Picasso.with(getContext()).load(builtUri).into(thumbImage);
//        }
//
//        if (!videoURL.isEmpty()) {
//
//
//            initializePlayer(Uri.parse(steps.get(selectedIndex).getVideoURL()));
//
//            if (rootView.findViewWithTag("sw600dp-land-recipe_step_detail")!=null) {
//                getActivity().findViewById(R.id.fragment_container2).setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
//                simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
//            }
//            else if (isInLandscapeMode(getContext())){
//                textView.setVisibility(View.GONE);
//            }
//        }
//        else {
//            player=null;
//            simpleExoPlayerView.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.ic_visibility_off_white_36dp));
//            simpleExoPlayerView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
//        }


//        Button mPrevStep = (Button) rootView.findViewById(R.id.previousStep);
//        Button mNextstep = (Button) rootView.findViewById(R.id.nextStep);

//        mPrevStep.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (steps.get(selectedIndex).getId() > 0) {
//                    if (player!=null){
//                        player.stop();
//                    }
//                    itemClickListener.onListItemClick(steps,steps.get(selectedIndex).getId() - 1,recipeName);
//                }
//                else {
//                    Toast.makeText(getActivity(),"You already are in the First step of the recipe", Toast.LENGTH_SHORT).show();
//
//                }
//            }});

//        mNextstep.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//
//                int lastIndex = steps.size()-1;
//                if (steps.get(selectedIndex).getId() < steps.get(lastIndex).getId()) {
//                    if (player!=null){
//                        player.stop();
//                    }
//                    itemClickListener.onListItemClick(steps,steps.get(selectedIndex).getId() + 1,recipeName);
//                }
//                else {
//                    Toast.makeText(getContext(),"You already are in the Last step of the recipe", Toast.LENGTH_SHORT).show();
//
//                }
//            }});




        return rootView;
    }
}
