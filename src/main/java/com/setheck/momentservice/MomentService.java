package com.setheck.momentservice;

import com.setheck.momentservice.api.*;
import com.setheck.momentservice.db.*;
import com.setheck.momentservice.resources.*;
import io.dropwizard.*;
import io.dropwizard.db.*;
import io.dropwizard.hibernate.*;
import io.dropwizard.migrations.*;
import io.dropwizard.setup.*;
import org.eclipse.jetty.servlets.*;
import org.slf4j.*;

import javax.servlet.*;
import java.util.*;

public class MomentService extends Application<MomentServiceConfiguration>
{
    private static final Logger log = LoggerFactory.getLogger(MomentService.class);

    private final HibernateBundle<MomentServiceConfiguration> hibernateBundle = new HibernateBundle<MomentServiceConfiguration>(Moment.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(MomentServiceConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(String[] args) throws Exception {
        new MomentService().run(args);
    }

    @Override
    public String getName() {
        return "MomentService";
    }


    @Override
    public void initialize(Bootstrap<MomentServiceConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MigrationsBundle<MomentServiceConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(MomentServiceConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(MomentServiceConfiguration configuration, Environment environment) {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        final MomentDAO dao = new MomentDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new MomentResource(dao));
    }
}
