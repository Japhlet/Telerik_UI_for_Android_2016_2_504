package com.telerik.widget.dataform.engine;

import android.util.Log;

import com.telerik.widget.dataform.visualization.annotations.DataFormEditorParams;
import com.telerik.widget.dataform.visualization.annotations.DataFormProperty;
import com.telerik.widget.dataform.visualization.annotations.DataFormValidator;
import com.telerik.widget.dataform.visualization.annotations.DataFormValidatorParams;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

import java.util.ArrayList;
import java.util.HashMap;

public class EntityPropertyMetadata {
    private PropertyConverter converter = new EmptyConverter();
    private PropertyValidator validator = new EmptyValidator();
    private HashMap<String, Object> validatorParams = new HashMap<>();
    private ArrayList<Object> values;
    private boolean readOnly;
    private boolean skip;
    private String header;
    private int columnPosition;
    private int columnSpan = 1;
    private int position = -1;
    private String groupName = "DefaultGroup";
    private String hintText = "";
    private boolean required;
    private Class<? extends EntityPropertyEditor> editor = EntityPropertyEditor.class;
    private HashMap<String, Object> editorParams = new HashMap<>();
    private Class<? extends EntityPropertyViewer> viewer = EntityPropertyViewer.class;
    private int editorLayoutId;
    private Object customMetadata;
    private int coreEditorLayout;
    private int headerLayout;
    private int validationLayout;

    public EntityPropertyMetadata() {

    }

    public EntityPropertyMetadata(DataFormProperty dataFormAnnotation) {
        if(dataFormAnnotation != null) {
            try {
                if(dataFormAnnotation.validators().length != 0) {
                    PropertyValidatorSet propertyValidatorSet = makeValidatorSet(dataFormAnnotation.validators());
                    this.setValidator(propertyValidatorSet);
                } else {
                    this.setValidator(dataFormAnnotation.validator().getConstructor().newInstance());
                    this.setValidatorParams(makeValidatorParams(dataFormAnnotation.validatorParams()));
                }
                this.setConverter(dataFormAnnotation.converter().getConstructor().newInstance());
            } catch (Exception ex) {
                throw new Error(ex);
            }

            this.setSkip(dataFormAnnotation.skip());
            if(!dataFormAnnotation.label().equals(DataFormProperty.NULL)) {
                this.setHeader(dataFormAnnotation.label());
            }
            this.setPosition(dataFormAnnotation.index());
            this.setColumnPosition(dataFormAnnotation.columnIndex());
            this.setGroupName(dataFormAnnotation.group());
            this.setHintText(dataFormAnnotation.hint());
            this.setRequired(dataFormAnnotation.required());
            this.setReadOnly(dataFormAnnotation.readOnly());
            this.setEditorType(dataFormAnnotation.editor());
            this.setEditorParams(makeEditorParams(dataFormAnnotation.editorParams()));
            this.setViewerType(dataFormAnnotation.viewer());
            this.setColumnSpan(dataFormAnnotation.columnSpan());
            this.setEditorLayoutId(dataFormAnnotation.editorLayout());
            this.setCoreEditorLayoutId(dataFormAnnotation.coreEditorLayout());
            this.setHeaderLayoutId(dataFormAnnotation.headerLayout());
            this.setValidationLayoutId(dataFormAnnotation.validationLayout());
        }
    }

    private PropertyValidatorSet makeValidatorSet(DataFormValidator[] validators) {
        PropertyValidatorSet propertyValidatorSet = new PropertyValidatorSet();
        for(DataFormValidator validatorAnnotation : validators) {
            try {
                PropertyValidator propertyValidator = validatorAnnotation.type().getConstructor().newInstance();
                if (!(propertyValidator instanceof PropertyValidatorBase)) {
                    throw new IllegalArgumentException("validators in validator set must extend PropertyValidatorBase");
                }
                PropertyValidatorBase validatorBase = (PropertyValidatorBase) propertyValidator;
                validatorBase.applyParams(makeValidatorParams(validatorAnnotation.params()));
                propertyValidatorSet.add(validatorBase);
            } catch (Exception e) {
                Log.e("DataForm", String.format("Can't create an instance of %s.", validatorAnnotation.type().toString()), e);
            }
        }
        return propertyValidatorSet;
    }

