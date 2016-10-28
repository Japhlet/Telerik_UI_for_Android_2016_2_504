package com.telerik.widget.dataform.engine;

import com.telerik.widget.dataform.visualization.annotations.DataFormProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * This class represents a combination of a get and a set methods which together define a property.
 */
public class EntityPropertyBase extends EntityPropertyCore {

    private boolean hasSetter;

    /**
     * Creates an instance of the {@link EntityProperty} class with the provided arguments.
     *
     * @param name  the name of the property.
     * @param type  the type of the property
     * @param owner the owner {@link Entity} to which the property belongs.
     */
    public EntityPropertyBase(String name, Class type, Entity owner) {
        super(name, type, owner);

        Object source = owner.getSourceObject();
        if(source instanceof NotifyPropertyChanged) {
            NotifyPropertyChanged propertyNotifier = (NotifyPropertyChanged)source;
            propertyNotifier.addPropertyChangedListener(this);
        }

        this.readMetadata(name, owner);
    }

    protected void readMetadata(String name, Entity owner) {
        Class ownerType = owner.getSourceObject().getClass();

        Method getter = findMethod("get", name, ownerType);
        Method setter = findMethod("set", name, ownerType);

        if(getter == null) {
            throw new IllegalArgumentException(String.format("Property with name %s must have at least a getter method.", name));
        }

        if(setter != null) {
            hasSetter = true;
        }

        DataFormProperty dataFormAnnotation = findMetadata(getter);
        EntityPropertyMetadata entityPropertyMetadata = new EntityPropertyMetadata(dataFormAnnotation);
        readMetadata(entityPropertyMetadata);
    }

    protected DataFormProperty findMetadata(Method getter) {
        for(Annotation annotation : getter.getAnnotations()) {
            if(annotation instanceof DataFormProperty) {
                return (DataFormProperty)annotation;
            }
        }

        return null;
    }

    protected Method findMethod(String prefix, String name, Class ownerType) {
        Method[] methods = ownerType.getMethods();
        for(Method method : methods) {
            if(method.getName().equals(prefix + name)) {
                return method;
            }
        }

        return null;
    }

    @Override
    public boolean getReadOnly() {
        return super.getReadOnly() || !this.hasSetter;
    }
}