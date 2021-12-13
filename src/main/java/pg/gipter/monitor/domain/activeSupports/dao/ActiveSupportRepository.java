package pg.gipter.monitor.domain.activeSupports.dao;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import pg.gipter.monitor.db.ActiveSupportConverter;
import pg.gipter.monitor.db.MongoDaoConfig;
import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
class ActiveSupportRepository extends MongoDaoConfig implements ActiveSupportDao {

    protected ActiveSupportRepository() {
        super(ActiveSupport.COLLECTION_NAME);
    }

    @Override
    public ActiveSupport upsert(ActiveSupport activeSupport) {
        ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);
        UpdateResult updateResult = collection.replaceOne(
                Filters.eq("_id", activeSupport.getId()),
                new ActiveSupportConverter().convert(activeSupport),
                replaceOptions
        );

        log.info("ActiveSupport upserted: matchCount: {}, modifiedCount: {}, upsertId: {}.",
                updateResult.getMatchedCount(),
                updateResult.getModifiedCount(),
                updateResult.getUpsertedId()
        );
        return activeSupport;
    }

    @Override
    public List<ActiveSupport> saveAll(List<ActiveSupport> activeSupports) {
        ActiveSupportConverter converter = new ActiveSupportConverter();
        List<Document> documents = activeSupports.stream().map(converter::convert).collect(toList());
        collection.insertMany(documents);
        return activeSupports;
    }
}
