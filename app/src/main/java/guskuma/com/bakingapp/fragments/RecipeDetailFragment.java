package guskuma.com.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import guskuma.com.bakingapp.R;
import guskuma.com.bakingapp.data.Ingredient;
import guskuma.com.bakingapp.data.Recipe;
import guskuma.com.bakingapp.data.Step;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeDetailInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailFragment extends Fragment {

    public static final String TAG = "frag_recipedetail_tag";

    private static final String ARG_RECIPE_OBJECT = "recipe_object";
    private Recipe mRecipe;

    private RecipeDetailInteractionListener mListener;

    @BindView(R.id.txtIngredients) TextView txtIngredients;
    @BindView(R.id.stepsPlaceHolder) LinearLayout stepsPlaceHolder;


    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe Recipe object to show
     * @return A new instance of fragment RecipeDetailFragment.
     */
    public static RecipeDetailFragment newInstance(Recipe recipe) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE_OBJECT, Parcels.wrap(recipe));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new Timber.DebugTree());

        if (getArguments() != null) {
            mRecipe = (Recipe) Parcels.unwrap(getArguments().getParcelable(ARG_RECIPE_OBJECT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);
        String ingredients =  "";

        final DecimalFormat format = new DecimalFormat("0.#");

        for (Ingredient i : mRecipe.ingredients) {
            String quantity = format.format(i.quantity);
            String measure = handleMeasure(i.measure);
            ingredients += quantity + measure + i.ingredient + "; ";
        }

        txtIngredients.setText(ingredients);

        for(final Step s : mRecipe.steps){
            View v = inflater.inflate(R.layout.fragment_recipe_detail_step, stepsPlaceHolder, false);
            TextView txtCircleNumber = (TextView)v.findViewById(R.id.txtCircleNumber);
            TextView txtShortDescription = (TextView)v.findViewById(R.id.txtShortDescription);
            TextView txtDescription = (TextView)v.findViewById(R.id.txtDescription);
            View vwDivider = (View)v.findViewById(R.id.divider);

            if(s.id == 0) {
                vwDivider.setVisibility(View.GONE);
            }

            txtCircleNumber.setText(String.valueOf(s.id));
            txtShortDescription.setText( s.shortDescription);
            txtDescription.setText(s.description);

            stepsPlaceHolder.addView(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onStepClick(mRecipe.steps, s.id);
                }
            });
        }

        return view;
    }

    private String handleMeasure(String measure) {
        if("UNIT".equals(measure)) {
            return " ";
        }

        if("CUP".equals(measure) || "TSP".equals(measure) || "TBLSP".equals(measure))
            return " " + measure.toLowerCase() + " ";

        return measure.toLowerCase() + " ";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipeDetailInteractionListener) {
            mListener = (RecipeDetailInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RecipeDetailInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface RecipeDetailInteractionListener {
        void onStepClick(List<Step> stepList, int stepIndex);
    }
}
