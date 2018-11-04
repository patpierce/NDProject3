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
import com.example.android.pjbakersbuzzin.models.Step;

import java.util.ArrayList;

/**
 * {@link StepListRecyclerAdapter} exposes a list of step items to a
 * {@link RecyclerView}
 */
public class StepListRecyclerAdapter
        extends RecyclerView.Adapter<StepListRecyclerAdapter.StepsViewHolder> {

    private static final String TAG = StepListRecyclerAdapter.class.getSimpleName();

    private ArrayList<Step> dataList;
    private Context context;

    private final ListItemClickListener mStepOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    /**
     * Constructor for StepListRecyclerAdapter that accepts
     * the specification for the ListItemClickListener.
     * <p>
     *
     * @param listener Listener for list item clicks
     */
    public StepListRecyclerAdapter(ListItemClickListener listener) {
        mStepOnClickListener = listener;
    }

    public void setStepData(ArrayList<Step> stepData, Context contextIn) {
        dataList = stepData;
        context = contextIn;
        notifyDataSetChanged();
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.rvi_step_list_item, viewGroup, false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {

        Integer stepId = dataList.get(position).getId();
        String stepNum = (stepId < 1) ? "" :
                context.getResources().getString(R.string.step_header) + " " + stepId.toString();
        String shortDescription = dataList.get(position).getShortDescription();
        String thumbnailUrl = dataList.get(position).getThumbnailURL();

        holder.mStepNumberTextView.setText(stepNum);
        holder.mStepsShortDescriptionTextView.setText(shortDescription);
        Context context = holder.mStepThumbnailImageView.getContext();
        Glide.with(context).load(thumbnailUrl)
                .placeholder(R.drawable.vg_spoon).into(holder.mStepThumbnailImageView);
    }

    public class StepsViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mStepNumberTextView;
        private final TextView mStepsShortDescriptionTextView;
        private final ImageView mStepThumbnailImageView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         *
         * @param itemView The View that you inflated in
         *                 {@link StepListRecyclerAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        private StepsViewHolder(View itemView) {
            super(itemView);

            mStepNumberTextView = (TextView) itemView.findViewById(R.id.tv_step_number);
            mStepsShortDescriptionTextView = (TextView) itemView.findViewById(R.id.tv_step_short_description);
            mStepThumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_step_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mStepOnClickListener.onListItemClick(clickedPosition);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList == null) ? 0 : dataList.size();
    }

}
