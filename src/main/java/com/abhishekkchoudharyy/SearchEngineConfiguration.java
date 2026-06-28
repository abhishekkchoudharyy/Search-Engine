package com.abhishekkchoudharyy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.abhishekkchoudharyy.config.CrawlingConfig;
import io.dropwizard.core.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchEngineConfiguration extends Configuration {

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    @JsonProperty("CrawlingConfig")
    private CrawlingConfig crawlingConfig;
}
