package guskuma.com.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import guskuma.com.bakingapp.data.Recipe;
import guskuma.com.bakingapp.fragments.RecipesFragment;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipesFragment.OnRecipeClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        RecipesFragment recipesFragment = RecipesFragment.newInstance(1);
        getSupportFragmentManager().beginTransaction().add(R.id.recipeListContainer, recipesFragment).commit();
    }

    @Override
    public void onListFragmentInteraction(Recipe item) {
        Timber.v("Recipe clicked: " + item.name);
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.ARG_RECIPE, Parcels.wrap(item));
        startActivity(i);
    }
}
