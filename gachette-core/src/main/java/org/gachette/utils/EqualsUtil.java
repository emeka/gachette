package org.gachette.utils;

import org.gachette.instrumentation.GachetteProxy;

import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <code>equals</code>.
 * This code does not create new objects as opposite to Apache Commons EqualBuilder.
 * <p/>
 * <p/>
 * Example use case:
 * <pre>
 *  public boolean equals(Object o){
 *    T other = EqualsUtil.init(this,o);
 *    if(other == null){
 *        return false;
 *    }
 *
 *    //collect the contributions of various fields
 *    result = EqualsUtil.isEqual(result, this.fPrimitive, other.fPrimitive);
 *    result = EqualsUtil.isEqual(result, this.fObject, other.fObject);
 *    result = EqualsUtil.isEqual(result, this.fArray, other.fArray);
 *    return result;
 *  }
 * </pre>
 */
public final class EqualsUtil {

    public static boolean isSameClass(Object o, Object other) {
        if (o == other) return true;
        if (other == null) return false;
        if (o.getClass() != other.getClass()) return false;
        return true;
    }

    /**
     * booleans.
     */
    public static boolean equals(boolean previousResult, boolean thisBoolean, boolean otherBoolean) {
        if (previousResult == false) {
            return false;
        }
        return thisBoolean == otherBoolean;
    }

    /**
     * chars.
     */
    public static boolean equals(boolean previousResult, char thisChar, char otherChar) {
        if (previousResult == false) {
            return false;
        }
        return thisChar == otherChar;
    }

    /**
     * ints.
     */
    public static boolean equals(boolean previousResult, int thisInt, int otherInt) {
    /*
    * Implementation Note
    * Note that byte and short are handled by this method, through
    * implicit conversion.
    */
        if (previousResult == false) {
            return false;
        }
        return thisInt == otherInt;
    }

    /**
     * longs.
     */
    public static boolean equals(boolean previousResult, long thisLong, long otherLong) {
        if (previousResult == false) {
            return false;
        }
        return thisLong == otherLong;
    }

    /**
     * floats.
     */
    public static boolean equals(boolean previousResult, float thisFloat, float otherFloat) {
        if (previousResult == false) {
            return false;
        }
        return thisFloat == otherFloat;
    }

    /**
     * doubles.
     */
    public static boolean equals(boolean previousResult, double thisDouble, double otherDouble) {
        if (previousResult == false) {
            return false;
        }
        return thisDouble == otherDouble;
    }

    /**
     * <code>aObject</code> is a possibly-null object field, and possibly an array.
     * <p/>
     * If <code>aObject</code> is an array, then each element may be a primitive
     * or a possibly-null object.
     */
    public static <T> boolean equals(boolean previousResult, Object thisObject, Object otherObject) {
        if (previousResult == false) {
            return false;
        } else if (thisObject == null) {
            return otherObject == null;
        } else if (!isArray(thisObject)) {
            return thisObject.equals(otherObject);
        } else {
            int thisObjectLength = Array.getLength(thisObject);
            if (thisObjectLength != Array.getLength(otherObject)) {
                return false;
            }
            for (int idx = 0; idx < thisObjectLength; ++idx) {
                if (!Array.get(thisObject, idx).equals(Array.get(otherObject, idx))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean same(boolean previousResult, Object thisObject, Object otherObject) {
        if (previousResult == false) {
            return false;
        }

        if(otherObject instanceof GachetteProxy){
            return thisObject == ((GachetteProxy)otherObject).getOriginalObject();
        } else {
            return thisObject == otherObject;
        }
    }

    private static boolean isArray(Object aObject) {
        return aObject.getClass().isArray();
    }
}
