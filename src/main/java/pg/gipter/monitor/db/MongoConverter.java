package pg.gipter.monitor.db;

import org.bson.Document;

public interface MongoConverter<T> {
    Document convert(T type);
    T convert(Document document);
}
