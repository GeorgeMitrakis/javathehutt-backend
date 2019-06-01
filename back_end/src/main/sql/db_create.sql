create table city
(
	id integer not null
		constraint city_pk
			primary key,
	name varchar(32) not null,
	geom geometry
);

alter table city owner to javathehutt;

create table location
(
	id integer not null
		constraint location_pk
			primary key,
	city_id integer not null
		constraint location_city
			references city,
	geom geometry
);

alter table location owner to javathehutt;

create table "user"
(
	id bigint not null
		constraint user_pk
			primary key,
	email varchar(64) not null,
	password varchar(256) not null,
	role varchar(32) not null,
	isbanned boolean default false
);

alter table "user" owner to javathehutt;

create table administrator
(
	id integer not null
		constraint administrator_pk
			primary key
		constraint administrator_user
			references "user",
	name varchar(64) not null,
	surname varchar(64) not null
);

alter table administrator owner to javathehutt;

create table provider
(
	id integer not null
		constraint provider_pk
			primary key
		constraint provider_user
			references "user",
	providername varchar(256) not null
);

alter table provider owner to javathehutt;

create table room
(
	id integer not null
		constraint room_pk
			primary key,
	provider_id integer not null
		constraint room_provider
			references provider,
	location_id integer not null
		constraint room_location
			references location
				on update restrict on delete restrict,
	price integer not null,
	wifi boolean not null,
	pool boolean not null,
	shauna boolean not null,
	capacity integer
);

alter table room owner to javathehutt;

create table visitor
(
	id integer not null
		constraint visitor_pk
			primary key
		constraint visitor_user
			references "user",
	name varchar(64) not null,
	surname varchar(64) not null
);

alter table visitor owner to javathehutt;

create table favorites
(
	visitor_id integer not null
		constraint favorites_visitor
			references visitor,
	room_id integer not null
		constraint favorites_room
			references room,
	constraint favorites_pk
		primary key (visitor_id, room_id)
);

alter table favorites owner to javathehutt;

create table rating
(
	id integer not null
		constraint rating_pk
			primary key,
	comment varchar(200) not null,
	stars integer not null,
	room_id integer not null
		constraint rating_room
			references room,
	visitor_id integer not null
		constraint rating_visitor
			references visitor
);

alter table rating owner to javathehutt;

create table transactions
(
	visitor_id integer not null
		constraint transactions_visitor
			references visitor,
	room_id integer not null
		constraint transactions_room
			references room,
	closure_date timestamp not null,
	start_date date not null,
	end_date date not null,
	cost real,
	id serial not null
		constraint transactions_pk_2
			primary key
);

alter table transactions owner to javathehutt;

create table spatial_ref_sys
(
	srid integer not null
		constraint spatial_ref_sys_pkey
			primary key
		constraint spatial_ref_sys_srid_check
			check ((srid > 0) AND (srid <= 998999)),
	auth_name varchar(256),
	auth_srid integer,
	srtext varchar(2048),
	proj4text varchar(2048)
);

alter table spatial_ref_sys owner to rdsadmin;

create table us_lex
(
	id serial not null
		constraint pk_us_lex
			primary key,
	seq integer,
	word text,
	stdword text,
	token integer,
	is_custom boolean default true not null
);

alter table us_lex owner to rdsadmin;

create table us_gaz
(
	id serial not null
		constraint pk_us_gaz
			primary key,
	seq integer,
	word text,
	stdword text,
	token integer,
	is_custom boolean default true not null
);

alter table us_gaz owner to rdsadmin;

create table us_rules
(
	id serial not null
		constraint pk_us_rules
			primary key,
	rule text,
	is_custom boolean default true not null
);

alter table us_rules owner to rdsadmin;

