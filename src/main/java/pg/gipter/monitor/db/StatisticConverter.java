package pg.gipter.monitor.db;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import pg.gipter.monitor.statistics.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class StatisticConverter {

    public static final DateTimeFormatter MONGO_FORMATTER_SSSSSSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
    public static final DateTimeFormatter MONGO_FORMATTER_SSSSSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    public static final DateTimeFormatter MONGO_FORMATTER_SSSSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS");
    public static final DateTimeFormatter MONGO_FORMATTER_SSSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS");
    public static final DateTimeFormatter MONGO_FORMATTER_SSS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter MONGO_FORMATTER_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");
    public static final DateTimeFormatter MONGO_FORMATTER_S = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S");

    public Document convert(Statistic statistic) {
        Document document = new Document();
        document.put("_id", statistic.getId());
        document.put("applicationVersion", statistic.getApplicationVersion());
        document.put("firstExecutionDate", MONGO_FORMATTER_SSS.format(statistic.getFirstExecutionDate()));
        document.put("javaVersion", statistic.getJavaVersion());
        document.put("lastExecutionDate", MONGO_FORMATTER_SSS.format(statistic.getLastExecutionDate()));
        document.put("lastFailedDate", MONGO_FORMATTER_SSS.format(statistic.getLastFailedDate()));
        document.put("lastRunType", statistic.getLastRunType().name());
        document.put("lastUpdateStatus", statistic.getLastUpdateStatus().name());
        document.put("lastSuccessDate", MONGO_FORMATTER_SSS.format(statistic.getLastSuccessDate()));
        document.put("username", statistic.getUsername());
        document.put("systemUsers", statistic.getSystemUsers());
        final Map<String, Set<String>> controlSystemMap = statistic.getControlSystemMap()
                .entrySet()
                .stream()
                .collect(toMap(entry -> entry.getKey().name(), Map.Entry::getValue, (v1, v2) -> v1));
        document.put("controlSystemMap", controlSystemMap);

        final ExceptionDetailsConverter converter = new ExceptionDetailsConverter();
        final List<Document> exceptions = statistic.getExceptions().stream().map(converter::convert).collect(toList());
        document.put("exceptions", exceptions);
        return document;
    }

    private LocalDateTime getLocalDateTime(Document document, String fieldName) {
        if (document.getString(fieldName) == null) {
            return null;
        }
        try {
            return LocalDateTime.from(MONGO_FORMATTER_SSS.parse(document.getString(fieldName)));
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse date [{}] with the following formatter [MONGO_FORMATTER_SSS].", document.getString(fieldName));
        }
        try {
            return LocalDateTime.from(MONGO_FORMATTER_SSSSSSS.parse(document.getString(fieldName)));
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse date [{}] with the following formatter [MONGO_FORMATTER_SSSSSSS].", document.getString(fieldName));
        }
        try {
            return LocalDateTime.from(MONGO_FORMATTER_SSSSSS.parse(document.getString(fieldName)));
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse date [{}] with the following formatter [MONGO_FORMATTER_SSSSSS].", document.getString(fieldName));
        }
        try {
            return LocalDateTime.from(MONGO_FORMATTER_SS.parse(document.getString(fieldName)));
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse date [{}] with the following formatter [MONGO_FORMATTER_SS].", document.getString(fieldName));
        }
        try {
            return LocalDateTime.from(MONGO_FORMATTER_SSSSS.parse(document.getString(fieldName)));
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse date [{}] with the following formatter [MONGO_FORMATTER_SSSSS].", document.getString(fieldName));
        }
        try {
            return LocalDateTime.from(MONGO_FORMATTER_SSSS.parse(document.getString(fieldName)));
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse date [{}] with the following formatter [MONGO_FORMATTER_SSSS].", document.getString(fieldName));
        }
        DateTimeParseException exception;
        try {
            return LocalDateTime.from(MONGO_FORMATTER_S.parse(document.getString(fieldName)));
        } catch (DateTimeParseException ex) {
            log.warn("Could not parse date [{}] with the following formatter [MONGO_FORMATTER_S].", document.getString(fieldName));
            throw ex;
        }
    }

    public Statistic convert(Document document) {
        Statistic statistic = new Statistic();
        statistic.setId(document.getObjectId("_id"));
        statistic.setApplicationVersion(document.getString("applicationVersion"));
        statistic.setFirstExecutionDate(getLocalDateTime(document, "firstExecutionDate"));
        statistic.setJavaVersion(document.getString("javaVersion"));
        statistic.setLastExecutionDate(getLocalDateTime(document, "lastExecutionDate"));
        statistic.setLastFailedDate(getLocalDateTime(document, "lastFailedDate"));
        statistic.setLastRunType(RunType.valueOf(document.getString("lastRunType")));
        statistic.setLastUpdateStatus(UploadStatus.valueOf(document.getString("lastUpdateStatus")));
        statistic.setLastSuccessDate(getLocalDateTime(document, "lastSuccessDate"));
        statistic.setUsername(document.getString("username"));
        statistic.setSystemUsers(new LinkedHashSet<>(document.getList("systemUsers", String.class)));
        statistic.setExceptions(getExceptions(document));
        statistic.setControlSystemMap(getVersionControlSystemSetMap(document));

        return statistic;
    }

    private List<ExceptionDetails> getExceptions(Document document) {
        List<Document> exceptions = Optional.ofNullable(document.getList("exceptions", Document.class))
                .orElseGet(ArrayList::new);
        return exceptions.stream()
                .map(d -> new ExceptionDetails(
                                d.getString("errorMsg"),
                                d.getString("cause"),
                                getLocalDateTime(d, "errorDate")
                        )
                )
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private Map<VersionControlSystem, Set<String>> getVersionControlSystemSetMap(Document document) {
        Map<String, Set<String>> controlSystemMap = Optional.ofNullable(document.get("controlSystemMap", Map.class))
                .orElseGet(HashMap::new);
        return controlSystemMap.entrySet()
                .stream()
                .collect(toMap(
                        entry -> VersionControlSystem.valueFor(entry.getKey()),
                        Map.Entry::getValue,
                        (v1, v2) -> v1));
    }

}
