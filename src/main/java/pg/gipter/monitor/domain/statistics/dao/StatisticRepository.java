package pg.gipter.monitor.domain.statistics.dao;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import pg.gipter.monitor.db.MongoDaoConfig;
import pg.gipter.monitor.domain.statistics.collections.*;
import pg.gipter.monitor.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static pg.gipter.monitor.utils.DateTimeUtils.MONGO_FORMATTER;

@Slf4j
class StatisticRepository extends MongoDaoConfig implements StatisticDao {

    StatisticRepository() {
        super(Statistic.COLLECTION_NAME);
    }

    @Override
    public Collection<Statistic> findAllByLastFailedDateAfter(LocalDateTime localDateTime) {
        FindIterable<Statistic> failedStatistics = collection.find(
                Filters.and(
                        Filters.eq("lastUpdateStatus", UploadStatus.FAIL.name()),
                        Filters.gte("lastFailedDate", localDateTime.format(MONGO_FORMATTER))
                ),
                Statistic.class
        );
        final Collection<Statistic> statistics = new LinkedList<>();
        try (MongoCursor<Statistic> cursor = failedStatistics.cursor()) {
            cursor.forEachRemaining(statistics::add);
        }
        return statistics;
    }

    @Override
    public void saveProcessed(String statisticId, ExceptionDetails exceptionDetails, String processId) {
        DateTimeFormatter dateTimeFormatter = DateTimeUtils.getFormatter(exceptionDetails.getErrorDate());
        String errorDateStr = exceptionDetails.getErrorDate().format(dateTimeFormatter);

        Map<String, String> map = new HashMap<>();
        map.put("exceptions.$.processId", processId);

        UpdateResult updateResult = collection.updateOne(
                Filters.and(
                        Filters.eq("_id", new ObjectId(statisticId)),
                        Filters.elemMatch("exceptions",
                                Filters.and(
                                        Filters.eq("cause", exceptionDetails.getCause()),
                                        Filters.eq("errorDate", errorDateStr)
                                )
                        )
                ),
                new BasicDBObject().append("$set", Document.parse(new Gson().toJson(map)))
        );

        log.info("Records found: [{}].", updateResult.getMatchedCount());
        log.info("Records updated: [{}].", updateResult.getModifiedCount());
    }
}
