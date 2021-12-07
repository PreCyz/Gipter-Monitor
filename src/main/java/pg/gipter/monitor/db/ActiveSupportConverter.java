package pg.gipter.monitor.db;

import org.bson.Document;
import pg.gipter.monitor.statistics.collections.ActiveSupport;

import static pg.gipter.monitor.utils.DateTimeUtils.MONGO_FORMATTER;
import static pg.gipter.monitor.utils.DateTimeUtils.getLocalDateTime;

class ActiveSupportConverter implements MongoConverter<ActiveSupport> {

    @Override
    public Document convert(ActiveSupport activeSupport) {
        Document document = new Document();
        //document.put("_id", activeSupport.getId());
        document.put("processed", activeSupport.getProcessed());
        document.put("processDateTime", MONGO_FORMATTER.format(activeSupport.getProcessDateTime()));
        document.put("userProcessor", activeSupport.getUserProcessor());
        document.put("comment", activeSupport.getComment());
        return document;
    }

    @Override
    public ActiveSupport convert(Document document) {
        return new ActiveSupport(
                document.getBoolean("processed"),
                getLocalDateTime(document, "processDateTime"),
                document.getString("userProcessor"),
                document.getString("comment")
        );
    }
}
