package com.I4U.mukpic.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    private static Dotenv dotenv;

    // Load .env file and set system properties
    public static void loadEnv() {
        dotenv = Dotenv.configure().load();

        // Set environment variables
        System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
        System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DB_DRIVER", dotenv.get("DB_DRIVER"));
    }

    // Optional: Get specific environment variable
    public static String getEnv(String key) {
        return dotenv.get(key);
    }
}
