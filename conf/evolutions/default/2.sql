
# --- !Ups

INSERT INTO category (name) VALUES ('Algorithms and Theory');
INSERT INTO category (name) VALUES ('Programming Languages');
INSERT INTO category (name) VALUES ('Concurrent, Distributed and Parallel Computing');
INSERT INTO category (name) VALUES ('Software Engineering');
INSERT INTO category (name) VALUES ('Operating Systems');
INSERT INTO category (name) VALUES ('Computer Architecture');
INSERT INTO category (name) VALUES ('Computer Networking and Networked Systems');
INSERT INTO category (name) VALUES ('Security and Privacy');
INSERT INTO category (name) VALUES ('Data Management');
INSERT INTO category (name) VALUES ('Artificial Intelligence');
INSERT INTO category (name) VALUES ('Computer Graphics');
INSERT INTO category (name) VALUES ('Human–Computer Interaction');
INSERT INTO category (name) VALUES ('Computational Biology');
INSERT INTO category (name) VALUES ('Education');

INSERT INTO publisher (name) VALUES ('SIAM');
INSERT INTO publisher (name) VALUES ('ACM');
INSERT INTO publisher (name) VALUES ('IEEE');
INSERT INTO publisher (name) VALUES ('Springer');

# --- !Downs

DELETE FROM category;
DELETE FROM publisher;



