create extension postgis;
create extension postgis_topology;
create extension fuzzystrmatch;
create extension address_standardizer;
create extension address_standardizer_data_us;
create extension postgis_tiger_geocoder;

insert into public.city(id, name, geom) values (1, 'Athens', ST_GeomFromText('POINT(37.983810 23.727539)'));
insert into public.city(id, name, geom) values (2, 'Volos', ST_GeomFromText('POINT(39.3622 22.9422)'));

insert into public.location(id, city_id, geom) values (1, 1, ST_GeomFromText('POINT(37.983810 23.727539)'));
insert into public.location(id, city_id, geom) values (2, 2, ST_GeomFromText('POINT(39.3622 22.9422)'));