package com.example.helloworld;

import com.codahale.metrics.MetricRegistry;
import com.example.helloworld.core.Template;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.util.Duration;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class HelloWorldConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldConfiguration.class);
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @Valid
    @NotNull
    private PooledDataSourceFactory database = new PooledDataSourceFactory(){
    
        @Override
        public boolean isAutoCommentsEnabled() {
            return false;
        }
    
        @Override
        public Optional<Duration> getValidationQueryTimeout() {
            return null;
        }
    
        @Override
        public String getValidationQuery() {
            return null;
        }
    
        @Override
        public String getUrl() {
            return null;
        }
    
        @Override
        public Map<String, String> getProperties() {
            return null;
        }
    
        @Override
        public Optional<Duration> getHealthCheckValidationTimeout() {
            return null;
        }
    
        @Override
        public String getHealthCheckValidationQuery() {
            return null;
        }
    
        @Override
        public String getDriverClass() {
            return null;
        }
    
        @Override
        public ManagedDataSource build(MetricRegistry metricRegistry, String name) {
            return null;
        }
    
        @Override
        public void asSingleConnectionPool() {
            
        }
    };

    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public Template buildTemplate() {
        return new Template(template, defaultName);
    }

    @JsonProperty("database")
    public PooledDataSourceFactory getDataSourceFactory() {
        LOGGER.info("getting data source");
        LOGGER.info(System.getenv("ENVIRONMENT"));
        if ((System.getenv("ENVIRONMENT") != null) && System.getenv("ENVIRONMENT").equals("PRODUCTION")) {
            // Use Heroku Env to override db url in production
            LOGGER.info("Dropwizard dummy DB URL (will be overridden)=" + database.getUrl());
            DatabaseConfiguration databaseConfiguration = ExampleDatabaseConfiguration
                    .create(System.getenv("DATABASE_URL"));
            database = databaseConfiguration.getDataSourceFactory(null);
            LOGGER.info("Heroku DB URL=" + database.getUrl());   
        }
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    @JsonProperty("viewRendererConfiguration")
    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration;
    }

    @JsonProperty("viewRendererConfiguration")
    public void setViewRendererConfiguration(Map<String, Map<String, String>> viewRendererConfiguration) {
        final ImmutableMap.Builder<String, Map<String, String>> builder = ImmutableMap.builder();
        for (Map.Entry<String, Map<String, String>> entry : viewRendererConfiguration.entrySet()) {
            builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
        }
        this.viewRendererConfiguration = builder.build();
    }
}
