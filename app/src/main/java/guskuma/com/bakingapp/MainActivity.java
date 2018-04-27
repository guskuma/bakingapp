package guskuma.com.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import guskuma.com.bakingapp.adapter.RecipesRecyclerViewAdapter;
import guskuma.com.bakingapp.data.Recipe;
import guskuma.com.bakingapp.data.RecipeService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipesRecyclerViewAdapter.RecipeInteractionListener {

    private RecipeService mRecipeService;
    @BindView(R.id.listRecipes) RecyclerView recyclerView;
    @BindView(R.id.btnTryAgain) Button btnTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        Gson gson = new GsonBuilder().setLenient().create();
        mRecipeService = new Retrofit.Builder()
                .baseUrl(RecipeService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RecipeService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fetchRecipeList();

        if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.setSpanCount(3);
        }
    }

    private void fetchRecipeList() {
        final List<Recipe> recipes = new ArrayList<>();

        Call<List<Recipe>> call = mRecipeService.getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                recipes.clear();
                recipes.addAll(response.body());
                Timber.v(recipes.size() + " recipes retrieved");
                recyclerView.setAdapter(new RecipesRecyclerViewAdapter(recipes, MainActivity.this));
                recyclerView.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Timber.e(t);
                Toast.makeText(MainActivity.this, R.string.recipe_list_fetch_fail, Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.GONE);
                btnTryAgain.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRecipeClick(Recipe item) {
        Timber.v("Recipe clicked: " + item.name);
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.ARG_RECIPE, Parcels.wrap(item));
        startActivity(i);
    }

    @OnClick(R.id.btnTryAgain)
    public void onTryAgainClick(View view){
        fetchRecipeList();
    }
}
