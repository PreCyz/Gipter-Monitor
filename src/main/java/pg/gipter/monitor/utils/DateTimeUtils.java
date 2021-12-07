package pg.gipter.monitor.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {
    public static final DateTimeFormatter MONGO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private static final Map<Integer, DateTimeFormatter> formatterMap = new LinkedHashMap<>();
    static {
        formatterMap.put(1, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S"));
        formatterMap.put(2, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"));
        formatterMap.put(3, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        formatterMap.put(4, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS"));
        formatterMap.put(5, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"));
        formatterMap.put(6, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
        formatterMap.put(7, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"));
        formatterMap.put(8, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS"));
    }

    public static LocalDateTime getLocalDateTime(Document document, String fieldName) {
        String localDateTime = document.getString(fieldName);
        if (localDateTime == null) {
            return null;
        } else {
            int key = localDateTime.substring(localDateTime.lastIndexOf(".") + 1).length();
            return LocalDateTime.from(formatterMap.get(key).parse(localDateTime));
        }
    }
}
