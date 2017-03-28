<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/apps/silversea/silversea-com/ui/global.jsp" %><%
%><%@page session="false"
          import="java.util.Collection,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ComponentHelper.Options,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag" %><%--###
Field
=====

.. granite:servercomponent:: /libs/granite/ui/components/foundation/form/field

   Field is an abstract component that is extended by the actual field implementation, such as :granite:servercomponent:`textfield </libs/granite/ui/components/foundation/form/textfield>`.
   It is created to make creating a form field easier. It handles:
   
   * field label and field description
   * read-only mode
   * root field (``com.adobe.granite.ui.components.ComponentHelper.Options#rootField``)

  
   **Life Cycle**
   
   It will call the following scripts in order:
   
   init.jsp
      This script needs to set a ValueMap to the request attribute, like so ``request.setAttribute(com.adobe.granite.ui.components.Field.class.getName(), vm);``.
      The value map needs to contain at least property named ``value`` that is storing the value of the field.
      The implementor is free to put any property to this map, though it is encouraged to namespace the property name to avoid future conflict.
      By default Field is giving default implementation of init.jsp, setting the value to of ``value`` property of type ``StringEL``.
      This map is also available during the calling of ``render.jsp``.
    
   render.jsp
      This is where the actual rendering of the actual field is performed.
  
  
   **Read-only Mode**
   
   This feature is enabled by setting ``renderReadOnly`` property to true.
   The read-only version of the component is rendered by including the read-only resource type.
   Consult ``com.adobe.granite.ui.components.ComponentHelper#getReadOnlyResourceType`` for details.
   If read-only resource type is not available, no read-only version is rendered.
   The value set during ``init.jsp`` will be inspected to check if it is empty. If it is empty the read-only mode will be hidden, unless ``showEmptyInReadOnly`` property is set to true in the content.
  
   
   **Content Structure**

   .. gnd:gnd::
   
      [granite:FormField]
      
      /**
       * The label of the component.
       */
      - fieldLabel (String) i18n
      
      /**
       * The description of the component.
       */
      - fieldDescription (String) i18n
      
      /**
       * Renders the read-only markup as well.
       */
      - renderReadOnly (Boolean)
      
      /**
       * Shows read-only version even when it has empty value.
       */
      - showEmptyInReadOnly (Boolean)

      /**
       * Renders the whole field hidden on page load.
       */
      - renderHidden (Boolean)
###--%><%
try {
    Config cfg = cmp.getConfig();
    Field field = new Field(cfg);

    %>
    <ui:includeClientLib categories="framework.ui.multifields" />
    <sling:call script="init.jsp" /><%
    
    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());

    String fieldLabel = cfg.get("fieldLabel", String.class);
    String fieldDesc = cfg.get("fieldDescription", String.class);
    
    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();

    // Attribute builder for form field wrapper
    AttrBuilder wrapperAttr = new AttrBuilder(request, xssAPI);
    wrapperAttr.addClass("coral-Form-fieldwrapper");
    if (cfg.get("renderHidden", false)) {
        wrapperAttr.addBoolean("hidden", true);
    }

    if (cfg.get("renderReadOnly", false)) {
        Object value = vm.get("value");
        boolean isEmpty = false;
        
        if (value == null) {
            isEmpty = true;
        } else if (value.getClass().isArray()) {
            Object[] values = ((Object[]) value);
            if (values.length == 0) {
                isEmpty = true;
            } else if (values.length == 1) {
                value = values[0];
                if (value == null) {
                    isEmpty = true;
                } else if (value.toString().length() == 0) {
                    isEmpty = true;
                }
            }
        } else if (value instanceof Collection) {
            isEmpty = ((Collection) value).isEmpty();
        } else {
            isEmpty = value.toString().length() == 0;
        }

        boolean isMixed = field.isMixed(cmp.getValue());
        
        String rootClass = Field.getRootClass(cfg, isEmpty || isMixed);
        
        attrs.addClass("coral-Form-field");
        
        %><div class="foundation-field-editable <%= rootClass %>"><%
            String readOnlyRT = cmp.getReadOnlyResourceType();
            if (readOnlyRT != null) {
                AttrBuilder a = new AttrBuilder(request, xssAPI);
                a.addClass("foundation-field-readonly");
                cmp.include(resource, readOnlyRT, new Tag(a));
            }
            wrapperAttr.addClass("foundation-field-edit");
                %><div <%= wrapperAttr.build() %>><%
                if (fieldLabel != null) {
                    %><label class="coral-Form-fieldlabel"><%= outVar(xssAPI, i18n, fieldLabel) %></label><%
                }

                cmp.call("render.jsp", new Options().tag(tag));

                if (fieldDesc != null) {
                    %><span class="coral-Form-fieldinfo coral-Icon coral-Icon--infoCircle coral-Icon--sizeS" data-init="quicktip" data-quicktip-type="info" data-quicktip-arrow="right" data-quicktip-content="<%= xssAPI.encodeForHTMLAttr(outVar(xssAPI, i18n, fieldDesc)) %>"></span><%
                }
            %></div>
        </div><%
    } else {
        if (cmp.getOptions().rootField()) {
            attrs.addClass("coral-Form-field");
            
            %><div <%= wrapperAttr.build() %>><%

            if (fieldLabel != null) {
                %><label class="coral-Form-fieldlabel"><%= outVar(xssAPI, i18n, fieldLabel) %></label><%
            }
        }
        
        cmp.call("render.jsp", new Options().tag(tag));
        
        if (cmp.getOptions().rootField()) {
            if (fieldDesc != null) {
                %><span class="coral-Form-fieldinfo coral-Icon coral-Icon--infoCircle coral-Icon--sizeS" data-init="quicktip" data-quicktip-type="info" data-quicktip-arrow="right" data-quicktip-content="<%= xssAPI.encodeForHTMLAttr(outVar(xssAPI, i18n, fieldDesc)) %>"></span><%
            }
            %></div><%
        }
    }
} finally {
    request.removeAttribute(Field.class.getName());
}
%>