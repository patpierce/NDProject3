package com.example.android.pjbakersbuzzin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pjbakersbuzzin.R;
import com.example.android.pjbakersbuzzin.models.Ingredient;

import java.util.List;

/**
 * {@link IngredientListRecyclerAdapter} exposes a list of ingredient items to a
 * {@link RecyclerView}
 */
public class IngredientListRecyclerAdapter
        extends RecyclerView.Adapter<IngredientListRecyclerAdapter.IngredientsViewHolder> {

    private static final String TAG = IngredientListRecyclerAdapter.class.getSimpleName();

    private List<Ingredient> dataList;
    private Context context;
    private final ListItemClickListener mIngredientOnClickListener;

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(Ingredient clickedItemIndex);
    }

    /**
     * Constructor for IngredientListRecyclerAdapter that accepts
     * the specification for the ListItemClickListener.
     * <p>
     *
     * @param listener Listener for list item clicks
     */
    public IngredientListRecyclerAdapter(ListItemClickListener listener) {
        mIngredientOnClickListener = listener;
    }

    public void setIngredientData(List<Ingredient> ingredientData, Context contextIn) {
        dataList = ingredientData;
        context = contextIn;
        notifyDataSetChanged();
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.rvi_ingredient_list_item, viewGroup, false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + dataList.get(position).getIngredient());

        Double quantity = dataList.get(position).getQuantity();
        String measure = dataList.get(position).getMeasure();
        String ingredient = dataList.get(position).getIngredient();

        holder.mIngredientQuantityTextView.setText(quantity.toString());
        holder.mIngredientMeasureTextView.setText(measure);
        holder.mIngredientNameTextView.setText(ingredient);
    }

    public class IngredientsViewHolder
            extends RecyclerView.ViewHolder {
//            implements OnClickListener {

        private final TextView mIngredientNameTextView;
        private final TextView mIngredientQuantityTextView;
        private final TextView mIngredientMeasureTextView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         *
         * @param itemView The View that you inflated in
         *                 {@link IngredientListRecyclerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        private IngredientsViewHolder(View itemView) {
            super(itemView);

            mIngredientNameTextView = itemView.findViewById(R.id.tv_ingredient_name);
            mIngredientQuantityTextView = itemView.findViewById(R.id.tv_ingredient_value);
            mIngredientMeasureTextView = itemView.findViewById(R.id.tv_ingredient_measure);
            // Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//        }
    }

    @Override
    public int getItemCount() {
        return (dataList == null) ? 0 : dataList.size();
    }

}
