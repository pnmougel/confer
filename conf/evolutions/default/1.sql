# Tasks schema
 
# --- !Ups

CREATE TABLE IUser (
    id BIGSERIAL NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    pseudo VARCHAR(255) NOT NULL,
    isAdmin BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Ranking (
    id BIGSERIAL NOT NULL,
    url VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    PRIMARY KEY (id)
);

CREATE TABLE Tag (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Category (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Publisher (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Conference (
    id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    short_name VARCHAR(255) NOT NULL,
    year_since INT,
    description TEXT,
    is_national BOOLEAN NOT NULL,
    is_workshop BOOLEAN NOT NULL,
    is_journal BOOLEAN NOT NULL,
    category_id BIGINT NOT NULL REFERENCES Category(id),
    publisher_id BIGINT NOT NULL REFERENCES Publisher(id),
    PRIMARY KEY (id)
);

CREATE TABLE Comment (
    id BIGSERIAL NOT NULL,
    iuser_id BIGINT NOT NULL REFERENCES IUser(id),
    conference_id BIGINT NOT NULL REFERENCES Conference(id),
    content TEXT NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ConferenceTag (
    conference_id BIGINT NOT NULL REFERENCES Conference(id),
    tag_id BIGINT NOT NULL REFERENCES Tag(id),
    PRIMARY KEY (conference_id,tag_id)
);

CREATE TABLE RankingScore (
    ranking_id BIGSERIAL NOT NULL REFERENCES Ranking(id),
    conference_id BIGINT NOT NULL REFERENCES Conference(id),
    score INT NOT NULL,
    PRIMARY KEY (ranking_id,conference_id)
);

CREATE TABLE Link (
    id BIGSERIAL NOT NULL,
    conference_id BIGINT NOT NULL REFERENCES Conference(id), 
    url VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE ScoreIUser (
    iuser_id BIGINT NOT NULL REFERENCES IUser(id),
    conference_id BIGINT NOT NULL,
    score INT NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (iuser_id,conference_id)
);

CREATE TYPE Relation AS ENUM ('colocated', 'splited', 'merged');

CREATE TABLE ConferenceConferenceRelation (
    conference_from_id BIGINT NOT NULL REFERENCES Conference(id),
    conference_to_id BIGINT NOT NULL REFERENCES Conference(id),
    relation Relation NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (conference_from_id, conference_to_id, date)
);

# --- !Downs
 
DROP TABLE ConferenceConferenceRelation;
DROP TYPE Relation;
DROP TABLE ScoreIUser;
DROP TABLE Link;
DROP TABLE RankingScore;
DROP TABLE ConferenceTag;
DROP TABLE Comment;
DROP TABLE Conference;
DROP TABLE Publisher;
DROP TABLE Category;
DROP TABLE Tag;
DROP TABLE Ranking;
DROP TABLE IUser;
