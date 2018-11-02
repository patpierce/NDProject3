package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pjbakersbuzzin.models.Recipe;
import com.example.android.pjbakersbuzzin.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepDetailFragment extends Fragment {

    private static final String TAG = StepDetailFragment.class.getSimpleName();

    // Strings to store information about the list of steps and list index
    private ArrayList<Recipe> recipe;
    private ArrayList<Step> steps;
    private Integer clickedItemIndex;
    private Integer stepId;
    private String videoUrl;
    private String shortDescription;
    private String description;
    private String thumbnailURL;
    private String stepTitleString;

    private PlayerView exoPlayerView;
    private ImageView exoPlaceholderView;
    private SimpleExoPlayer exoPlayer;

    public StepDetailFragment() {
    }

    ButtonClickListener navClickListener;

    public interface ButtonClickListener {
        void onButtonClick(Integer targetStepIndex);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        try {
            navClickListener = (ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: begin");
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreateView: savedInstanceState yes");
            steps = savedInstanceState.getParcelableArrayList("Saved_Steps_Bundle");
            clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
        }

        Bundle arguments = getArguments();
        if (steps == null) {
            if (arguments != null) {
                if (arguments.containsKey("Current_Recipe")) {
                    recipe = getArguments().getParcelableArrayList("Current_Recipe");
                    steps = recipe.get(0).getSteps();
                    Log.d(TAG, "onCreateView: getting steps from Current_Recipe " + steps.size());
                }
                if (arguments.containsKey("Step_Index")) {
                    clickedItemIndex = arguments.getInt("Step_Index");
                    Log.d(TAG, "onCreateView: getting clickedItemIndex from Step_Index " + clickedItemIndex);
                }
                else
                    clickedItemIndex = 0;
            }
        }

        stepId = steps.get(clickedItemIndex).getId();
        shortDescription = steps.get(clickedItemIndex).getShortDescription();
        description = steps.get(clickedItemIndex).getDescription();
        videoUrl = steps.get(clickedItemIndex).getVideoURL();
        thumbnailURL = steps.get(clickedItemIndex).getThumbnailURL();

        View stepView = inflater.inflate(R.layout.fragment_step_detail, viewGroup, false);
        TextView stepTitleView = stepView.findViewById(R.id.tv_step_short_description);
        TextView stepInstructionsView = stepView.findViewById(R.id.tv_step_description);

        stepTitleString = (stepId < 1) ? "" : "Step " + stepId.toString() + ": ";
        stepTitleString = stepTitleString + shortDescription;
        stepTitleView.setText(stepTitleString);

        if (!description.equals(shortDescription)) {
            stepInstructionsView.setText(description);
        }

        exoPlaceholderView = stepView.findViewById(R.id.exo_placeholder_view);
        exoPlayerView = stepView.findViewById(R.id.exo_player_view);
        exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//        exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

        if (!videoUrl.equals("")) {
            Log.d(TAG, "onCreateView: videoUrl " + videoUrl);
            exoPlaceholderView.setVisibility(View.INVISIBLE);
            exoPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(videoUrl));
        }
        else {
            Log.d(TAG, "onCreateView: videoUrl empty");
            exoPlayerView.setVisibility(View.INVISIBLE);
            exoPlaceholderView.setVisibility(View.VISIBLE);
            exoPlayer = null;
//            exoPlayerView.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.vg_silicone_spatula));
//            exoPlayerView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        }

        Button prevButtonView = stepView.findViewById(R.id.previous_button);
        prevButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickedItemIndex > 0) {
                    Integer targetStepIndex = clickedItemIndex - 1;
                    if (exoPlayer != null){ exoPlayer.stop(); }
                    navClickListener.onButtonClick(targetStepIndex);
                }
                else {
                    Toast.makeText(getActivity(),
                            "No previous steps", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button nextButtonView = stepView.findViewById(R.id.next_button);
        nextButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (clickedItemIndex < (steps.size() - 1)) {
                    Integer targetStepIndex = clickedItemIndex + 1;
                    if (exoPlayer != null){ exoPlayer.stop(); }
                    navClickListener.onButtonClick(targetStepIndex);
                }
                else {
                    Toast.makeText(getActivity(),
                            "No more steps", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return stepView;
    }

    public void setListIndex(int newindex) {
        clickedItemIndex = newindex;
    }

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {
            Log.d(TAG, "initializePlayer: url " + mediaUri.toString());

            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.seekTo(0);

            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            exoPlayer.prepare(mediaSource, true, false);
            exoPlayer.setPlayWhenReady(true);

        }
    }

    private MediaSource buildMediaSource(Uri uri) {

        Context context = getContext();
        assert context != null;
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Bakers Buzzin"), bandwidthMeter);
        // Prepare the player with the source.
        return new ExtractorMediaSource.
                Factory(dataSourceFactory).createMediaSource(uri);
    }

    public void expolayerStopReleaseResources() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        expolayerStopReleaseResources();
    }

    @Override
    public void onStop() {
        super.onStop();
        expolayerStopReleaseResources();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        expolayerStopReleaseResources();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        expolayerStopReleaseResources();
        exoPlayer = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) exoPlayerView.getLayoutParams();

        Log.d(TAG, "onConfigurationChanged: newConfig.orientation" + newConfig.orientation);
        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //First Hide other objects (listview or recyclerview), better hide them using Gone.
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            exoPlayerView.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //unhide your objects here.
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = 250;
            exoPlayerView.setLayoutParams(params);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Saved_Steps_Bundle", steps);
        currentState.putInt("Saved_Step_Index", clickedItemIndex);
    }

}
