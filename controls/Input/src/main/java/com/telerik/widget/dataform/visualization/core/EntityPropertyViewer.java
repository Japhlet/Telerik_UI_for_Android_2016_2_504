package com.telerik.widget.dataform.visualization.core;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.telerik.android.common.Util;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

import java.util.HashMap;

public abstract class EntityPropertyViewer {
    protected EntityProperty associatedProperty;
    protected View editorView;
    protected View headerView;
    protected View rootLayout;

    protected ViewGroup editorContainer;
    protected ViewGroup headerContainer;

    protected RadDataForm dataForm;

    protected int editorLayoutId;
    protected int headerLayoutId;

    protected int headerViewId;
    protected int editorViewId;

    /**
     * Creates a new instance of EntityPropertyViewer.
     * @param dataForm The data form for this editor.
     * @param layoutId The view layout id.
     * @param headerViewId The id of the header view.
     * @param editorViewId The id of the actual viewer view.
     * @param property Te associated property.
     */
    public EntityPropertyViewer(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, EntityProperty property) {
        this.dataForm = dataForm;
        if(property.getEditorLayoutId() != 0) {
            layoutId = property.getEditorLayoutId();
        }

        if(property.getCoreEditorLayoutId() != 0) {
            editorLayoutId = property.getCoreEditorLayoutId();
        }

        if(property.getHeaderLayoutId() != 0) {
            headerLayoutId = property.getHeaderLayoutId();
        }

        this.editorLayoutId = editorLayoutId;
        this.headerLayoutId = headerLayoutId;

        this.associatedProperty = property;
        Context context = dataForm.getContext();
        this.rootLayout = LayoutInflater.from(context).inflate(layoutId, dataForm, false);

        View editorView = LayoutInflater.from(context).inflate(editorLayoutId, dataForm, false);
        editorContainer = Util.getLayoutPart(this.rootLayout, R.id.data_form_editor_container, ViewGroup.class);
        editorContainer.addView(editorView);

        View headerView = LayoutInflater.from(context).inflate(headerLayoutId, dataForm, false);
        headerContainer = Util.getLayoutPart(this.rootLayout, R.id.data_form_header_container, ViewGroup.class);
        headerContainer.addView(headerView);

        this.headerViewId = headerViewId;
        this.headerView = rootLayout.findViewById(headerViewId);
        this.initHeader(this.headerView, property);

        this.editorViewId = editorViewId;
        this.editorView = rootLayout.findViewById(editorViewId);
        this.initEditor(this.editorView, property);
    }

    public void setEditorLayout(int value) {
        this.editorLayoutId = value;
        this.editorContainer.removeView(this.editorView);
        View editorView = LayoutInflater.from(this.dataForm.getContext()).inflate(value, dataForm, false);
        editorContainer.addView(editorView);

        this.editorView = rootLayout.findViewById(editorViewId);
        this.initEditor(this.editorView, this.property());
    }

    public void setHeaderLayout(int value) {
        this.headerLayoutId = value;
        this.headerContainer.removeView(this.headerView);
        View headerView = LayoutInflater.from(this.dataForm.getContext()).inflate(value, dataForm, false);
        headerContainer.addView(headerView);

        this.headerView = rootLayout.findViewById(headerViewId);
        this.initHeader(this.headerView, this.property());
    }

    public View rootLayout() {
        return this.rootLayout;
    }

    protected void initHeader(View headerView, EntityProperty property) {
        String header = property.getHeader();

        if((headerView instanceof TextView) && header != null) {
            TextView textView = (TextView) headerView;
            HeaderTextWatcher headerTextWatcher = new HeaderTextWatcher(textView);
            textView.addTextChangedListener(headerTextWatcher);
            textView.setText(header);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editorView instanceof EditText) {
                        EditText editText = (EditText)editorView;
                        editText.requestFocus();
                        if(editText.getText() != null) {
                            int textLength = editText.getText().length();
                            editText.setSelection(textLength, textLength);
                        }
                    }
                }
            });
        }

        headerView.setId(Util.generateViewId());
    }

    protected void initEditor(View editor, EntityProperty property) {
        editor.setId(Util.generateViewId());
    }

    public View getEditorView() {
        return editorView;
    }

    @SuppressWarnings("unused")
    public View getHeaderView() {
        return headerView;
    }

    /**
     * Gets the property associated with this viewer.
     */
    public EntityProperty property(){
        return this.associatedProperty;
    }

    /**
     * Initializes the editor with the associated property.
     */
    public void load(){
    }

    /**
     * Frees the viewer from the associated property.
     */
    public void unload(){
    }

    public void applyParams(HashMap<String, Object> params) {
    }

    class HeaderTextWatcher implements TextWatcher {
        private TextView textView;

        HeaderTextWatcher(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(this.textView.getText() == null || this.textView.getText().length() == 0) {
                this.textView.setVisibility(View.GONE);
            } else {
                this.textView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
