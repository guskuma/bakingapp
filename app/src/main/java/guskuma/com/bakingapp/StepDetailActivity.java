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
    public static final String ARG_STEP_NAME = "step_name_extra";
    private List<Step> mStepList;
    private int mStepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mStepList = (List<Step>) Parcels.unwrap(intent.getParcelableExtra(ARG_STEP_LIST));
        mStepIndex = intent.getIntExtra(ARG_STEP_INDEX, -1);
        getSupportActionBar().setTitle(intent.getStringExtra(ARG_STEP_NAME));

        if(getSupportFragmentManager().findFragmentByTag(StepDetailFragment.TAG) == null) {
            StepDetailFragment fragment = StepDetailFragment.newInstance(mStepList, mStepIndex);
            getSupportFragmentManager().beginTransaction().add(R.id.stepDetailPlaceHolder, fragment, StepDetailFragment.TAG).commit();
        }

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
        Timber.i("Next clicked");
    }

    @Override
    public void onPreviousStepButtonClick(List<Step> stepList, int stepIndex) {
        Timber.i("Previous clicked");
    }
}
