package ru.yandex.yandexlavka.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;


@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("create_tables.sql");
        String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        jdbcTemplate.execute(sql);
    }
}
