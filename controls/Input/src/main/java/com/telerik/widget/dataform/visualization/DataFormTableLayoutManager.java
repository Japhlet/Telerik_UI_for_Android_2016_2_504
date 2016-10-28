package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DataFormTableLayoutManager extends DataFormLayoutManager {
    private HashMap<Integer, ArrayList<EntityPropertyViewer>> grid = new LinkedHashMap<>();

    public DataFormTableLayoutManager(Context context, int layout) {
        super(context, layout);
    }

    public void unload() {
        grid.clear();
    }

    protected void arrangeEditorsCore(Iterable<EntityPropertyViewer> editors, ViewGroup rootLayout) {
        TableLayout layout = (TableLayout)rootLayout;

        int defaultRowCounter = 0;
        for(EntityPropertyViewer editor : editors) {
            Integer rowIndex = editor.property().getPosition();
            if(rowIndex < 0) {
                rowIndex = defaultRowCounter++;
            }
            if(grid.containsKey(rowIndex)) {
                grid.get(rowIndex).add(editor);
            } else {
                ArrayList<EntityPropertyViewer> columns = new ArrayList<>();
                columns.add(editor);
                grid.put(rowIndex, columns);
            }
        }

        int maxColumnCount = 1;
        for(Integer rowIndex : grid.keySet()) {
            ArrayList<EntityPropertyViewer> columns = grid.get(rowIndex);
            Collections.sort(columns, new Comparator<EntityPropertyViewer>() {
                @Override
                public int compare(EntityPropertyViewer lhs, EntityPropertyViewer rhs) {
                    Integer left = lhs.property().getColumnPosition();

                    return left.compareTo(rhs.property().getColumnPosition());
                }
            });

            TableRow row = new TableRow(context);
            for(EntityPropertyViewer editor : columns) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                params.span = editor.property().getColumnSpan();
                params.column = editor.property().getColumnPosition();
                row.addView(editor.rootLayout(), params);
            }

            layout.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if(columns.size() > maxColumnCount) {
                maxColumnCount = columns.size();
            }
        }

        for(int i = 0; i < maxColumnCount; ++i) {
            layout.setColumnStretchable(i, true);
        }
    }
}
