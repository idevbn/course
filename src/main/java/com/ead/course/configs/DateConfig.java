package com.ead.course.configs;

import com.ead.course.models.CourseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.format.DateTimeFormatter;

/**
 * Classe de configuração global para a formatação UTC.
 *
 * Observação: Essa classe será comentada por padrão, e
 * só será aplicada corretamente no endpoint que exibe
 * um curso pelo id.
 * Devido às configurações de especificação e paginação,
 * na classe ResolverConfig (ver o vídeo a partir do minuto 15)
 * é necessário comentar o @Configuration em {@link ResolverConfig}
 * e comentar as annotation de @JsonFormat em {@link CourseModel}.
 *
 * A formatação UTC ficará a nível de atributo, no @JsonFormat.
 *
 * Observação 2: A configuração pode ser encontrada também em:
 * <a href="https://www.baeldung.com/spring-boot-formatting-json-dates">...</a>
 */
//@Configuration
public class DateConfig {

    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final LocalDateTimeSerializer LOCAL_DATE_TIME_SERIALIZER = new LocalDateTimeSerializer(
            DateTimeFormatter.ofPattern(DATETIME_FORMAT)
    );

    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        final JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATE_TIME_SERIALIZER);

        final ObjectMapper registeredModule = new ObjectMapper().registerModule(module);

        return registeredModule;
    }

}
