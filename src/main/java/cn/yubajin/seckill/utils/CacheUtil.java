package cn.yubajin.seckill.utils;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

public class CacheUtil {
    private static CacheManager cacheManager;
    private static final Boolean TENANT_MODE;

    public CacheUtil() {
    }

    private static CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = (CacheManager)SpringUtils.getBean(CacheManager.class);
        }

        return cacheManager;
    }

    public static Cache getCache(String cacheName) {
        return getCacheManager().getCache(cacheName);
    }

    @Nullable
    public static Object get(String cacheName, String keyPrefix, Object key) {
        if (Func.hasEmpty(new Object[]{cacheName, keyPrefix, key})) {
            return null;
        } else {
            ValueWrapper valueWrapper = getCache(cacheName).get(keyPrefix.concat(String.valueOf(key)));
            return Func.isEmpty(valueWrapper) ? null : valueWrapper.get();
        }
    }

    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, @Nullable Class<T> type) {
        return Func.hasEmpty(new Object[]{cacheName, keyPrefix, key}) ? null : getCache(cacheName).get(keyPrefix.concat(String.valueOf(key)), type);
    }

    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, Callable<T> valueLoader) {
        if (Func.hasEmpty(new Object[]{cacheName, keyPrefix, key})) {
            return null;
        } else {
            try {
                ValueWrapper valueWrapper = getCache(cacheName).get(keyPrefix.concat(String.valueOf(key)));
                Object value = null;
                if (valueWrapper == null) {
                    T call = valueLoader.call();
                    if (!ObjectUtils.isEmpty(call)) {
                        Field field = ReflectUtil.getField(call.getClass(), "id");
                        if (!ObjectUtils.isEmpty(field) && ObjectUtils.isEmpty(ClassUtils.getMethod(call.getClass(), "getId", new Class[0]).invoke(call))) {
                            return null;
                        }

                        getCache(cacheName).put(keyPrefix.concat(String.valueOf(key)), call);
                        value = call;
                    }
                } else {
                    value = valueWrapper.get();
                }
                return (T)value;
            } catch (Exception var9) {
                var9.printStackTrace();
                return null;
            }
        }
    }

    public static void put(String cacheName, String keyPrefix, Object key, @Nullable Object value) {
        getCache(cacheName).put(keyPrefix.concat(String.valueOf(key)), value);
    }

    public static void evict(String cacheName, String keyPrefix, Object key) {
        if (!Func.hasEmpty(new Object[]{cacheName, keyPrefix, key})) {
            getCache(cacheName).evict(keyPrefix.concat(String.valueOf(key)));
        }
    }

    public static void clear(String cacheName) {
        if (!Func.isEmpty(cacheName)) {
            getCache(cacheName).clear();
        }
    }

    static {
        TENANT_MODE = Boolean.TRUE;
    }
}

