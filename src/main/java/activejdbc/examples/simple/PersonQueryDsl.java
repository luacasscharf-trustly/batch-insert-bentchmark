package activejdbc.examples.simple;

import javax.annotation.processing.Generated;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import static com.mysema.query.types.PathMetadataFactory.forVariable;

import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class PersonQueryDsl extends com.mysema.query.sql.RelationalPathBase<PersonQueryDsl> {
    public NumberPath <Integer> id = createNumber("id", Integer.class);
    public StringPath name = createString("name");

    public PersonQueryDsl(Class<? extends PersonQueryDsl> type, PathMetadata<?> metadata, String schema, String table) {
        super(type, metadata, schema, table);
        // TODO Auto-generated constructor stub
    }

    public PersonQueryDsl(String variable) {
        super(PersonQueryDsl.class, forVariable(variable), "movies_test", "people");
    }
}
