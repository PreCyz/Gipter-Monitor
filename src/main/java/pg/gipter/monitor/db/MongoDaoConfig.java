package pg.gipter.monitor.db;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;
import pg.gipter.monitor.domain.statistics.collections.ExceptionDetails;
import pg.gipter.monitor.domain.statistics.collections.Statistic;
import pg.gipter.monitor.utils.CryptoUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

public abstract class MongoDaoConfig {

    protected final Logger logger;

    private static final String DB_CONFIG = "db.properties";

    private final String collectionName;
    protected MongoCollection<Document> collection;

    private boolean statisticsAvailable;

    protected MongoDaoConfig(String collectionName) {
        logger = LoggerFactory.getLogger(getClass());
        this.collectionName = collectionName;
        init(loadProperties().orElseGet(() -> MongoConfig.dbProperties));
    }

    private Optional<Properties> loadProperties() {
        Properties properties;

        try (InputStream fis = new FileInputStream(DB_CONFIG);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)
        ) {
            properties = new Properties();
            properties.load(reader);
        } catch (IOException | NullPointerException e) {
            logger.warn("Could not load [{}] because: {}", DB_CONFIG, e.getMessage());
            properties = null;
        }
        return Optional.ofNullable(properties);
    }

    private void init(Properties dbConfig) {
        try {
            CodecRegistry codecRegistry = MongoClient.getDefaultCodecRegistry();
            Codec<Document> documentCodec = codecRegistry.get(Document.class);
            Codec<Statistic> statisticCodec = new StatisticCodec(codecRegistry);
            Codec<ExceptionDetails> exceptionDetailsCodec = new ExceptionDetailsCodec(codecRegistry);
            Codec<ActiveSupport> activeSupportCodec = new ActiveSupportCodec(codecRegistry);
            codecRegistry = CodecRegistries.fromRegistries(
                    MongoClient.getDefaultCodecRegistry(),
                    CodecRegistries.fromCodecs(
                            documentCodec,
                            statisticCodec,
                            exceptionDetailsCodec,
                            activeSupportCodec
                    )
            );

            String host = dbConfig.getProperty("db.host");
            String username = dbConfig.getProperty("db.username");
            String password = CryptoUtils.decrypt(dbConfig.getProperty("db.password"));
            String databaseName = dbConfig.getProperty("db.dbName");

            MongoClientOptions.Builder mongoClientOptionsBuilder = MongoClientOptions.builder()
                    .writeConcern(WriteConcern.ACKNOWLEDGED)
                    .codecRegistry(codecRegistry);
            String uri = String.format("mongodb+srv://%s:%s@%s", username, password, host);
            MongoClientURI mongoClientURI = new MongoClientURI(uri, mongoClientOptionsBuilder);
            MongoClient mongoClient = new MongoClient(mongoClientURI);

            collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            statisticsAvailable = true;

            logger.debug("Connection to the database established. [host: {}, databaseName: {}]", host, databaseName);
        } catch (Exception ex) {
            logger.error("Can not establish connection to the database.");
            statisticsAvailable = false;
        }
    }

    public boolean isStatisticsAvailable() {
        return statisticsAvailable;
    }

}
