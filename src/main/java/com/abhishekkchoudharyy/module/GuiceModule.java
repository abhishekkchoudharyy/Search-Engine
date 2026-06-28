package com.abhishekkchoudharyy.module;

import com.abhishekkchoudharyy.SearchEngineConfiguration;
import com.abhishekkchoudharyy.config.CrawlingConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import java.lang.module.Configuration;

public class GuiceModule extends AbstractModule {

    private ObjectMapper objectMapper;

    @Provides
    @Singleton
    public SwaggerBundleConfiguration swaggerBundleConfiguration(SearchEngineConfiguration configuration) {
        return configuration.getSwaggerBundleConfiguration();
    }

    @Provides
    @Singleton
    public CrawlingConfig crawlingConfig(SearchEngineConfiguration configuration){
        return configuration.getCrawlingConfig();
    }

}
