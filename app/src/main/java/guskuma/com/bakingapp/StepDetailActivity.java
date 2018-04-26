package guskuma.com.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import guskuma.com.bakingapp.data.Step;
import guskuma.com.bakingapp.fragments.StepDetailFragment;
import timber.log.Timber;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.StepDetailInteractionListener {

    public static final String ARG_STEP_LIST = "step_list_extra";
    public static final String ARG_STEP_INDEX = "step_index_extra";
    public static final String ARG_RECIPE_NAME = "recipe_name_extra";
    private List<Step> mStepList;
    private int mStepIndex;
    private String mRecipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        mStepList = (List<Step>) Parcels.unwrap(intent.getParcelableExtra(ARG_STEP_LIST));
        mStepIndex = intent.getIntExtra(ARG_STEP_INDEX, -1);
        mRecipeName = intent.getStringExtra(ARG_RECIPE_NAME);

        if(savedInstanceState != null)
            mStepIndex = savedInstanceState.getInt(ARG_STEP_INDEX);

        setActivityTitle();

        if(getSupportFragmentManager().findFragmentByTag(StepDetailFragment.TAG) == null) {
            StepDetailFragment fragment = StepDetailFragment.newInstance(mStepList, mStepIndex);
            getSupportFragmentManager().beginTransaction().add(R.id.stepDetailPlaceHolder, fragment, StepDetailFragment.TAG).commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_STEP_INDEX, mStepIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNextStepButtonClick(List<Step> stepList, int stepIndex) {
        Timber.i("Next clicked: " + stepIndex);
        resetFragment(stepList, stepIndex);
    }

    @Override
    public void onPreviousStepButtonClick(List<Step> stepList, int stepIndex) {
        Timber.i("Previous clicked: " + stepIndex);
        resetFragment(stepList, stepIndex);
    }

    private void resetFragment(List<Step> stepList, int stepIndex) {
        StepDetailFragment fragment = (StepDetailFragment) getSupportFragmentManager().findFragmentByTag(StepDetailFragment.TAG);
        fragment.resetVisualization(stepList, stepIndex);
        mStepIndex = stepIndex;
        setActivityTitle();
    }

    private void setActivityTitle(){
        String stepName = String.format(getResources().getString(R.string.step_name), (mStepIndex+1), mStepList.size(), mRecipeName);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(stepName);
        }
    }
}
