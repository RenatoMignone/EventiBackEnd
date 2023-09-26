package it.Repositories.ConfigABS;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Collections.singletonList;


@Configuration
@EnableMongoRepositories(basePackages = "it.Repositories.db.Utente", mongoTemplateRef = "securedbMongoTemplate")
@EnableConfigurationProperties
public class dbSecureUtenteConfigurator {

    @Bean(name = "securedbProperties")
    @ConfigurationProperties(prefix = "securedb.utenti")
    public MongoProperties primaryProperties() {
        return new MongoProperties();
    }

    @Bean(name = "secureMongoClient")
    public MongoClient mongoClient(@Qualifier("securedbProperties") MongoProperties mongoProperties) {

        MongoCredential credential = MongoCredential.createCredential(mongoProperties.getUsername(), mongoProperties.getAuthenticationDatabase(), mongoProperties.getPassword());

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(singletonList(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()))))
                .credential(credential)
                .build());
    }

    @Primary
    @Bean(name = "secureMongoDBFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier("secureMongoClient") MongoClient mongoClient, @Qualifier("securedbProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Primary
    @Bean(name = "securedbMongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("secureMongoDBFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
