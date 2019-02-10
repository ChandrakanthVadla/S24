# --- First database schema

# --- !Ups

set ignorecase true;


create table adverts(
  id                    int not null,
  title                 varchar(255) not null,
  fuel                  varchar(255)  not null,
  price                 int  not null,
  `new`                 boolean   not null,
  mileage               int,
  firstRegistration    date,
  constraint pk_adverts primary key (id))
;

create sequence adverts_seq start with 1000;
# --- !Downs
drop table is exists adverts;
drop sequence if exists adverts_seq;

