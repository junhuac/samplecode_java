package com.paynearme.callbacks.enums;

/**
* Created by ryan on 11/20/13.
*/
public enum Version {
    v1_7("1.7"),
    v1_8("1.8"),
    v2_0("2.0"),
    unknown("unknown");

    public final String value;

    Version(String str) {
        this.value = str;
    }

    /** Get the version enum value from a string */
    public static Version getVersionFromString(String str) {
        for (Version v: Version.values()) {
            if (v.value.equals(str)) return v;
        }
        return unknown;
    }
}
