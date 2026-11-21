package com.wscicloud.service.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import com.wscicloud.common.enumeration.ConstanteEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesInitializer implements EnvironmentPostProcessor {

    private static final String PROPERTIES_NAME = "BianaCloudProperties";
    private static final String PARAMETRE_QUERY_SQL =
            "SELECT id, code, valeur_defaut, valeur_nouvelle FROM sys_parametre";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        Objects.requireNonNull(environment);

        try {
            DataSource dataSource = DataSourceBuilder.create()
                    .url(environment.getProperty("spring.datasource.jdbcUrl"))
                    .username(environment.getProperty("spring.datasource.username"))
                    .password(environment.getProperty("spring.datasource.password"))
                    .driverClassName(environment.getProperty("spring.datasource.driverClassName"))
                    .build();

            Map<String, Object> propertiesMap = this.getProperties(dataSource);

            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTIES_NAME, propertiesMap));

            this.updateParametre(environment.getPropertySources(), propertiesMap);

        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void updateParametre(MutablePropertySources propertySources, Map<String, Object> propertiesMap) {

        MapPropertySource target = null;

        if (propertySources.contains(PROPERTIES_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTIES_NAME);

            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;

                for (Map.Entry<String, Object> entry : propertiesMap.entrySet()) {
                    String key = entry.getKey();
                    if (target.containsProperty(key)) {
                        target.getSource().put(key, propertiesMap.get(key));
                    }
                }
            }
        }
    }

    private Map<String, Object> getProperties(DataSource dataSource) {
        Map<String, Object> propertiesMap = new HashMap<>();

        try (PreparedStatement preparedStmt = dataSource.getConnection().prepareStatement(PARAMETRE_QUERY_SQL)) {

            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                final String code = rs.getString("code");
                final String defaultValue = rs.getString("valeur_defaut");
                final String newValue = rs.getString("valeur_nouvelle");

                String valeur = StringUtils.isNotBlank(newValue) ? newValue : defaultValue;

                valeur = ConstanteEnum.AUCUN.getValeur().equals(valeur) ? null : valeur;

                propertiesMap.put(code, valeur);
            }

        } catch (Exception e) {
            log.error("", e);
        }
        return propertiesMap;
    }
}
