package activejdbc.examples.simple;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.connection_config.DBConfiguration;

public class Main {

    public static void main(String[] args) {

        DBConfiguration.loadConfiguration("/database.properties");

        Base.open();

        Person director  = new Person("Stephen Spielberg");
        director.saveIt();

        director.add(new Movie("Saving private Ryan", 1998));
        director.add(new Movie("Jaws", 1982));
        director.getAll(Movie.class).forEach(System.out::println);

        Base.close();
    }
}
