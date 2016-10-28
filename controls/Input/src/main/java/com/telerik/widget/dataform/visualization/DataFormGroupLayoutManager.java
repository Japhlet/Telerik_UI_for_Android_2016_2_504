package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.telerik.android.common.Function2;
import com.telerik.android.common.Procedure;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DataFormGroupLayoutManager extends DataFormLayoutManager {
    private Function2<Context, String, EditorGroup> createGroup;
    private Procedure<List<EditorGroup>> sortGroups;
    private Procedure<EditorGroup> editorGroupCustomizations;

    public DataFormGroupLayoutManager(Context context) {
        this(context, R.layout.data_form_group_layout);
    }

    public DataFormGroupLayoutManager(Context context, int layout) {
        super(context, layout);
    }

    public Function2<Context, String, EditorGroup> getCreateGroup() {
        return this.createGroup;
    }

    public void setCreateGroup(Function2<Context, String, EditorGroup> value) {
        this.createGroup = value;
    }

    public Procedure<List<EditorGroup>> getSortGroups() {
        return sortGroups;
    }

    public void setSortGroups(Procedure<List<EditorGroup>> value) {
        this.sortGroups = value;
    }

    public Procedure<EditorGroup> getEditorGroupCustomizations() {
        return editorGroupCustomizations;
    }

    public void setEditorGroupCustomizations(Procedure<EditorGroup> editorGroupCustomizations) {
        this.editorGroupCustomizations = editorGroupCustomizations;
    }

    @Override
    protected void arrangeEditorsCore(Iterable<EntityPropertyViewer> editors, ViewGroup root) {
        List<EditorGroup> groups = groupEditors(editors);

        if(sortGroups != null) {
            sortGroups.apply(groups);
        }

        arrangeGroups(groups, root);
    }

    protected void arrangeGroups(Collection<EditorGroup> groups, ViewGroup rootLayout) {
        for(EditorGroup group : groups) {
            rootLayout.addView(group.getView(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    protected List<EditorGroup> groupEditors(Iterable<EntityPropertyViewer> editors) {
        HashMap<String, EditorGroup> groups = new LinkedHashMap<>();
        for(EntityPropertyViewer editor : editors) {
            String groupName = editor.property().getGroupName();
            if(groups.containsKey(groupName)) {
                groups.get(groupName).editors().add(editor);
            } else {
                EditorGroup group = this.createEditorGroup(groupName);
                if(group == null) {
                    throw new IllegalStateException("The createGroup callback can not return null.");
                }
                group.editors().add(editor);
                if(getEditorGroupCustomizations() != null) {
                    getEditorGroupCustomizations().apply(group);
                }
                groups.put(groupName, group);
            }
        }

        ArrayList<EditorGroup> result = new ArrayList<>();
        for(EditorGroup group : groups.values()) {
            result.add(group);
        }
        return result;
    }

    protected EditorGroup createEditorGroup(String name) {
        if(createGroup != null) {
            EditorGroup result = createGroup.apply(context, name);
            if(result != null) {
                return result;
            }
        }

        return new EditorGroup(context, name);
    }
}
