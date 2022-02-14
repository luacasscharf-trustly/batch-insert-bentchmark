package activejdbc.examples.simple;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;

/**
 * @author Igor Polevoy on 11/16/15.
 */
@IdGenerator("nextval('seq')")
public class Person extends Model {

    public Person() {}

    public Person(String name){
        set("name", name);
    }
}
