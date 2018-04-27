package guskuma.com.bakingapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import guskuma.com.bakingapp.R;
import guskuma.com.bakingapp.data.Recipe;
import timber.log.Timber;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Recipe} and makes a call to the
 * specified {@link RecipeInteractionListener}.
 */
public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    private final List<Recipe> mValues;
    private final RecipeInteractionListener mListener;

    public RecipesRecyclerViewAdapter(List<Recipe> items, RecipeInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);
        Timber.plant(new Timber.DebugTree());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String summary = holder.mView.getResources().getString(R.string.recipe_summary);
        summary = String.format(summary, holder.mItem.servings, holder.mItem.ingredients.size(), holder.mItem.steps.size());

        holder.mRecipeName.setText(holder.mItem.name);
        holder.mRecipeSummary.setText(summary);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRecipeClick(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.txtRecipeName) public TextView mRecipeName;
        @BindView(R.id.txtRecipeSummary) public TextView mRecipeSummary;
        public Recipe mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRecipeName.getText() + "'";
        }
    }

    public interface RecipeInteractionListener {
        void onRecipeClick(Recipe item);
    }
}
