package com.example.project101;

import java.io.InputStream;
import java.util.Properties;

public class Env {

        private static final Properties properties = new Properties();

        static {
            try (InputStream input = Env.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find database.properties");
                } else {
                    // Load the properties file elements
                    properties.load(input);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public static String get(String key) {
            return properties.getProperty(key);
        }
    }

