package com.mss301.kraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class KraftApplication {

    public static void main(String[] args) {
        // Set default timezone to Vietnam (GMT+7)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        // Set default encoding to UTF-8 for Vietnamese characters
        System.setProperty("file.encoding", StandardCharsets.UTF_8.name());

        SpringApplication.run(KraftApplication.class, args);
    }

    /**
     * Configure default locale for Vietnamese language support
     * This helps with date formatting, currency, and text processing
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("vi", "VN"));
        return localeResolver;
    }
}
