-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2019-03-30 15:56:58.842

-- tables
-- Table: administrator
CREATE TABLE administrator (
    id int  NOT NULL,
    CONSTRAINT administrator_pk PRIMARY KEY (id)
);

-- Table: city
CREATE TABLE city (
    id int  NOT NULL,
    name varchar(32)  NOT NULL,
    CONSTRAINT city_pk PRIMARY KEY (id)
);

-- Table: favorites
CREATE TABLE favorites (
    visitor_id int  NOT NULL,
    room_id int  NOT NULL,
    CONSTRAINT favorites_pk PRIMARY KEY (visitor_id,room_id)
);

-- Table: location
CREATE TABLE location (
    id int  NOT NULL,
    coordinate_X int  NOT NULL,
    coordiante_Y int  NOT NULL,
    city_id int  NOT NULL,
    CONSTRAINT location_pk PRIMARY KEY (id)
);

-- Table: provider
CREATE TABLE provider (
    id int  NOT NULL,
    CONSTRAINT provider_pk PRIMARY KEY (id)
);

-- Table: rating
CREATE TABLE rating (
    id int  NOT NULL,
    comment varchar(200)  NOT NULL,
    stars int  NOT NULL,
    room_id int  NOT NULL,
    visitor_id int  NOT NULL,
    CONSTRAINT rating_pk PRIMARY KEY (id)
);

-- Table: room
CREATE TABLE room (
    id int  NOT NULL,
    provider_id int  NOT NULL,
    location int  NOT NULL,
    price int  NOT NULL,
    wifi boolean  NOT NULL,
    pool boolean  NOT NULL,
    shauna boolean  NOT NULL,
    CONSTRAINT room_pk PRIMARY KEY (id)
);

-- Table: transactions
CREATE TABLE transactions (
    visitor_id int  NOT NULL,
    room_id int  NOT NULL,
    cost money  NOT NULL,
    datetime timestamp  NOT NULL,
    CONSTRAINT transactions_pk PRIMARY KEY (visitor_id,room_id)
);

-- Table: user
CREATE TABLE "user" (
    id int  NOT NULL,
    email varchar(32)  NOT NULL,
    username varchar(32)  NOT NULL,
    password varchar(50)  NOT NULL,
    role varchar(50)  NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY (id)
);

-- Table: visitor
CREATE TABLE visitor (
    id int  NOT NULL,
    CONSTRAINT visitor_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: Favorites_room (table: favorites)
ALTER TABLE favorites ADD CONSTRAINT Favorites_room
    FOREIGN KEY (room_id)
    REFERENCES room (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: Favorites_visitor (table: favorites)
ALTER TABLE favorites ADD CONSTRAINT Favorites_visitor
    FOREIGN KEY (visitor_id)
    REFERENCES visitor (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: administrator_user (table: administrator)
ALTER TABLE administrator ADD CONSTRAINT administrator_user
    FOREIGN KEY (id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: location_city (table: location)
ALTER TABLE location ADD CONSTRAINT location_city
    FOREIGN KEY (city_id)
    REFERENCES city (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: provider_user (table: provider)
ALTER TABLE provider ADD CONSTRAINT provider_user
    FOREIGN KEY (id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: rating_room (table: rating)
ALTER TABLE rating ADD CONSTRAINT rating_room
    FOREIGN KEY (room_id)
    REFERENCES room (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: rating_visitor (table: rating)
ALTER TABLE rating ADD CONSTRAINT rating_visitor
    FOREIGN KEY (visitor_id)
    REFERENCES visitor (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: room_location (table: room)
ALTER TABLE room ADD CONSTRAINT room_location
    FOREIGN KEY (location)
    REFERENCES location (id)
    ON DELETE  RESTRICT 
    ON UPDATE  RESTRICT 
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: room_provider (table: room)
ALTER TABLE room ADD CONSTRAINT room_provider
    FOREIGN KEY (provider_id)
    REFERENCES provider (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: transactions_room (table: transactions)
ALTER TABLE transactions ADD CONSTRAINT transactions_room
    FOREIGN KEY (room_id)
    REFERENCES room (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: transactions_visitor (table: transactions)
ALTER TABLE transactions ADD CONSTRAINT transactions_visitor
    FOREIGN KEY (visitor_id)
    REFERENCES visitor (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- Reference: visitor_user (table: visitor)
ALTER TABLE visitor ADD CONSTRAINT visitor_user
    FOREIGN KEY (id)
    REFERENCES "user" (id)  
    NOT DEFERRABLE 
    INITIALLY IMMEDIATE
;

-- End of file.

