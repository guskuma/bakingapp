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
import guskuma.com.bakingapp.data.Step;
import guskuma.com.bakingapp.fragments.RecipeDetailFragment;
import timber.log.Timber;

public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.ViewHolder> {

private final List<Step> mSteps;
private final RecipeDetailFragment.OnStepClickListener mListener;

    public StepsRecyclerViewAdapter(List<Step> items, RecipeDetailFragment.OnStepClickListener listener) {
        mSteps = items;
        mListener = listener;
    }

    @Override
    public StepsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipe_detail_step, parent, false);
        Timber.plant(new Timber.DebugTree());

        return new StepsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StepsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mSteps.get(position);

        holder.mStepDescription.setText( ++position + " - " + holder.mItem.shortDescription);
        holder.mDescription.setText(holder.mItem.description);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.txtShortDescription) public TextView mStepDescription;
        @BindView(R.id.txtDescription) public TextView mDescription;
        public Step mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStepDescription.getText() + "'";
        }
    }
}
