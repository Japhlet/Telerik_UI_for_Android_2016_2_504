package com.telerik.widget.dataform.engine;

import android.util.Log;

import com.telerik.widget.dataform.visualization.core.CommitMode;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.ValidationMode;
import com.telerik.widget.dataform.visualization.editors.DataFormCheckBoxEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormDateEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormDecimalEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormEmailEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormIntegerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormListViewEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormNumberPickerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormPasswordEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormPhoneEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormRadioGroupEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormSeekBarEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormSegmentedEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormSpinnerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormSwitchEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormTextEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormTimeEditor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DataFormMetadata {

    HashMap<String, EntityPropertyMetadata> metadata = new HashMap<>();
    private boolean isReadOnly;
    private CommitMode commitMode;
    private ValidationMode validationMode;

    public DataFormMetadata() {
    }

    public DataFormMetadata(JSONObject jsonMetadata) {
        parseMetadata(jsonMetadata);
    }

    public DataFormMetadata(JSONArray jsonMetadata) {
        parsePropertiesMetadata(jsonMetadata);
    }

    public void putMetadataForProperty(String propertyName, EntityPropertyMetadata entityPropertyMetadata) {
        metadata.put(propertyName, entityPropertyMetadata);
    }

    public EntityPropertyMetadata getMetadataForProperty(String propertyName) {
        if(!metadata.containsKey(propertyName)) {
            return null;
        }
        return metadata.get(propertyName);
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public CommitMode getCommitMode() {
        return commitMode;
    }

    public void setCommitMode(CommitMode commitMode) {
        this.commitMode = commitMode;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public void setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
    }

    private void parseMetadata(JSONObject jsonObject) {

        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            try {
                String key = iter.next();
                switch (key.toLowerCase()) {
                    case "isreadonly":
                    case "readonly":
                        setIsReadOnly(jsonObject.getBoolean(key));
                        break;
                    case "commitmode":
                        String commit = jsonObject.getString(key);
                        CommitMode commitMode = CommitMode.valueOf(commit.toUpperCase());
                        setCommitMode(commitMode);
                        break;
                    case "validationmode":
                        String validation = jsonObject.getString(key);
                        ValidationMode validationMode = ValidationMode.valueOf(validation.toUpperCase());
                        setValidationMode(validationMode);
                        break;
                    case "properties": {
                        parsePropertiesMetadata((JSONArray) jsonObject.get(key));
                    }
                }
            } catch (JSONException e) {

            }
        }

    }

    private void parsePropertiesMetadata(JSONArray jsonArray) {

        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                EntityPropertyMetadata entityPropertyMetadata = new EntityPropertyMetadata();
                String propertyName = null;
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Object value = jsonObject.get(key);
                    if(key.toLowerCase().equals("name") || key.toLowerCase().equals("propertyname")) {
                        propertyName = (String)jsonObject.get(key);
                    } else {
                        applyKeyValuePair(key, value, entityPropertyMetadata);
                    }
                }
                if(propertyName == null) {
                    throw new IllegalArgumentException("Json Objects must contain a property 'name' that holds the property name");
                }
                metadata.put(propertyName, entityPropertyMetadata);
            } catch (JSONException e) {

            }
        }
    }

    private void applyKeyValuePair(String key, Object value, EntityPropertyMetadata entityPropertyMetadata) {
        switch (key.toLowerCase()) {
            case "type":
                break;
            case "displayname":
            case "label":
            case "header":
                entityPropertyMetadata.setHeader((String) value);
                break;
            case "groupname":
            case "group":
                entityPropertyMetadata.setGroupName((String) value);
                break;
            case "index":
            case "position":
                entityPropertyMetadata.setPosition((int) value);
                break;
            case "hidden":
            case "ignore":
            case "skip":
                entityPropertyMetadata.setSkip((boolean) value);
                break;
            case "readonly":
            case "isreadonly":
                entityPropertyMetadata.setReadOnly((boolean) value);
                break;
            case "required":
                entityPropertyMetadata.setRequired((boolean) value);
                break;
            case "errormessage":
            case "negativemessage":
                entityPropertyMetadata.getValidator().setNegativeMessage((String) value);
                break;
            case "successmessage":
            case "positivemessage":
                entityPropertyMetadata.getValidator().setPositiveMessage((String) value);
                break;
            case "hinttext":
            case "hint":
            case "watermark":
            case "placeholder":
                entityPropertyMetadata.setHintText((String) value);
                break;
            case "editor":
            case "editortype":
                String editorClassName = (String) value;
                Class<? extends EntityPropertyEditor> editorClass = editorClassForName(editorClassName);
                entityPropertyMetadata.setEditorType(editorClass);
                break;
            case "editorparams":
            case "editorparameters":
                JSONObject jsonObjectEditorParams = (JSONObject)value;
                HashMap<String, Object> editorParams = parseParams(jsonObjectEditorParams);
                entityPropertyMetadata.setEditorParams(editorParams);
                break;
            case "validator":
            case "validatortype":
                String validatorClassName = (String) value;
                Class<? extends PropertyValidatorBase> validatorClass = validatorClassForName(validatorClassName);
                PropertyValidator validator = null;
                try {
                    validator = validatorClass.getConstructor().newInstance();
                } catch (Exception ex) {

                }
                entityPropertyMetadata.setValidator(validator);
                break;
            case "validators":
                JSONArray jsonValidatorsArray = (JSONArray)value;
                PropertyValidatorSet propertyValidatorSet = parseJsonValidatorsList(jsonValidatorsArray);
                entityPropertyMetadata.setValidator(propertyValidatorSet);
                break;
            case "validatorparams":
            case "validatorparameters":
                JSONObject jsonObjectValidatorParams = (JSONObject)value;
                HashMap<String, Object> validatorParams = parseParams(jsonObjectValidatorParams);
                entityPropertyMetadata.setValidatorParams(validatorParams);
                break;
            case "valuesprovider":
                JSONArray jsonArray = (JSONArray)value;
                ArrayList<Object> values = parseSimpleJsonArray(jsonArray);
                entityPropertyMetadata.setValues(values);
                break;
            case "defaultvalue":
                break;
        }
    }

    private PropertyValidatorSet parseJsonValidatorsList(JSONArray array) {
        ArrayList simpleList = parseSimpleJsonArray(array);
        PropertyValidatorSet validatorSet = new PropertyValidatorSet();
        for(Object item : simpleList) {
            JSONObject jsonValidator = (JSONObject)item;
            PropertyValidatorBase validator = parseValidatorObject(jsonValidator);
            if(validator != null) {
                validatorSet.add(validator);
            }
        }
        return validatorSet;
    }

    private PropertyValidatorBase parseValidatorObject(JSONObject jsonObject) {

        String validatorClassName = null;
        Object validatorParamsJson = null;
        try {
            validatorClassName = jsonObject.getString("name");
            validatorParamsJson = jsonObject.get("params");
        } catch (JSONException e) {
            Log.e("DataForm", "Validator doesn't have a name", e);
        }
        if(validatorClassName == null) {
            return null;
        }
        Class<? extends PropertyValidatorBase> validatorClass = validatorClassForName(validatorClassName);
        PropertyValidatorBase validator = null;
        try {
            validator = validatorClass.getConstructor().newInstance();
        } catch (Exception e) {
            Log.e("DataForm", "Can't create a validator with name " + validatorClassName, e);
        }

        if(validator == null) {
            return null;
        }

        if(validatorParamsJson == null) {
            return validator;
        }

        HashMap<String, Object> validatorParams = parseParams((JSONObject)validatorParamsJson);
        validator.applyParams(validatorParams);

        return validator;
    }

    private ArrayList<Object> parseSimpleJsonArray(JSONArray jsonArray) {
        ArrayList<Object> list = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                Object jsonObject = jsonArray.get(i);
                list.add(jsonObject);
            } catch (JSONException e) {

            }
        }
        return list;
    }

    private HashMap<String, Object> parseParams(JSONObject jsonObject) {
        HashMap<String, Object> params = new HashMap<>();
        try {
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                Object value = jsonObject.get(key);
                params.put(key, value);
            }
        } catch (JSONException e) {
            Log.e("DataForm", "Error loading parameters.", e);
        }
        return params;
    }

    private Class<? extends EntityPropertyEditor> editorClassForName(String className) {
        String lowercaseClass = className.toLowerCase();
        if(lowercaseClass.contains("text")) {
            return DataFormTextEditor.class;
        }
        if(lowercaseClass.contains("email") || lowercaseClass.contains("e-mail")) {
            return DataFormEmailEditor.class;
        }
        if(lowercaseClass.contains("password")) {
            return DataFormPasswordEditor.class;
        }
        if(lowercaseClass.contains("phone")) {
            return DataFormPhoneEditor.class;
        }
        if(lowercaseClass.contains("integer") || lowercaseClass.contains("number")) {
            return DataFormIntegerEditor.class;
        }
        if(lowercaseClass.contains("decimal")) {
            return DataFormDecimalEditor.class;
        }
        if(lowercaseClass.contains("switch")) {
            return DataFormSwitchEditor.class;
        }
        if(lowercaseClass.contains("stepper") || lowercaseClass.contains("numberpicker")) {
            return DataFormNumberPickerEditor.class;
        }
        if(lowercaseClass.contains("slider") || lowercaseClass.contains("seekbar")) {
            return DataFormSeekBarEditor.class;
        }
        if(lowercaseClass.contains("segmented")) {
            return DataFormSegmentedEditor.class;
        }
        if(lowercaseClass.contains("date")) {
            return DataFormDateEditor.class;
        }
        if(lowercaseClass.contains("time")) {
            return DataFormTimeEditor.class;
        }
        if(lowercaseClass.contains("picker") || lowercaseClass.contains("spinner")) {
            return DataFormSpinnerEditor.class;
        }
        if(lowercaseClass.contains("options") || lowercaseClass.contains("list")) {
            return DataFormListViewEditor.class;
        }
        if(lowercaseClass.contains("radio")) {
            return DataFormRadioGroupEditor.class;
        }
        if(lowercaseClass.contains("checkbox")) {
            return DataFormCheckBoxEditor.class;
        }
        throw new IllegalArgumentException("Editor for name " + className + " not found.");
    }

    private Class<? extends PropertyValidatorBase> validatorClassForName(String className) {
        String lowercaseClass = className.toLowerCase();
        if(lowercaseClass.contains("length")) {
            if(lowercaseClass.contains("max")) {
                return MaximumLengthValidator.class;
            } else {
                return MinimumLengthValidator.class;
            }
        }
        if(lowercaseClass.contains("empty")) {
            if(lowercaseClass.contains("no")) {
                return NonEmptyValidator.class;
            }
            return EmptyValidator.class;
        }
        if(lowercaseClass.contains("mail")) {
            return MailValidator.class;
        }
        if(lowercaseClass.contains("phone")) {
            return PhoneValidator.class;
        }
        if(lowercaseClass.contains("range")) {
            return RangeValidator.class;
        }
        throw new IllegalArgumentException("Validator for name " + className + " not found.");
    }
}
