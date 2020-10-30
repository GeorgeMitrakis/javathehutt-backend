create extension postgis;
create extension postgis_topology;
create extension fuzzystrmatch;
create extension address_standardizer;
create extension address_standardizer_data_us;
create extension postgis_tiger_geocoder;

create table administrator
(
    id      integer     not null
        constraint administrator_pk
            primary key
        constraint administrator_user
            references "user",
    name    varchar(64) not null,
    surname varchar(64) not null
);

alter table administrator
    owner to javathehutt;

INSERT INTO public.administrator (id, name, surname) VALUES (31, 'petros', 'petropoulos');
create table city
(
    id   integer     not null
        constraint city_pk
            primary key,
    name varchar(32) not null,
    geom geometry
);

alter table city
    owner to javathehutt;

INSERT INTO public.city (id, name, geom) VALUES (1, 'Athens', 010100000027BD6F7CEDFD42408690F3FE3FBA3740);
INSERT INTO public.city (id, name, geom) VALUES (2, 'Volos', 01010000003C4ED1915CAE43408C4AEA0434F13640);
create table favorites
(
    visitor_id integer not null
        constraint favorites_visitor
            references visitor,
    room_id    integer not null
        constraint favorites_room
            references room,
    constraint favorites_pk
        primary key (visitor_id, room_id)
);

alter table favorites
    owner to javathehutt;


create table location
(
    id      integer not null
        constraint location_pk
            primary key,
    city_id integer not null
        constraint location_city
            references city,
    geom    geometry
);

alter table location
    owner to javathehutt;

INSERT INTO public.location (id, city_id, geom) VALUES (1, 1, 010100000004C8D0B183FC4240B613252191BE3740);
INSERT INTO public.location (id, city_id, geom) VALUES (2, 2, 0101000000D99596917AAF434085B35BCB64F43640);
create table provider
(
    id           integer      not null
        constraint provider_pk
            primary key
        constraint provider_user
            references "user",
    providername varchar(256) not null
);

alter table provider
    owner to javathehutt;

INSERT INTO public.provider (id, providername) VALUES (1, 'thelegend27');
create table rating
(
    id         integer      not null
        constraint rating_pk
            primary key,
    comment    varchar(200) not null,
    stars      integer      not null,
    room_id    integer      not null
        constraint rating_room
            references room,
    visitor_id integer      not null
        constraint rating_visitor
            references visitor
);

alter table rating
    owner to javathehutt;


create table room
(
    id          integer not null
        constraint room_pk
            primary key,
    provider_id integer not null
        constraint room_provider
            references provider,
    location_id integer not null
        constraint room_location
            references location
            on update restrict on delete restrict,
    price       integer not null,
    wifi        boolean not null,
    pool        boolean not null,
    shauna      boolean not null,
    capacity    integer
);

alter table room
    owner to javathehutt;

INSERT INTO public.room (id, provider_id, location_id, price, wifi, pool, shauna, capacity) VALUES (1, 1, 1, 80, true, true, false, 32);
create table spatial_ref_sys
(
    srid      integer not null
        constraint spatial_ref_sys_pkey
            primary key
        constraint spatial_ref_sys_srid_check
            check ((srid > 0) AND (srid <= 998999)),
    auth_name varchar(256),
    auth_srid integer,
    srtext    varchar(2048),
    proj4text varchar(2048)
);

alter table spatial_ref_sys
    owner to rdsadmin;

create table transactions
(
    visitor_id   integer   not null
        constraint transactions_visitor
            references visitor,
    room_id      integer   not null
        constraint transactions_room
            references room,
    closure_date timestamp not null,
    start_date   date      not null,
    end_date     date      not null,
    cost         real,
    id           serial    not null
        constraint transactions_pk_2
            primary key
);

alter table transactions
    owner to javathehutt;

INSERT INTO public.transactions (visitor_id, room_id, closure_date, start_date, end_date, cost, id) VALUES (32, 1, '2019-05-25 00:00:00.000000', '1997-11-22', '2019-05-14', 80, 1);
create table "user"
(
    id       bigint       not null
        constraint user_pk
            primary key,
    email    varchar(64)  not null,
    password varchar(256) not null,
    role     varchar(32)  not null,
    isbanned boolean default false
);

alter table "user"
    owner to javathehutt;

INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (30, 'firsttest@gmail.com', '8f3d0848f5d81379f0dffb8441034ff75dd27179b276c66b53d74a4885bfb0ba', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (32, 'alfa@mpura.com', '5d088b7c227edec484790433d156221cd93fb991a7746c0803807be38d453c24', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (31, 'petros@test.com', 'dfa7249f0c63d0e0a4fb827735e35c95ebd441fa92b5b3a6798482fb0e11a351', 'admin', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (33, 'test@test.com', '021e7acdce34d36b5dc434ac73c30efa07dc724f2dc61794b084a79e134876ff', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (34, 'ctsap97@gmail.com', 'c138d5f5a6a72a47b05b9b44ad503e6ac6b8b800f56976f4e2bea5816e8fcd01', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (1, 'thelegend27@agame.com', '021e7acdce34d36b5dc434ac73c30efa07dc724f2dc61794b084a79e134876ff', 'provider', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (35, '1234Da@email.com', '68591fb6020a915556d5411a6e749669186a0c08cc48f2088359081e34108543', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (36, 'test2@test.example.com', 'c138d5f5a6a72a47b05b9b44ad503e6ac6b8b800f56976f4e2bea5816e8fcd01', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (37, 'visitor1@test.com', '9d6f95cd1ec525958413709f36151e4bcd06a2776d331b9c90ab3417d62dd499', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (38, 'george@test.com', '4d1f20772cc965b93cf971b6179cc23265e03809b3a079f3512c2a54f0265b1e', 'visitor', false);
INSERT INTO public."user" (id, email, password, role, isbanned) VALUES (39, 'abcdef@test.com', '62ffbec96d99d7734a6a767dd51e2f4d98257aad62120bf46ca6e1d2da206beb', 'visitor', false);
create table visitor
(
    id      integer     not null
        constraint visitor_pk
            primary key
        constraint visitor_user
            references "user",
    name    varchar(64) not null,
    surname varchar(64) not null
);

alter table visitor
    owner to javathehutt;

INSERT INTO public.visitor (id, name, surname) VALUES (30, 'Jon', 'Doe');
INSERT INTO public.visitor (id, name, surname) VALUES (32, 'alfa', 'mpura');
INSERT INTO public.visitor (id, name, surname) VALUES (33, 'test', 'testing');
INSERT INTO public.visitor (id, name, surname) VALUES (34, 'Christos', 'Tsapelas');
INSERT INTO public.visitor (id, name, surname) VALUES (35, 'Mark', 'os');
INSERT INTO public.visitor (id, name, surname) VALUES (36, 'Teeest', 'test');
INSERT INTO public.visitor (id, name, surname) VALUES (37, 'testname', 'testsurname');
INSERT INTO public.visitor (id, name, surname) VALUES (38, 'George', 'Mitrakis');
INSERT INTO public.visitor (id, name, surname) VALUES (39, 'Aaaa', 'aAAAa');