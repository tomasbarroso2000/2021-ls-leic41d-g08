
begin transaction;

insert into users (name, email, password, token) values(
    'Shrek',
    'shrek@gmail.com',
    12345,
    'e2a6cf7c-7125-4a88-858a-2196d24e8ead'
);

insert into users (name, email, password, token) values(
    'Bob',
    'bob@gmail.com',
    12345,
    'ffc0b3b2-8684-4d16-bccf-331a93a982c2'
);

insert into users (name, email, password, token) values(
    'Duck',
    'duck@gmail.com',
    12345,
    '59caeb1f-426e-4882-b050-5ef388df3069'
);

insert into sports (name, description, user_number) values(
    'Voleibol',
    'Melhor desporto para engate',
    1
);

insert into sports (name, description, user_number) values(
    'Badminton',
    'Desporto individual ou de pares',
    2
);

insert into routes (start_location, end_location, distance, user_number) values(
    'Lisboa',
    'Porto',
    300.0,
    1
);

insert into routes (start_location, end_location, distance, user_number) values(
    'Lisboa',
    'Lagos',
    300.0,
    2
);

insert into activities (date, duration, user_number, sport_number, route_number) values(
    '2022-03-24',
    12*60*60*1000+50*30*1000+20*1000,
    1,
    1,
    1
);

insert into activities (date, duration, user_number, sport_number, route_number) values(
    '2022-06-01',
    12*60*60*1000+50*30*1000,
    2,
    2,
    2
);

insert into activities (date, duration, user_number, sport_number, route_number) values(
    '2022-06-01',
    1*60*60*1000+50*30*1000,
    2,
    2,
    1
);

commit transaction;