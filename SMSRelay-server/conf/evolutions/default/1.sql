# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "registration" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"login" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"registrationId" VARCHAR NOT NULL);

# --- !Downs

drop table "registration";

