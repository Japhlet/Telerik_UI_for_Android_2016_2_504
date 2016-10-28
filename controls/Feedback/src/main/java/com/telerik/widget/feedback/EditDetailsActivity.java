package com.telerik.widget.feedback;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class EditDetailsActivity extends ActionBarActivity {

    private RadFeedback feedback = RadFeedback.instance();
    private EditText txtAuthor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);
        this.txtAuthor = (EditText) this.findViewById(R.id.txtAuthorName);
        this.txtAuthor.setText(feedback.getCurrentAuthorName());
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);
        this.getSupportActionBar().setLogo(new ColorDrawable());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.txtAuthor.getText() != null && !this.txtAuthor.getText().toString().trim().isEmpty()) {
            feedback.setCurrentAuthorName(this.txtAuthor.getText().toString().trim());
        }
        feedback.storeShouldShowPrefPrompt(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RadFeedback.instance().onSendFeedbackFinished();
    }
}
