package com.jerry.mekextras.common.util;

import java.lang.reflect.Method;

/**
 * Compatibility wrapper for Mekanism's optional SecondaryFuelUsage helper.
 *
 * Some 1.12.2 Mekanism builds do not ship that class, while newer builds do.
 * Keeping the lookup reflective prevents the factory tile entities from
 * linking the missing class when they are placed in an older installation.
 */
public final class ExtraSecondaryFuelUsage {

    private static final String CLASS_NAME = "mekanism.common.recipe.cache.SecondaryFuelUsage";

    private ExtraSecondaryFuelUsage() {
    }

    public static boolean isRandomized(Object tile) {
        Object value = invoke("isRandomized", tile);
        return value instanceof Boolean && (Boolean) value;
    }

    public static long constantUse(long totalUsage, long usedSoFar, int ticksRequired, int operatingTicks) {
        Object value = invoke("constantUse", totalUsage, usedSoFar, ticksRequired, operatingTicks);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (operatingTicks <= 0 || ticksRequired <= 0 || totalUsage <= 0) {
            return 0;
        }
        long before = (totalUsage * usedSoFar) / ticksRequired;
        long after = (totalUsage * (usedSoFar + operatingTicks)) / ticksRequired;
        return Math.max(0, after - before);
    }

    public static int fixedTotal(Object tile, int baseTotal) {
        Object value = invoke("fixedTotal", tile, baseTotal);
        return value instanceof Number ? ((Number) value).intValue() : baseTotal;
    }

    private static Object invoke(String name, Object... args) {
        try {
            Class<?> helper = Class.forName(CLASS_NAME, false, ExtraSecondaryFuelUsage.class.getClassLoader());
            for (Method method : helper.getMethods()) {
                if (!method.getName().equals(name) || method.getParameterTypes().length != args.length) {
                    continue;
                }
                return method.invoke(null, args);
            }
        } catch (Throwable ignored) {
            // The helper is absent in older Mekanism builds; use the fallback above.
        }
        return null;
    }
}
