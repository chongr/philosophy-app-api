package com.project.philosophy;

import com.project.philosophy.core.PathToPhilosophy;
import com.project.philosophy.db.PathToPhilosophyDAO;
import com.project.philosophy.resources.PathToPhilosophyResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import javax.servlet.FilterRegistration;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.SessionFactory;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;

public class PhilosophyApplication extends Application<PhilosophyConfiguration> {
    public static void main(String[] args) throws Exception {
        new PhilosophyApplication().run(args);
    }

    private final HibernateBundle<PhilosophyConfiguration> hibernateBundle =
        new HibernateBundle<PhilosophyConfiguration>(PathToPhilosophy.class) {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(PhilosophyConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<PhilosophyConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<PhilosophyConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(PhilosophyConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<PhilosophyConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(PhilosophyConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(PhilosophyConfiguration configuration, Environment environment) {
        SessionFactory session = hibernateBundle.getSessionFactory();
        final PathToPhilosophyDAO pathToPhilosophyDAO = new PathToPhilosophyDAO(session);

        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", System.getenv("CORS_ALLOWED_ORIGINS"));
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new PathToPhilosophyResource(pathToPhilosophyDAO));
    }
}
