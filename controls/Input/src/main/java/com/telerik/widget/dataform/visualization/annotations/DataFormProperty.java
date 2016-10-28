package com.telerik.widget.dataform.visualization.annotations;

import com.telerik.widget.dataform.engine.EmptyConverter;
import com.telerik.widget.dataform.engine.EmptyValidator;
import com.telerik.widget.dataform.engine.PropertyConverter;
import com.telerik.widget.dataform.engine.PropertyValidator;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataFormProperty {
    String NULL = "This is Null";

    int editorLayout() default 0;
    int coreEditorLayout() default 0;
    int headerLayout() default 0;
    int validationLayout() default 0;

    /**
     * A specific viewer for this property.
     */
    Class<? extends EntityPropertyViewer> viewer() default EntityPropertyViewer.class;

    /**
     * A specific editor for this property.
     */
    Class<? extends EntityPropertyEditor> editor() default EntityPropertyEditor.class;

    DataFormEditorParams editorParams() default @DataFormEditorParams;

    /**
     * The validator type for the property.
     */
    Class<? extends PropertyValidator> validator() default EmptyValidator.class;

    DataFormValidatorParams validatorParams() default @DataFormValidatorParams;

    DataFormValidator[] validators() default {};

    /**
     * The value converter for the property.
     */
    Class<? extends PropertyConverter> converter() default EmptyConverter.class;

    /**
     * Defines the column span of an editor in the default table layout.
     */
    int columnSpan() default 1;

    /**
     * Defines the position of the property editor in a standard {@link com.telerik.widget.dataform.visualization.RadDataForm}
     * layout.
     */
    int index() default -1;

    /**
     * Defines the column of property viewer if visualized in a table layout.
     */
    int columnIndex() default 0;

    /**
     * Defines a group to which the property will belong when visualized in
     * {@link com.telerik.widget.dataform.visualization.RadDataForm}.
     */
    String group() default "DefaultGroup";

    /**
     * Defines a text used to hint for the purpose of a property while it is empty.
     */
    String hint() default "";

    /**
     * Defines whether the property is mandatory.
     * The mandatory indication is only visual, it is the responsibility of
     * the property validator to determine if the property is correctly edited.
     */
    boolean required() default false;

    /**
     * Determines the label that will be displayed above the editor. If not set
     * the label will be the property name;
     */
    String label() default NULL;

    /**
     * Determines whether to display the property as read-only or not.
     */
    boolean readOnly() default false;

    /**
     * If set to true the data form will not display this property.
     */
    boolean skip() default false;
}
