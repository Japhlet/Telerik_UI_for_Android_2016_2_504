package com.telerik.widget.dataform.engine;

public interface EntityPropertyCommitListener {
    boolean onBeforeCommit(EntityProperty property);
    void onAfterCommit(EntityProperty property);
}
