package com.silversea.aem.models.impl.injectors;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.silversea.aem.models.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 3010)
public class AdaptedChildResourceInjector extends AbstractInjector implements Injector, InjectAnnotationProcessorFactory2 {

    static final private Logger LOGGER = LoggerFactory.getLogger(AdaptedChildResourceInjector.class);

    static final private List<Class<?>> allowedTypes = Arrays.asList(
            HotelModel.class,
            LandProgramModel.class,
            ExcursionModel.class,
            SuiteModel.class,
            PublicAreaModel.class,
            DiningModel.class,
            ItineraryModel.class,
            ItineraryExcursionModel.class,
            ItineraryHotelModel.class,
            ItineraryLandProgramModel.class);

    @Override
    public @Nonnull String getName() {
        return "adapted-child-resources";
    }

    @Override
    public Object getValue(@Nonnull Object adaptable, String name, @Nonnull Type declaredType, @Nonnull AnnotatedElement element,
                           @Nonnull DisposalCallbackRegistry callbackRegistry) {
        if (isDeclaredTypeCollection(declaredType)) {
            Class<?> type = getActualType((ParameterizedType) declaredType);

            if (!allowedTypes.contains(type)) {
                return null;
            }
        } else {
            if (!allowedTypes.contains(declaredType)) {
                return null;
            }
        }

        if (adaptable instanceof Resource) {
            Resource child = ((Resource) adaptable).getChild(name);
            if (child != null) {
                return getValue(child, declaredType);
            }
        }

        if (adaptable instanceof Page) {
            final Page page = ((Page) adaptable);

            final PageManager pageManager = page.getPageManager();

            if (pageManager != null) {
                final Page child = pageManager.getPage(page.getPath() + "/" + name);

                if (child != null) {
                    return getValue(child, declaredType);
                }
            }

            final Resource resource = page.adaptTo(Resource.class);
            if (resource != null) {
                final Resource child = resource.getChild(name);

                if (child != null) {
                    return getValue(child, declaredType);
                }
            }
        }

        return null;
    }

    private Object getValue(Resource child, Type declaredType) {
        if (declaredType instanceof Class) {
            return child;
        } else if (isDeclaredTypeCollection(declaredType)) {
            return getResultList(child, declaredType);
        } else {
            return null;
        }
    }

    private Object getValue(Page child, Type declaredType) {
        if (declaredType instanceof Class) {
            return child.adaptTo((Class<?>) declaredType);
        } else if (isDeclaredTypeCollection(declaredType)) {
            return getResultList(child, declaredType);
        } else {
            return null;
        }
    }

    private Object getResultList(Resource resource, Type declaredType) {
        List<Object> result = new ArrayList<>();
        Class<?> type = getActualType((ParameterizedType) declaredType);

        if (type != null && resource != null) {
            Iterator<Resource> children = resource.listChildren();
            while (children.hasNext()) {
                result.add(children.next().adaptTo(type));
            }
        }

        return result;
    }

    private Object getResultList(Page page, Type declaredType) {
        List<Object> result = new ArrayList<>();
        Class<?> type = getActualType((ParameterizedType) declaredType);

        if (type != null && page != null) {
            Iterator<Page> children = page.listChildren();

            while (children.hasNext()) {
                final Page next = children.next();
                result.add(next.adaptTo(type));
            }
        }

        return result;
    }

    private Class<?> getActualType(ParameterizedType declaredType) {
        Type[] types = declaredType.getActualTypeArguments();
        if (types != null && types.length > 0) {
            return (Class<?>) types[0];
        }
        return null;
    }


    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        // check if the element has the expected annotation
        ChildResource annotation = element.getAnnotation(ChildResource.class);
        if (annotation != null) {
            return new AdaptedChildResourceAnnotationProcessor(annotation, adaptable);
        }
        return null;
    }

    private static class AdaptedChildResourceAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final ChildResource annotation;
        private final Object adaptable;

        public AdaptedChildResourceAnnotationProcessor(ChildResource annotation, Object adaptable) {
            this.annotation = annotation;
            this.adaptable = adaptable;
        }

        @Override
        public String getName() {
            // since null is not allowed as default value in annotations, the empty string means,
            // the default should be used!
            if (annotation.name().isEmpty()) {
                return null;
            }
            return annotation.name();
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }

        @Override
        @SuppressWarnings("deprecation")
        public Boolean isOptional() {
            return annotation.optional();
        }

        @Override
        public String getVia() {
            if (StringUtils.isNotBlank(annotation.via())) {
                return annotation.via();
            }
            // automatically go via resource, if this is the httprequest
            if (adaptable instanceof SlingHttpServletRequest) {
                return "resource";
            } else {
                return null;
            }
        }
    }
}
