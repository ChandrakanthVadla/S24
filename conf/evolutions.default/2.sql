# --- Sample dataset

# --- !Ups
  insert into advert (id,title,fuel,price,`new`) values (1,'volvo avanta 1','gasoline', 100, 'true' );

  insert into advert (id,title,fuel,price,`new`, mileage, firstRegistration  ) values (
  2,'volvo avanta 2','gasoline', 100, 'false', 10, '1987-01-01' );

  insert into advert (id,title,fuel,price,`new`, mileage, firstRegistration  ) values (
  3,'volvo avanta 3','gasoline', 100, 'false', 10, '1987-01-01' );

  insert into advert (id,title,fuel,price,`new`) values (4,'volvo avanta 4','gasoline', 100, 'true' );


# --- !Downs

delete from advert;
