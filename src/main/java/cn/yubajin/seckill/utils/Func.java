package cn.yubajin.seckill.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class Func {
    public Func() {
    }


    public static boolean isEmpty(@Nullable Object obj) {
        return ObjectUtils.isEmpty(obj);
    }

    public static boolean isNotEmpty(@Nullable Object obj) {
        return !ObjectUtils.isEmpty(obj);
    }

    public static boolean isEmpty(@Nullable Object[] array) {
        return ObjectUtils.isEmpty(array);
    }

    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !ObjectUtils.isEmpty(array);
    }

    public static boolean hasEmpty(Object... os) {
        Object[] var1 = os;
        int var2 = os.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Object o = var1[var3];
            if (isEmpty(o)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAllEmpty(Object... os) {
        Object[] var1 = os;
        int var2 = os.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Object o = var1[var3];
            if (isNotEmpty(o)) {
                return false;
            }
        }

        return true;
    }

}

