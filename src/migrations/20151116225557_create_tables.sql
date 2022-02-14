CREATE TABLE IF NOT EXISTS movies (
  id    INTEGER NOT NULL,
  title TEXT,
  year  INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS people (
  id   INTEGER NOT NULL,
  name TEXT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS movies_people (
  id        INTEGER NOT NULL,
  movie_id  INTEGER NOT NULL,
  person_id INTEGER NOT NULL,
  PRIMARY KEY (id)
);

create SEQUENCE IF NOT EXISTS seq;