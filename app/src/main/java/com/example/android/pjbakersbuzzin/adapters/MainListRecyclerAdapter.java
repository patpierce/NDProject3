package com.example.android.pjbakersbuzzin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.pjbakersbuzzin.R;
import com.example.android.pjbakersbuzzin.models.Recipe;

import java.util.ArrayList;

/**
 * {@link MainListRecyclerAdapter} exposes a list of recipe items to a
 * {@link RecyclerView}
 */
public class MainListRecyclerAdapter
        extends RecyclerView.Adapter<MainListRecyclerAdapter.RecipeListViewHolder> {

    //private static final String TAG = MainListRecyclerAdapter.class.getSimpleName();
    final private ListItemClickListener onClickListener;
    private ArrayList<Recipe> dataList;

    /**
     * Constructor for IngredientListRecyclerAdapter that accepts
     * the specification for the ListItemClickListener.
     * <p>
     *
     * @param listener Listener for list item clicks
     */
    public MainListRecyclerAdapter(ListItemClickListener listener) {
        onClickListener = listener;
    }

    public void setRecipeData(ArrayList<Recipe> recipesIn) {
        dataList = recipesIn;
        notifyDataSetChanged();
    }

    @Override
    public RecipeListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.rvi_recipes_list_item, viewGroup, false);
        return new RecipeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeListViewHolder holder, int position) {
        String recipeName = dataList.get(position).getName();
        String servesHeader =
                holder.itemView.getContext().getResources().getString(R.string.serves_header);
        String numServings =
                " " + servesHeader + ": " + dataList.get(position).getServings().toString();
        String imageUrl = dataList.get(position).getImage();

        holder.recipeNameView.setText(recipeName);
        holder.numServingsView.setText(numServings);
        Context context = holder.recipeImageView.getContext();
        Glide.with(context).load(imageUrl)
                .placeholder(R.drawable.vg_kitchen).into(holder.recipeImageView);
    }

    @Override
    public int getItemCount() {
        return (dataList == null) ? 0 : dataList.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(Recipe clickedRecipeCard);
    }

    public class RecipeListViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView recipeNameView;
        private final TextView numServingsView;
        private final ImageView recipeImageView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         *
         * @param itemView The View that you inflated in
         *                 {@link MainListRecyclerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        private RecipeListViewHolder(View itemView) {
            super(itemView);

            recipeNameView = (TextView) itemView.findViewById(R.id.tv_content);
            numServingsView = (TextView) itemView.findViewById(R.id.tv_num_servings);
            recipeImageView = (ImageView) itemView.findViewById(R.id.iv_recipe_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onListItemClick(dataList.get(clickedPosition));
        }
    }

}
