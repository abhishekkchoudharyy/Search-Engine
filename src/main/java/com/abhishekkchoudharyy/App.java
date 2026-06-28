package com.abhishekkchoudharyy;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.abhishekkchoudharyy.module.GuiceModule;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class App extends Application<SearchEngineConfiguration> {

    public static void main(final String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "Search_Engine";
    }

    @Override
    public void initialize(final Bootstrap<SearchEngineConfiguration> bootstrap) {
        bootstrap.addBundle(guiceBundle(createGuiceModule(new ObjectMapper())));
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(SearchEngineConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

    }

    @Override
    public void run(final SearchEngineConfiguration orbitConfiguration,
                    final Environment environment) {
        // TODO: implement application
    }

    private GuiceModule createGuiceModule(ObjectMapper objectMapper) {
        return new GuiceModule(objectMapper);
    }

    GuiceBundle guiceBundle(Module... modules) {
        return GuiceBundle.builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(modules).build(Stage.DEVELOPMENT);
    }
}
