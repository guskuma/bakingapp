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

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.txtStepDescription) TextView txtStepDescription;
    @BindView(R.id.mediaPlayer) SimpleExoPlayerView mediaPlayer;
    @BindView(R.id.btnNextStep) Button btnNextStep;
    @BindView(R.id.btnPreviousStep) Button btnPreviousStep;

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
            mStepList = (List<Step>) Parcels.unwrap(getArguments().getParcelable(ARG_STEPS));
            mStepIndex = getArguments().getInt(ARG_INDEX);
            mStep = mStepList.get(mStepIndex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);
        txtStepDescription.setText(mStep.description);

        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNextStepButtonClick(mStepList, mStepIndex +1);
            }
        });

        btnPreviousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPreviousStepButtonClick(mStepList, mStepIndex -1);
            }
        });

        return view;
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

    private void initializePlayer(String videoUrl) {
        if(videoUrl != null && !"".equals(videoUrl)) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            player.setPlayWhenReady(true);

            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);

            mediaPlayer.setPlayer(player);
            mediaPlayer.setVisibility(View.VISIBLE);
        } else {
            mediaPlayer.setVisibility(View.GONE);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-bakingapp")).
                createMediaSource(uri);
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

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public interface StepDetailInteractionListener {
        public void onNextStepButtonClick(List<Step> stepList, int stepIndex);
        public void onPreviousStepButtonClick(List<Step> stepList, int stepIndex);
    }

}
