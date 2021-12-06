package pg.gipter.monitor.db;

import org.bson.Document;
import pg.gipter.monitor.statistics.ExceptionDetails;

import java.time.LocalDateTime;

import static pg.gipter.monitor.db.StatisticConverter.MONGO_FORMATTER_SSS;

public class ExceptionDetailsConverter {

    public Document convert(ExceptionDetails exceptionDetails) {
        Document document = new Document();
        //document.put("_id", exceptionDetails.getId());
        document.put("cause", exceptionDetails.getCause());
        document.put("errorDate", MONGO_FORMATTER_SSS.format(exceptionDetails.getErrorDate()));
        document.put("errorMsg", exceptionDetails.getErrorMsg());
        return document;
    }

    public ExceptionDetails convert(Document document) {
        ExceptionDetails exceptionDetails = new ExceptionDetails();
        //exceptionDetails.setId(document.getObjectId("_id"));
        exceptionDetails.setCause(document.getString("cause"));
        exceptionDetails.setErrorDate(LocalDateTime.from(MONGO_FORMATTER_SSS.parse(document.getString("errorDate"))));
        exceptionDetails.setErrorMsg(document.getString("errorMsg"));
        return exceptionDetails;
    }
}
