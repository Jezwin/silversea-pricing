package com.silversea.aem.helper;

import com.silversea.aem.components.editorial.AbstractSilverUse;
import com.silversea.aem.components.editorial.DeviceProperty;

public class DevicePropertyHelper extends AbstractSilverUse {
    private DeviceProperty<String> deviceProperty;
    private DeviceProperty<?>[] list;
    private String[] keyList;

    @Override
    public void activate() throws Exception {
        deviceProperty = new DeviceProperty<>(get("desktop", Object.class), get("tablet", Object.class), get("mobile", Object.class)).map((key, value) -> value != null ? value.toString() : null);

    }

    public DeviceProperty<String> getDeviceProperty() {
        return deviceProperty;
    }

}
