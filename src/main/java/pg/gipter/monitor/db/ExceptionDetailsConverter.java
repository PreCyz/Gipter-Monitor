package pg.gipter.monitor.db;

import org.bson.Document;
import pg.gipter.monitor.statistics.collections.ExceptionDetails;

import java.util.Optional;

import static pg.gipter.monitor.utils.DateTimeUtils.MONGO_FORMATTER;
import static pg.gipter.monitor.utils.DateTimeUtils.getLocalDateTime;

class ExceptionDetailsConverter implements MongoConverter<ExceptionDetails> {

    @Override
    public Document convert(ExceptionDetails exceptionDetails) {
        Document document = new Document();
        //document.put("_id", exceptionDetails.getId());
        document.put("cause", exceptionDetails.getCause());
        document.put("errorDate", MONGO_FORMATTER.format(exceptionDetails.getErrorDate()));
        document.put("errorMsg", exceptionDetails.getErrorMsg());
        document.put("activeSupport", exceptionDetails.getActiveSupport());
        return document;
    }

    @Override
    public ExceptionDetails convert(Document document) {
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        //exceptionDetails.setId(document.getObjectId("_id"));
        exceptionDetails.setCause(document.getString("cause"));
        exceptionDetails.setErrorDate(getLocalDateTime(document, "errorDate"));
        exceptionDetails.setErrorMsg(document.getString("errorMsg"));
        Optional.ofNullable(document.get("activeSupport", Document.class))
                .ifPresent(d -> exceptionDetails.setActiveSupport(
                        new ActiveSupportConverter().convert(document.get("activeSupport", Document.class))
                ));
        return exceptionDetails;
    }
}
