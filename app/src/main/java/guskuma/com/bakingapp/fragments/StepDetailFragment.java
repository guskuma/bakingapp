package guskuma.com.bakingapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import guskuma.com.bakingapp.R;
import guskuma.com.bakingapp.data.Step;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class StepDetailFragment extends Fragment {

    public static final String TAG = "frag_stepdetail_tag";

    private static final String ARG_STEPS = "steps_list";
    private static final String ARG_INDEX = "step_index";

    private Step mStep;
    private List<Step> mStepList;
    private int mStepIndex;
    private SimpleExoPlayer player;

    private StepDetailInteractionListener mListener;

    @Nullable @BindView(R.id.txtStepDescription) TextView txtStepDescription;
    @Nullable @BindView(R.id.btnNextStep) Button btnNextStep;
    @Nullable @BindView(R.id.btnPreviousStep) Button btnPreviousStep;
    @BindView(R.id.mediaPlayer) SimpleExoPlayerView mediaPlayerViewer;
    @BindView(R.id.txtVideoContainer) TextView txtVideoContainer;

    private Unbinder unbinder;

    public StepDetailFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stepList Step list to show
     * @return A new instance of fragment StepDetailFragment.
     */
    public static StepDetailFragment newInstance(List<Step> stepList, int index) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEPS, Parcels.wrap(stepList));
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new Timber.DebugTree());

        if (getArguments() != null) {
            setStepInfo((List<Step>) Parcels.unwrap(getArguments().getParcelable(ARG_STEPS)), getArguments().getInt(ARG_INDEX));
        }

        if(savedInstanceState != null) {
            setStepInfo((List<Step>) Parcels.unwrap(savedInstanceState.getParcelable(ARG_STEPS)), savedInstanceState.getInt(ARG_INDEX));
        }

    }

    private void setStepInfo(List<Step> stepList, int stepIndex){
        mStepList = stepList;
        mStepIndex = stepIndex;
        mStep = mStepList.get(mStepIndex);
    }

    public void resetVisualization(List<Step> stepList, int stepIndex){
        releasePlayer();
        setStepInfo(stepList, stepIndex);
        setStepDescription();
        configureNavigationButtons();
        initializePlayer(mStep.videoURL);
    }

    private void setStepDescription() {
        if(txtStepDescription != null) {
            txtStepDescription.setText(mStep.description);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ARG_STEPS, Parcels.wrap(mStepList));
        outState.putInt(ARG_INDEX, mStepIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        setStepDescription();

        if(btnNextStep != null) {
            btnNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onNextStepButtonClick(mStepList, mStepIndex + 1);
                }
            });
        }

        if(btnPreviousStep != null) {
            btnPreviousStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPreviousStepButtonClick(mStepList, mStepIndex - 1);
                }
            });
        }

        configureNavigationButtons();

        return view;
    }

    private void configureNavigationButtons(){
        if(btnNextStep != null) {
            btnNextStep.setEnabled(mStepIndex != (mStepList.size() - 1));
        }
        if(btnPreviousStep != null) {
            btnPreviousStep.setEnabled(mStepIndex != 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StepDetailInteractionListener) {
            mListener = (StepDetailInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StepDetailInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer(mStep.videoURL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer(mStep.videoURL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initializePlayer(String videoUrl) {
        if(videoUrl != null && !"".equals(videoUrl)) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            player.setPlayWhenReady(true);
            txtVideoContainer.setText(R.string.video_player_loading);
            player.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    if(!isLoading && mediaPlayerViewer != null){
                        mediaPlayerViewer.setVisibility(View.VISIBLE);
                        mediaPlayerViewer.bringToFront();
                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    txtVideoContainer.setText(R.string.video_player_error);
                    Toast.makeText(getContext(), R.string.video_player_error, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });

            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
            mediaPlayerViewer.setPlayer(player);
        } else {
            mediaPlayerViewer.setVisibility(View.INVISIBLE);
            if(txtStepDescription == null){
                txtVideoContainer.setText(mStep.description);
            } else {
                txtVideoContainer.setText(R.string.video_player_no_video);
            }
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-bakingapp")).
                createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public interface StepDetailInteractionListener {
        void onNextStepButtonClick(List<Step> stepList, int stepIndex);
        void onPreviousStepButtonClick(List<Step> stepList, int stepIndex);
    }

}
