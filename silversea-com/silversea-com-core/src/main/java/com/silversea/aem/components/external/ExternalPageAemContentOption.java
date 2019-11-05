package com.silversea.aem.components.external;

public enum ExternalPageAemContentOption {
    // If using external page - don't render the old AEM content
    RemoveAemContent,
    // Render AEM content in a hidden div and provide a JS function `showAemFallbackContent` to show it if needed
    RenderAsFallback
}
