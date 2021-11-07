package cn.yubajin.seckill.utils;

import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil extends ReflectionUtils {
    public ReflectUtil() {
    }

    public static Property getProperty(Class<?> propertyType, PropertyDescriptor propertyDescriptor, String propertyName) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        return new Property(propertyType, readMethod, writeMethod, propertyName);
    }

    public static TypeDescriptor getTypeDescriptor(Class<?> propertyType, PropertyDescriptor propertyDescriptor, String propertyName) {
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        Property property = new Property(propertyType, readMethod, writeMethod, propertyName);
        return new TypeDescriptor(property);
    }

    @Nullable
    public static Field getField(Class<?> clazz, String fieldName) {
        while(clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException var3) {
                clazz = clazz.getSuperclass();
            }
        }

        return null;
    }

    @Nullable
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, String fieldName, Class<T> annotationClass) {
        Field field = getField(clazz, fieldName);
        return field == null ? null : field.getAnnotation(annotationClass);
    }
}
