package pg.gipter.monitor.db;

import com.mongodb.MongoClient;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import pg.gipter.monitor.domain.activeSupports.collections.ActiveSupport;

public class ActiveSupportCodec implements CollectibleCodec<ActiveSupport> {

    private final CodecRegistry registry;
    private final Codec<Document> documentCodec;
    private final MongoConverter<ActiveSupport> converter;

    public ActiveSupportCodec() {
        this.registry = MongoClient.getDefaultCodecRegistry();
        this.documentCodec = this.registry.get(Document.class);
        this.converter = new ActiveSupportConverter();
    }

    public ActiveSupportCodec(Codec<Document> codec) {
        this.documentCodec = codec;
        this.registry = MongoClient.getDefaultCodecRegistry();
        this.converter = new ActiveSupportConverter();
    }

    public ActiveSupportCodec(CodecRegistry registry) {
        this.registry = registry;
        this.documentCodec = this.registry.get(Document.class);
        this.converter = new ActiveSupportConverter();
    }

    @Override
    public ActiveSupport generateIdIfAbsentFromDocument(ActiveSupport activeSupport) {
        return activeSupport;
    }

    @Override
    public boolean documentHasId(ActiveSupport activeSupport) {
        return true;
    }

    @Override
    public BsonValue getDocumentId(ActiveSupport activeSupport) {
        if (!documentHasId(activeSupport)) {
            throw new IllegalStateException("The document does not contain an _id");
        }
        return new BsonString(new ObjectId().toHexString());
    }

    @Override
    public ActiveSupport decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(bsonReader, decoderContext);
        return this.converter.convert(document);
    }

    @Override
    public void encode(BsonWriter bsonWriter, ActiveSupport activeSupport, EncoderContext encoderContext) {
        Document document = this.converter.convert(activeSupport);
        documentCodec.encode(bsonWriter, document, encoderContext);
    }

    @Override
    public Class<ActiveSupport> getEncoderClass() {
        return ActiveSupport.class;
    }
}
