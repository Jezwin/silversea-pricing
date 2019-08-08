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
%><%@page import="java.util.Iterator"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.common.reflect.TypeToken"%>
<%@page import="java.util.Map"%>
<%@page session="false"
          import="java.lang.reflect.Array,
                  java.util.HashMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.ComponentHelper,
                  com.adobe.granite.ui.components.ComponentHelper.Options,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Tag,
                  com.adobe.granite.ui.components.Value" %><%
Config cfg = cmp.getConfig();
Tag tag = cmp.consumeTag();
AttrBuilder attrs = tag.getAttrs();

String fieldLabel = cfg.get("fieldLabel", String.class);
Resource field = cfg.getChild("field");
Resource items = field.getChild("items");
Config fieldCfg = new Config(field);
String name = fieldCfg.get("name", String.class);
Object values = cmp.getValue().getContentValue(name, null); // don't convert; pass null for type

Object[] array = new Object[0];
if (values != null) {
    if (values.getClass().isArray()) {
        
        array = (Object[]) values;
    } else {
        array = (Object[]) Array.newInstance(values.getClass(), 1);
        array[0] = values;
    }
}
if (cmp.getOptions().rootField()) {
    attrs.addClass("coral-Form-fieldwrapper");
    %><div <%= attrs.build() %>><%
        if (fieldLabel != null) {
            %><label class="coral-Form-fieldlabel"><%= outVar(xssAPI, i18n, fieldLabel) %></label><%
        }
    
        %><ol class="coral-Form-field coral-List coral-List--minimal"><%
            for (int i = 0; i < array.length; i++) {
                Map<String, String> retMap = new Gson().fromJson(array[i].toString(), new TypeToken<HashMap<String, Object>>() {}.getType());%>
                <li class="coral-Multifield coral-List-item">
                    <%
                    Iterator<Resource> it = items.listChildren();
                    String childNodeName;
                    Resource childNode = null;

                    // iterate on fields
                    while(it.hasNext()){
                       childNode = it.next();
                       childNodeName = childNode.getValueMap().get("name").toString(); %>
                       <label class="coral-Form-fieldlabel"><%=childNode.getValueMap().get("fieldLabel")%></label><%
                       // Populate fields
                        include(childNode, cmp.getReadOnlyResourceType(childNode), childNodeName, retMap.get(childNodeName), cmp, request);
                    } %>
                </li><%
            }
        %></ol>
    </div><%
}
%><%!
private void include(Resource field, String resourceType, String name, Object value, ComponentHelper cmp, HttpServletRequest request) throws Exception {
    ValueMap map = new ValueMapDecorator(new HashMap<String, Object>());
    map.put(name, value);
    
    ValueMap existing = (ValueMap) request.getAttribute(Value.FORM_VALUESS_ATTRIBUTE);
    request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, map);
    
    cmp.include(field, resourceType, new Options().rootField(false));
    
    request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, existing);
}
%>
