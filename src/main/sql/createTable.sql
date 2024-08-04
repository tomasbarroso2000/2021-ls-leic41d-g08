begin transaction;

create table if not exists users(
                                    number serial primary key,
                                    name   varchar(20) not null,
                                    email  varchar(100) not null unique check (email like '%@%.%'),
                                    password integer not null,
                                    token varchar(40) unique
);

create table if not exists sports(
                                     number serial primary key,
                                     name varchar(20) not null,
                                     description varchar(100),
                                     user_number integer not null,
                                     constraint fk_user_number
                                         foreign key (user_number) references users(number)
);

create table if not exists routes(
                                     number serial primary key,
                                     start_location varchar(50) not null,
                                     end_location varchar(50) not null,
                                     distance double precision not null check (distance > 0),
                                     user_number integer not null,
                                     constraint fk_user_number
                                         foreign key (user_number) references users(number)
);

create table if not exists activities(
                                         number serial primary key,
                                         date date not null,
                                         duration integer not null,
                                         user_number integer not null,
                                         sport_number integer not null,
                                         route_number integer,
                                         constraint fk_user_number
                                             foreign key (user_number) references users(number),
                                         constraint fk_sport_number
                                             foreign key (sport_number) references sports(number),
                                         constraint fk_route_number
                                             foreign key (route_number) references routes(number)

);

commit transaction;

