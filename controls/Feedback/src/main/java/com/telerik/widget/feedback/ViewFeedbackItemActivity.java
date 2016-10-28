package com.telerik.widget.feedback;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * This activity visualizes a single Feedback item submitted by the user.
 * Comments can be read and submitted.
 */
public class ViewFeedbackItemActivity extends ActionBarActivity {

    private LinearLayout commentsView;
    private ImageView itemImage;
    private RadFeedback feedback;
    private TextView txtDate;
    private TextView txtStatus;
    private TextView feedbackText;
    private TextView commentsLabel;
    private EditText txtEditComment;
    private Button btnCommentDone;
    private Integer submitTaskId = null;
    private Integer getCommentsTaskId = null;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_view_feedback_item);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);
        this.getSupportActionBar().setLogo(new ColorDrawable());

        this.feedback = RadFeedback.instance();
        this.txtEditComment = (EditText) this.findViewById(R.id.txtEditComment);
        this.txtStatus = (TextView) this.findViewById(R.id.txtStatus);
        this.commentsView = (LinearLayout) this.findViewById(R.id.listComments);
        this.txtDate = (TextView) this.findViewById(R.id.txtDate);
        this.feedbackText = (TextView) this.findViewById(R.id.txtFeedbackText);
        this.commentsLabel = (TextView) this.findViewById(R.id.txtCommentsLabel);
        this.btnCommentDone = (Button) this.findViewById(R.id.btnCommentDone);
        this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        this.btnCommentDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEditComment.getText().length() == 0) {
                    Toast.makeText(ViewFeedbackItemActivity.this, getString(R.string.feedback_add_comment_reminder), Toast.LENGTH_SHORT).show();
                    return;
                }
                btnCommentDone.setEnabled(false);
                txtEditComment.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtEditComment.getWindowToken(), 0);
                final FeedbackItem item = new FeedbackItem();
                item.setText(txtEditComment.getText().toString());
                txtEditComment.setText(null);
                if (feedback.getCurrentAuthorName() != null) {
                    item.setAuthor(feedback.getCurrentAuthorName());
                }
                final ArrayList<FeedbackItem> itemsToSubmit = new ArrayList<FeedbackItem>();
                itemsToSubmit.add(item);

                submitTaskId = feedback.submitFeedback(itemsToSubmit, feedback.selectedItem().getId(), new RadFeedback.OnSubmitFeedbackFinishedCallback() {
                    @Override
                    public void onSubmitFinished(Exception e) {

                        if (e == null) {
                            for (FeedbackItem itemToAdd : itemsToSubmit) {
                                commentsView.addView(createViewForComment(itemToAdd));
                            }
                            Toast.makeText(ViewFeedbackItemActivity.this, getString(R.string.feedback_comment_sent), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewFeedbackItemActivity.this, getString(R.string.feedback_comment_not_sent), Toast.LENGTH_SHORT).show();
                        }

                        int commentsCount = commentsView.getChildCount();
                        if(commentsCount == 1) {
                            commentsLabel.setText(String.format(getString(R.string.feedback_single_comment_label_format), 1));
                        } else {
                            commentsLabel.setText(String.format(getString(R.string.feedback_multiple_comments_label_format), commentsCount));
                        }
                        btnCommentDone.setEnabled(true);
                        txtEditComment.setEnabled(true);
                        submitTaskId = null;
                    }
                });
            }
        });

        this.itemImage = (ImageView) this.findViewById(R.id.itemImage);

        AsyncTask downloadImageTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                URL imageURL = null;
                InputStream imageStream = null;
                try {
                    imageURL = new URL(feedback.selectedItem().getImage().getUri());
                    imageStream = imageURL.openStream();
                    Drawable remoteImageDrawable = Drawable.createFromStream(imageStream, "src");
                    imageStream.close();
                    return remoteImageDrawable;

                } catch (Exception e) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o == null) {
                    Toast.makeText(ViewFeedbackItemActivity.this, getString(R.string.feedback_screenshot_load_error), Toast.LENGTH_SHORT).show();
                } else {
                    itemImage.setImageDrawable((Drawable) o);
                }

                progressBar.setVisibility(View.GONE);
            }
        };

        downloadImageTask.execute(null, true);
        FeedbackItem item = this.feedback.selectedItem();

        this.txtDate.setText(DateParser.getDateFromJSONString(this, item.getCreatedAt()));
        if (item.getState().toLowerCase().equals(RadFeedback.STATUS_OPEN)) {
            this.txtStatus.setText(R.string.status_open);
            this.txtStatus.setTextColor(getResources().getColor(R.color.status_open_color));
        } else {
            this.txtStatus.setText(R.string.status_resolved);
            this.txtStatus.setTextColor(getResources().getColor(R.color.status_resolved_color));
        }

        this.feedbackText.setText(item.getText());
        this.getCommentsTaskId = this.feedback.getCommentsForItem(this.feedback.selectedItem(), new RadFeedback.OnItemsDeliveredCallback() {
                    @Override
                    public void onItemsDelivered(Exception e, ArrayList<FeedbackItem> items) {
                        getCommentsTaskId = null;
                        if (e != null) {
                            Toast.makeText(ViewFeedbackItemActivity.this, getString(R.string.feedback_comments_not_retrieved), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (FeedbackItem item : items) {
                            commentsView.addView(createViewForComment(item), 0);
                        }
                        if(items.size() == 1) {
                            commentsLabel.setText(String.format(getString(R.string.feedback_single_comment_label_format), 1));
                        } else {
                            commentsLabel.setText(String.format(getString(R.string.feedback_multiple_comments_label_format), items.size()));
                        }
                    }
                }
        );

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.getCommentsTaskId != null) {
            feedback.cancelTask(this.getCommentsTaskId);
        }

        if (this.submitTaskId != null) {
            feedback.cancelTask(this.submitTaskId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View createViewForComment(FeedbackItem comment) {
        View view = View.inflate(this, R.layout.feedback_comment, null);
        TextView commentText = (TextView) view.findViewById(R.id.txtCommentText);
        commentText.setText(comment.getText());
        TextView authorText = (TextView) view.findViewById(R.id.txtAuthor);

        if (comment.getAuthor() != null && !comment.getAuthor().trim().isEmpty()) {
            authorText.setText(comment.getAuthor());
        }

        TextView dateText = (TextView) view.findViewById(R.id.txtDate);
        dateText.setText(DateParser.getDateFromJSONString(this, comment.getCreatedAt()));

        return view;
    }
}
