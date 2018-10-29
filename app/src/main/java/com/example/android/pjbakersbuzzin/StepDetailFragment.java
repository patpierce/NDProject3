package com.example.android.pjbakersbuzzin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pjbakersbuzzin.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepDetailFragment extends Fragment {

    private static final String TAG = StepDetailFragment.class.getSimpleName();

    // Strings to store information about the list of steps and list index
    private ArrayList<Step> steps;
    private Integer clickedItemIndex;
    private Integer stepId;
    private String recipeName;
    private String videoUrl;
    private String shortDescription;
    private String description;
    private String thumbnailURL;
    private String stepTitleString;

    private PlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private BandwidthMeter bandwidthMeter;
    private Handler mainHandler;

    public StepDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: begin");
        steps = getArguments().getParcelableArrayList("Steps_Bundle");
        Log.d(TAG, "onCreateView: steps.size " + steps.size());
        clickedItemIndex = getArguments().getInt("Step_Index");
        Log.d(TAG, "onCreateView: clickedItemIndex " + clickedItemIndex);

//        recipeName = getArguments().getString("Title");
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

        exoPlayerView = stepView.findViewById(R.id.exo_player_view);
        exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

        if (!videoUrl.isEmpty()) {
            initializePlayer(Uri.parse(videoUrl));
        }

        // todo add buttons for next and prev

        return stepView;
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
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Bakers Buzzin"), bandwidthMeter);
        MediaSource videoSource = new ExtractorMediaSource.
                Factory(dataSourceFactory).createMediaSource(uri);
        // Prepare the player with the source.
        return videoSource;
    }

}
