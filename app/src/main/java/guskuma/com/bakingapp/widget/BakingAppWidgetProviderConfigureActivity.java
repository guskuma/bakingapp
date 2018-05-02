package guskuma.com.bakingapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import guskuma.com.bakingapp.R;
import guskuma.com.bakingapp.data.Recipe;

/**
 * The configuration screen for the {@link BakingAppWidgetProvider BakingAppWidgetProvider} AppWidget.
 */
public class BakingAppWidgetProviderConfigureActivity extends Activity {

    private static final String PREFS_NAME = "guskuma.com.bakingapp.widget.BakingAppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @BindView(R.id.spinner_recipes) Spinner mSpinnerRecipes;
    @BindView(R.id.add_button) Button mBtnAddWidget;


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = BakingAppWidgetProviderConfigureActivity.this;

            // When the button is clicked, store the string locally
            Recipe recipe = (Recipe)mSpinnerRecipes.getSelectedItem();
            String widgetText = recipe.getIngredientsText();
            saveTitlePref(context, mAppWidgetId, recipe.name ,widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BakingAppWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public BakingAppWidgetProviderConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String title, String description) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_title", title);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_desc", description);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_title", null);
        return titleValue;
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadDescrPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_desc", null);
        return titleValue;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_title");
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + "_desc");
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_app_widget_provider_configure);
        ButterKnife.bind(this);
        mBtnAddWidget.setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String pref_key = getResources().getString(R.string.preference_recipe_list);
        if(prefs.contains(pref_key)){
            Type listType = new TypeToken<ArrayList<Recipe>>(){}.getType();
            List<Recipe> recipeList = (List<Recipe>) new Gson().fromJson(prefs.getString(pref_key, ""), listType);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, recipeList);
            mSpinnerRecipes.setAdapter(spinnerArrayAdapter);
        } else {
            mSpinnerRecipes.setEnabled(false);
            Toast.makeText(this, R.string.no_recipes_available_yet, Toast.LENGTH_LONG).show();
        }

    }
}

