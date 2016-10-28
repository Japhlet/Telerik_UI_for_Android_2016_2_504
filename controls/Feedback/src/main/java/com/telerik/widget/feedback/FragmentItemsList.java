package com.telerik.widget.feedback;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentItemsList extends Fragment implements AdapterView.OnItemClickListener {

    private ListView itemsList;
    private ProgressBar progressBar;
    int itemsFilter = RadFeedback.ALL_ITEMS;
    private RadFeedback feedbackInstance = RadFeedback.instance();
    private Integer runningTaskId = null;
    private TextView txtNoItems;

    public FragmentItemsList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = View.inflate(this.getActivity(), R.layout.fragment_feedback_items, null);
        this.itemsList = (ListView) root.findViewById(R.id.listItems);
        this.progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        this.itemsList.setOnItemClickListener(this);
        this.txtNoItems = (TextView) root.findViewById(R.id.txtNoItems);


        itemsFilter = savedInstanceState != null ? savedInstanceState.getInt("itemsFilter") : itemsFilter;
        progressBar.setVisibility(View.VISIBLE);
        itemsList.setVisibility(View.GONE);
        this.requestItems();

        return root;
    }

    @Override
    public void onDestroy() {
        if (this.runningTaskId != null) {
            feedbackInstance.cancelTask(this.runningTaskId);
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("itemsFilter", itemsFilter);
    }

    public void requestItems(){
        if (this.runningTaskId != null){
            this.feedbackInstance.cancelTask(this.runningTaskId);
            this.runningTaskId = null;
        }

        progressBar.setVisibility(View.VISIBLE);
        itemsList.setVisibility(View.GONE);
        this.runningTaskId = this.feedbackInstance.getCurrentItems(new RadFeedback.OnItemsDeliveredCallback() {
            @Override
            public void onItemsDelivered(Exception e, ArrayList<FeedbackItem> items) {
                runningTaskId = null;
                if (e != null) {
                    Toast.makeText(FragmentItemsList.this.getActivity(), getResources().getString(R.string.feedback_not_retrieved), Toast.LENGTH_SHORT).show();
                    checkToggleNoItemsView();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                FeedbackItemsAdapter openItemsAdapter = new FeedbackItemsAdapter(getActivity(), 0, items);
                itemsList.setAdapter(openItemsAdapter);
                checkToggleNoItemsView();
                progressBar.setVisibility(View.GONE);
            }
        }, itemsFilter, 0, 10);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FeedbackItem selectedItem = (FeedbackItem) parent.getItemAtPosition(position);

        this.feedbackInstance.selectItem(selectedItem);
        Intent intent = new Intent(this.getActivity(), ViewFeedbackItemActivity.class);
        this.startActivity(intent);
    }


    private void checkToggleNoItemsView() {
        if (this.itemsList.getAdapter() == null || ((FeedbackItemsAdapter) this.itemsList.getAdapter()).originalSource.size() == 0) {
            this.itemsList.setVisibility(View.GONE);
            this.txtNoItems.setVisibility(View.VISIBLE);
        } else {
            this.itemsList.setVisibility(View.VISIBLE);
            this.txtNoItems.setVisibility(View.GONE);
        }
    }

    public class FeedbackItemsAdapter extends ArrayAdapter<FeedbackItem> {

        private int currentPage = 0;
        private static final int pageSize = 10;
        ArrayList<FeedbackItem> originalSource;
        private boolean showProgress = true;

        public FeedbackItemsAdapter(Context context, int resource, ArrayList<FeedbackItem> items) {
            super(context, resource);
            this.originalSource = items;
        }

        @Override
        public int getCount() {
            if (showProgress) {
                return this.originalSource.size() + 1;
            }
            return this.originalSource.size();
        }

        @Override
        public FeedbackItem getItem(int position) {
            if (position < this.originalSource.size()) {
                return this.originalSource.get(position);
            }

            return new FeedbackItem();
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == this.originalSource.size() && this.showProgress) {
                View progressView = this.getProgressView();
                this.callForMoreItems();
                return progressView;
            }
            View itemView = View.inflate(this.getContext(), R.layout.feedback_item, null);
            FeedbackItem associatedItem = this.getItem(position);
            TextView txtText = (TextView) itemView.findViewById(R.id.txtText);
            txtText.setText(associatedItem.getText());
            TextView txtStatus = (TextView)itemView.findViewById(R.id.txtStatus);
            if (associatedItem.getState().toLowerCase().equals(RadFeedback.STATUS_OPEN)){
                txtStatus.setText(R.string.status_open);
                txtStatus.setTextColor(getResources().getColor(R.color.status_open_color));
            }else{
                txtStatus.setText(R.string.status_resolved);
                txtStatus.setTextColor(getResources().getColor(R.color.status_resolved_color));
            }
            TextView txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtDate.setText(DateParser.getDateFromJSONString(this.getContext(), associatedItem.getCreatedAt()));

            TextView txtCommentsCount = (TextView) itemView.findViewById(R.id.txtCommentsCount);
            int commentsCount = Integer.parseInt(associatedItem.getCommentsCount() != null ? associatedItem.getCommentsCount() : "1") - 1;
            if(commentsCount == 1) {
                txtCommentsCount.setText(String.format(getString(R.string.feedback_activity_single_comment_label_format), 1));
            } else {
                txtCommentsCount.setText(String.format(getString(R.string.feedback_activity_multiple_comments_label_format), commentsCount));
            }

            return itemView;
        }

        private View getProgressView() {
            return View.inflate(this.getContext(), R.layout.list_progress_indicator, null);
        }

        private void callForMoreItems() {
            this.currentPage++;
            feedbackInstance.getCurrentItems(new RadFeedback.OnItemsDeliveredCallback() {
                @Override
                public void onItemsDelivered(Exception e, ArrayList<FeedbackItem> items) {
                    originalSource.addAll(items);
                    if (items.size() < pageSize) {
                        showProgress = false;
                    }
                    notifyDataSetChanged();
                }
            }, itemsFilter, this.currentPage, pageSize);
        }
    }
}
