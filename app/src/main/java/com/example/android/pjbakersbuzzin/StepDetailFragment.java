package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private ArrayList<Step> steps;
    private Integer clickedItemIndex;

    private PlayerView exoPlayerView;
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
        // always reference specific Step by the "clickedItemIndex" which is automatically generated (0-n)
        //  and not the "stepId", which is supplied by API and may skip integers

        if (savedInstanceState != null) {
            steps = savedInstanceState.getParcelableArrayList("Saved_Steps_Bundle");
            clickedItemIndex = savedInstanceState.getInt("Saved_Step_Index");
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                if (steps == null) {
                    if (arguments.containsKey("Current_Recipe")) {
                        ArrayList<Recipe> recipe = getArguments().getParcelableArrayList("Current_Recipe");
                        steps = recipe.get(0).getSteps();
                    }
                }
                if (clickedItemIndex == null) {
                    // we didnt get it from savedInstanceState
                    if (arguments.containsKey("Step_Index")) {
                        clickedItemIndex = arguments.getInt("Step_Index");
                    } else {
                        clickedItemIndex = 0;
                    }
                }
            }
            Integer stepId = steps.get(clickedItemIndex).getId();
            String shortDescription = steps.get(clickedItemIndex).getShortDescription();
            String description = steps.get(clickedItemIndex).getDescription();
            String videoUrl = steps.get(clickedItemIndex).getVideoURL();
            String thumbnailURL = steps.get(clickedItemIndex).getThumbnailURL();

            final View stepView = inflater.inflate(R.layout.fragment_step_detail, viewGroup, false);
            TextView stepTitleView = (TextView) stepView.findViewById(R.id.tv_step_short_description);
            TextView stepInstructionsView = (TextView) stepView.findViewById(R.id.tv_step_description);

            // Building the text views is the only place to use "stepId" as retrieved from API
            String stepTitleString = (stepId < 1) ? "" : getString(R.string.step_header) +
                    " " + stepId.toString() + ": ";
            stepTitleString = stepTitleString + shortDescription;
            stepTitleView.setText(stepTitleString);

            if (!description.equals(shortDescription)) {
                stepInstructionsView.setText(description);
            }

            ImageView exoPlaceholderView = (ImageView) stepView.findViewById(R.id.exo_placeholder_view);
            exoPlayerView = (PlayerView) stepView.findViewById(R.id.exo_player_view);
            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            if (!videoUrl.equals("")) {
                exoPlaceholderView.setVisibility(View.GONE);
                exoPlayerView.setVisibility(View.VISIBLE);
                initializePlayer(Uri.parse(videoUrl));
            }
            else {
                exoPlayerView.setVisibility(View.GONE);
                exoPlaceholderView.setVisibility(View.VISIBLE);
                exoPlayer = null;
            }

            Button prevButtonView = (Button) stepView.findViewById(R.id.previous_button);
            prevButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickedItemIndex > 0) {
                        Integer targetStepIndex = clickedItemIndex - 1;
                        if (exoPlayer != null){ exoPlayer.stop(); }
                        setListIndex(targetStepIndex);
                        navClickListener.onButtonClick(targetStepIndex);
                    }
                    else {
                        Toast.makeText(getActivity(),
                                R.string.begin_of_steps_message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button nextButtonView = (Button) stepView.findViewById(R.id.next_button);
            nextButtonView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (clickedItemIndex < (steps.size() - 1)) {
                        Integer targetStepIndex = clickedItemIndex + 1;
                        if (exoPlayer != null){ exoPlayer.stop(); }
                        setListIndex(targetStepIndex);
                        navClickListener.onButtonClick(targetStepIndex);
                    }
                    else {
                        Toast.makeText(getActivity(),
                                R.string.end_of_steps_message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return stepView;
        }

        return null;

    }

    public void setListIndex(int newindex) {
        clickedItemIndex = newindex;
    }

    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.seekTo(0);

            MediaSource mediaSource = buildMediaSource(mediaUri);
            exoPlayer.prepare(mediaSource, true, false);
            exoPlayerView.hideController();
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        Context context = getContext();
        assert context != null;
        // Measures bandwidth during playback.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getString(R.string.app_name)), bandwidthMeter);
        // Prepare the player with the source.
        return new ExtractorMediaSource.
                Factory(dataSourceFactory).createMediaSource(uri);
    }

    public void exoPlayerStopReleaseResources() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayerStopReleaseResources();
    }

    @Override
    public void onStop() {
        super.onStop();
        exoPlayerStopReleaseResources();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        exoPlayerStopReleaseResources();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exoPlayerStopReleaseResources();
        exoPlayer = null;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList("Saved_Steps_Bundle", steps);
        currentState.putInt("Saved_Step_Index", clickedItemIndex);
    }

}
