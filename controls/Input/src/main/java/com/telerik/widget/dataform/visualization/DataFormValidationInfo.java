package com.telerik.widget.dataform.visualization;

import com.telerik.widget.dataform.engine.ValidationInfo;

import java.util.ArrayList;
import java.util.List;

public class DataFormValidationInfo {
    private ArrayList<ValidationInfo> failedValidationInfos;

    public DataFormValidationInfo(List<ValidationInfo> editorValidationInfos) {
        failedValidationInfos = new ArrayList<>();
        for(ValidationInfo info : editorValidationInfos) {
            if(!info.isValid()) {
                failedValidationInfos.add(info);
            }
        }
    }

    public List<ValidationInfo> failedValidationInfos() {
        return failedValidationInfos;
    }

    public boolean hasErrors() {
        return failedValidationInfos.size() > 0;
    }
}
