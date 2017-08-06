package com.silversea.aem.models.impl.injectors;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

/**
 * Abstract base class for injectors to consolidate common functionality.
 */
abstract class AbstractInjector {

    protected ResourceResolver getResourceResolver(Object adaptable) {
        ResourceResolver resolver = null;
        if (adaptable instanceof Resource) {
            resolver = ((Resource) adaptable).getResourceResolver();
        } else if (adaptable instanceof SlingHttpServletRequest) {
            resolver = ((SlingHttpServletRequest) adaptable).getResourceResolver();
        }
        return resolver;
    }

    protected ValueMap getValueMap(Object adaptable) {
        if (adaptable instanceof ValueMap) {
            return (ValueMap) adaptable;
        } else if (adaptable instanceof Adaptable) {
            ValueMap map = ((Adaptable) adaptable).adaptTo(ValueMap.class);
            return map;
        } else {
            return null;
        }
    }

    protected boolean isDeclaredTypeCollection(Type declaredType) {
        boolean isCollection = false;
        if (declaredType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) declaredType;
            Class<?> collectionType = (Class<?>) type.getRawType();
            isCollection = collectionType.equals(Collection.class)
                    || collectionType.equals(List.class);
        }
        return isCollection;
    }

}