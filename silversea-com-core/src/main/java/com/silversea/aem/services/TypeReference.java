package com.silversea.aem.services;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> implements Comparable<TypeReference<T>>, Serializable {
    final Class<T> type;

    protected TypeReference() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
        type = extractClassFromType(superclass.getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    private Class<T> extractClassFromType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<T>) type;
        }
        return (Class<T>) ((ParameterizedType) type).getRawType();
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public int compareTo(TypeReference<T> other) {
        return 0;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof TypeReference;
    }

    @Override
    public int hashCode() {
        return getType() != null ? getType().hashCode() : 0;

    }

}