    private HashMap<String, Object> makeValidatorParams(DataFormValidatorParams params) {
        HashMap<String, Object> validatorParams = new HashMap<>();
        if(params.max() != DataFormValidatorParams.DEFAULT_MAX) {
            validatorParams.put("max", params.max());
        }
        if(params.min() != DataFormValidatorParams.DEFAULT_MIN) {
            validatorParams.put("min", params.min());
        }
        if(params.length() != DataFormValidatorParams.DEFAULT_LENGTH) {
            validatorParams.put("length", params.length());
        } else if(params.minimumLength() != DataFormValidatorParams.DEFAULT_LENGTH) {
            validatorParams.put("length", params.minimumLength());
        }
        if(!DataFormValidatorParams.NULL.equals(params.positiveMessage())) {
            validatorParams.put("positiveMessage", params.positiveMessage());
        }
        if(!DataFormValidatorParams.NULL.equals(params.negativeMessage())) {
            validatorParams.put("negativeMessage", params.negativeMessage());
        }
        return validatorParams;
    }

    private HashMap<String, Object> makeEditorParams(DataFormEditorParams params) {
        HashMap<String, Object> editorParams = new HashMap<>();
        if(params.max() != DataFormEditorParams.DEFAULT_MAX) {
            editorParams.put("max", params.max());
        }
        if(params.min() != DataFormEditorParams.DEFAULT_MIN) {
            editorParams.put("min", params.min());
        }
        if(params.step() != DataFormEditorParams.DEFAULT_STEP) {
            editorParams.put("step", params.step());
        }
        return editorParams;
    }

    public void setCustomMetadata(Object value) {
        this.customMetadata = value;
    }

    public Object getCustomMetadata() {
        return this.customMetadata;
    }

    public void setReadOnly(boolean value) {
        this.readOnly = value;
    }

    public boolean getReadOnly() {
        return this.readOnly;
    }

    public int getEditorLayoutId() {
        return editorLayoutId;
    }

    public void setEditorLayoutId(int value) {
        this.editorLayoutId = value;
    }

    public int getCoreEditorLayoutId() {
        return coreEditorLayout;
    }

    public void setCoreEditorLayoutId(int value) {
        coreEditorLayout = value;
    }

    public int getHeaderLayoutId() {
        return headerLayout;
    }

    public void setHeaderLayoutId(int value) {
        headerLayout = value;
    }

    public int getValidationLayoutId() {
        return validationLayout;
    }

    public void setValidationLayoutId(int value) {
        validationLayout = value;
    }

    public Class<? extends EntityPropertyViewer> getViewerType() {
        return viewer;
    }

    public void setViewerType(Class<? extends EntityPropertyViewer> value) {
        this.viewer = value;
    }

    public Class<? extends EntityPropertyEditor> getEditorType() {
        return this.editor;
    }

    public void setEditorType(Class<? extends EntityPropertyEditor> value) {
        this.editor = value;
    }

    public HashMap<String, Object> getEditorParams() {
        return editorParams;
    }

    public void setEditorParams(HashMap<String, Object> editorParams) {
        this.editorParams = editorParams;
    }

    public HashMap<String, Object> getValidatorParams() {
        return validatorParams;
    }

    public void setValidatorParams(HashMap<String, Object> validatorParams) {
        this.validatorParams = validatorParams;
    }

    public ArrayList<Object> getValues() {
        return values;
    }

    public void setValues(ArrayList<Object> values) {
        this.values = values;
    }

    public boolean getSkip() {
        return skip;
    }

    public void setSkip(boolean value) {
        skip = value;
    }

    public void setColumnPosition(int value) {
        columnPosition = value;
    }

    public int getColumnPosition() {
        return columnPosition;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int value) {
        columnSpan = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int value) {
        position = value;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String value) {
        header = value;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String value) {
        groupName = value;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean value) {
        required = value;
    }

    public void setConverter(PropertyConverter converter) {
        this.converter = converter;
    }

    public PropertyConverter getConverter() {
        return this.converter;
    }

    public void setValidator(PropertyValidator validator) {
        this.validator = validator;
    }

    public PropertyValidator getValidator() {
        return this.validator;
    }
}
