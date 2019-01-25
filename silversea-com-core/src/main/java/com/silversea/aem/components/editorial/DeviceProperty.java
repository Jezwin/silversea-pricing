package com.silversea.aem.components.editorial;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeviceProperty<T> {

    private final T desktop;
    private final T tablet;
    private final T mobile;

    private ValueMap map;

    public DeviceProperty(T desktop, T tablet, T mobile) {
        this.desktop = desktop;
        this.tablet = tablet == null ? desktop : tablet;
        this.mobile = mobile == null ? this.tablet : mobile;
    }

    public T getMobile() {
        return mobile;
    }

    public T getTablet() {
        return tablet;
    }

    public T getDesktop() {
        return desktop;
    }

    public T get(String device) {
        switch (device) {
            case AbstractSilverUse.TABLET:
                return getTablet();
            case AbstractSilverUse.MOBILE:
                return getMobile();
            default:
                return getDesktop();

        }
    }

    public ValueMap toValueMap(String property) {
        if (map != null) {
            return map;
        }
        map = new ValueMapDecorator(new HashMap<>());
        map.put(property + "Desktop", desktop);
        map.put(property + "Tablet", tablet);
        map.put(property + "Mobile", mobile);
        return map;
    }

    /**
     * @param mapFunction A function giving a new value using as parameters ["Desktop", "Tablet", "Mobile"] and the current value;
     * @return A new device property.
     */
    public <P> DeviceProperty<P> map(BiFunction<String, T, P> mapFunction) {
        return new DeviceProperty<>(mapFunction.apply(AbstractSilverUse.DESKTOP, desktop), mapFunction.apply(AbstractSilverUse.TABLET, tablet), mapFunction.apply(AbstractSilverUse.MOBILE, mobile));
    }

    @Override
    public String toString() {
        return Stream.of(desktop, tablet, mobile).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(" "));
    }


}
