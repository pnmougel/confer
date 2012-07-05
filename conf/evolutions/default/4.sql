
# --- !Ups

ALTER TABLE link ADD date timestamp with time zone;

# --- !Downs

ALTER TABLE link DROP COLUMN date;


