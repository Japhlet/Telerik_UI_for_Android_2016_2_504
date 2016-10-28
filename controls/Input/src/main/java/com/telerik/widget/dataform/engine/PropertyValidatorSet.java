package com.telerik.widget.dataform.engine;

import java.util.ArrayList;

/**
 * Validator set that allows you to use more than one validator for each property of RadDataForm.
 */
public class PropertyValidatorSet extends PropertyValidatorBase {

    private ArrayList<PropertyValidatorBase> validators = new ArrayList<>();

    public ArrayList<PropertyValidatorBase> validators() {
        return validators;
    }

    public void add(PropertyValidatorBase validator) {
        validators.add(validator);
    }

    public void remove(PropertyValidatorBase validator) {
        validators.remove(validator);
    }

    public void clear() {
        validators.clear();
    }

    @Override
    protected boolean validateCore(Object input, String propertyName) {
        for (PropertyValidatorBase validator : validators) {
            if(!validator.validateCore(input, propertyName)) {
                setNegativeMessage(validator.getNegativeMessage());
                return false;
            }
        }
        return true;
    }
}
