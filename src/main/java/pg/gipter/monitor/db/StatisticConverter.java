package pg.gipter.monitor.db;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import pg.gipter.monitor.statistics.collections.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static pg.gipter.monitor.utils.DateTimeUtils.MONGO_FORMATTER;
import static pg.gipter.monitor.utils.DateTimeUtils.getLocalDateTime;

@Slf4j
class StatisticConverter implements MongoConverter<Statistic> {

    @Override
    public Document convert(Statistic statistic) {
        Document document = new Document();
        document.put("_id", statistic.getId());
        document.put("applicationVersion", statistic.getApplicationVersion());
        document.put("firstExecutionDate", MONGO_FORMATTER.format(statistic.getFirstExecutionDate()));
        document.put("javaVersion", statistic.getJavaVersion());
        document.put("lastExecutionDate", MONGO_FORMATTER.format(statistic.getLastExecutionDate()));
        document.put("lastFailedDate", MONGO_FORMATTER.format(statistic.getLastFailedDate()));
        document.put("lastRunType", statistic.getLastRunType().name());
        document.put("lastUpdateStatus", statistic.getLastUpdateStatus().name());
        document.put("lastSuccessDate", MONGO_FORMATTER.format(statistic.getLastSuccessDate()));
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

    @Override
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
                .map(d -> new ExceptionDetailsConverter().convert(d))
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
