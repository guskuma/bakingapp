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
import timber.log.Timber;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.RecipeDetailInteractionListener {

    public static final String ARG_RECIPE = "recipe_extra";
    private Recipe mRecipe;

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

        getSupportActionBar().setTitle(mRecipe.name);

        if(getSupportFragmentManager().findFragmentByTag(RecipeDetailFragment.TAG) == null) {
            RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(mRecipe);
            getSupportFragmentManager().beginTransaction().add(R.id.recipeDetailPlaceHolder, detailFragment, RecipeDetailFragment.TAG).commit();
        }
    }

    @Override
    public void onStepClick(List<Step> stepList, int stepIndex) {

        String stepName = String.format(getResources().getString(R.string.step_name), (stepIndex+1), mRecipe.steps.size(), mRecipe.name);

        Intent stepDetailIntent = new Intent(this, StepDetailActivity.class);
        stepDetailIntent.putExtra(StepDetailActivity.ARG_STEP_LIST, Parcels.wrap(stepList));
        stepDetailIntent.putExtra(StepDetailActivity.ARG_STEP_INDEX, stepIndex);
        stepDetailIntent.putExtra(StepDetailActivity.ARG_STEP_NAME, stepName);
        startActivity(stepDetailIntent);
    }


}
