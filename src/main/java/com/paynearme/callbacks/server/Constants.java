package com.paynearme.callbacks.server;

/**
 * Configuration. Configuration should normally be done externally (properties files, database, etc), but to keep the
 * demonstration application simple it is done here.
 */
public class Constants {
    public static final String PNM_BOOTSTRAP_VERSION="1.0.0";

    public static final String SECRET = "your_secret_key";
    public static final String SITE_IDENTIFIER = "your_site_identifier";
    
    public static final boolean IGNORE_KEY_CHECK = false; // ONLY IF YOU KNOW WHAT YOURE DOING.
}
