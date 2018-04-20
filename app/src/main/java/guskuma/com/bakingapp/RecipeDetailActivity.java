package guskuma.com.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.parceler.Parcels;

import guskuma.com.bakingapp.data.Recipe;
import guskuma.com.bakingapp.data.Step;
import guskuma.com.bakingapp.fragments.RecipeDetailFragment;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener {

    public static final String ARG_RECIPE = "recipe_extra";
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        mRecipe = (Recipe) Parcels.unwrap(i.getParcelableExtra(ARG_RECIPE));

        RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(mRecipe);
        getSupportFragmentManager().beginTransaction().add(R.id.detailPlaceHolder, detailFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Step step) {

    }
}
