package guskuma.com.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.parceler.Parcels;

import java.util.List;

import guskuma.com.bakingapp.data.Recipe;
import guskuma.com.bakingapp.data.Step;
import guskuma.com.bakingapp.fragments.RecipeDetailFragment;
import guskuma.com.bakingapp.fragments.StepDetailFragment;
import timber.log.Timber;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.RecipeDetailInteractionListener, StepDetailFragment.StepDetailInteractionListener {

    public static final String ARG_RECIPE = "recipe_extra";
    private Recipe mRecipe;
    private boolean mBigScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Timber.plant(new Timber.DebugTree());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(mRecipe== null) {
            Intent i = getIntent();
            mRecipe = (Recipe) Parcels.unwrap(i.getParcelableExtra(ARG_RECIPE));
        }

        mBigScreen = (findViewById(R.id.recipeStepPlaceHolder) != null);

        if(getSupportFragmentManager().findFragmentByTag(RecipeDetailFragment.TAG) == null) {
            RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(mRecipe);
            if(!mBigScreen) {
                getSupportActionBar().setTitle(mRecipe.name);
                getSupportFragmentManager().beginTransaction().add(R.id.recipeDetailPlaceHolder, detailFragment, RecipeDetailFragment.TAG).commit();
            } else {
                StepDetailFragment stepDetailFragment = StepDetailFragment.newInstance(mRecipe.steps, 0);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipeDetailPlaceHolder, detailFragment, RecipeDetailFragment.TAG)
                        .add(R.id.recipeStepPlaceHolder, stepDetailFragment, StepDetailFragment.TAG)
                        .commit();
                setActivityTitle(0);
            }
        }
    }

    @Override
    public void onStepClick(List<Step> stepList, int stepIndex) {

        if(!mBigScreen) {
            Intent stepDetailIntent = new Intent(this, StepDetailActivity.class);
            stepDetailIntent.putExtra(StepDetailActivity.ARG_STEP_LIST, Parcels.wrap(stepList));
            stepDetailIntent.putExtra(StepDetailActivity.ARG_STEP_INDEX, stepIndex);
            stepDetailIntent.putExtra(StepDetailActivity.ARG_RECIPE_NAME, mRecipe.name);
            startActivity(stepDetailIntent);
        } else {
            resetFragment(mRecipe.steps, stepIndex);
        }
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
        setActivityTitle(stepIndex);
    }

    private void setActivityTitle(int stepIndex){
        String stepName = String.format(getResources().getString(R.string.recipe_name_with_steps), mRecipe.name, (stepIndex+1), mRecipe.steps.size());
        getSupportActionBar().setTitle(stepName);
    }


}
