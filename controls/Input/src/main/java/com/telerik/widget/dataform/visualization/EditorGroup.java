package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

import java.util.ArrayList;
import java.util.List;

public class EditorGroup {
    private static DataFormLayoutManager defaultManager;
    public static DataFormLayoutManager getDefaultLayoutManager(Context context) {
        if(defaultManager == null) {
            defaultManager = new DataFormLinearLayoutManager(context, R.layout.data_form_linear_layout);
        }

        return defaultManager;
    }

    public static void setDefaultLayoutManager(DataFormLayoutManager value) {
        defaultManager = value;
    }

    private Context context;
    private String groupName;
    private List<EntityPropertyViewer> editors = new ArrayList<>();
    private DataFormLayoutManager layoutManager;
    private ViewGroup rootLayout;
    private View headerView;
    private ViewGroup headerContainer;

    public EditorGroup(Context context, String groupName) {
        this(context, groupName, R.layout.data_form_default_group_layout);
    }

    public EditorGroup(Context context, String groupName, int layoutId) {
        this.context = context;
        this.groupName = groupName;
        this.rootLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        this.headerView = rootLayout.findViewById(R.id.data_form_group_header);
        this.headerContainer = (ViewGroup)rootLayout.findViewById(R.id.data_form_group_header_container);
        this.initGroupHeader(headerView, groupName);
    }

    public View rootLayout() {
        return rootLayout;
    }

    public View getHeaderView() {
        return headerView;
    }

    public ViewGroup getHeaderContainer() {
        return headerContainer;
    }

    protected void initGroupHeader(View groupHeader, String groupName) {
        ((TextView)groupHeader).setText(groupName);
    }

    public String name() {
        return this.groupName;
    }

    public List<EntityPropertyViewer> editors() {
        return this.editors;
    }

    protected View getView() {
        DataFormLayoutManager layoutManager = this.resolveLayoutManager();

        ViewGroup rootLayout = layoutManager.arrangeEditors(this.editors);
        ViewGroup editorsContainer = (ViewGroup)this.rootLayout.findViewById(R.id.data_form_editor_group_container);
        editorsContainer.addView(rootLayout);
        return this.rootLayout;
    }

    private DataFormLayoutManager resolveLayoutManager() {
        return this.layoutManager != null ? this.layoutManager : getDefaultLayoutManager(this.context);
    }

    public DataFormLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(DataFormLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
}
