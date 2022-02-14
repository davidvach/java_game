/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  vacha
 * Created: 23.3.2020
 */

DROP TABLE IF EXISTS player CASCADE;
CREATE TABLE player (
    id serial UNIQUE,
    username varchar,
    password varchar,
    email varchar,
    credit_amount double precision,
    CHECK(email SIMILAR TO '[a-z0-9.]+@[a-z.0-9]+.[a-z]+')
);

DROP TABLE IF EXISTS fee_history CASCADE;
CREATE TABLE fee_history (
    id serial, --serial
    player_id integer REFERENCES player(id) ON DELETE CASCADE,
    date DATE,
    service_id integer REFERENCES services(id)
);

DROP TABLE IF EXISTS services CASCADE;
CREATE TABLE services (
    id integer UNIQUE,
    type varchar,
    description varchar,
    price float
);

DROP TABLE IF EXISTS game_character CASCADE;
CREATE TABLE game_character (
    id serial UNIQUE,
    player_id integer REFERENCES player(id) ON DELETE CASCADE,
    name varchar,
    current_xp integer,
    current_health integer,
    gender varchar,
    eye_color varchar,
    hair_color varchar,
    face_expression varchar,
    is_alive boolean,
    base_attributes_id integer REFERENCES base_attributes(id) ON DELETE CASCADE,
    character_class_id integer REFERENCES character_class(id),
    level_number integer REFERENCES level(number)
);

DROP TABLE IF EXISTS level CASCADE;
CREATE TABLE level (
    number integer UNIQUE,
    xp_next integer
);

DROP TABLE IF EXISTS items CASCADE;
CREATE TABLE items (
    id integer UNIQUE,
    name varchar,
    description varchar,
    armor_id integer REFERENCES armor(id),
    weapon_id integer REFERENCES weapon(id),
    ring_id integer REFERENCES ring(id),
    price integer
);

DROP TABLE IF EXISTS armor CASCADE;
CREATE TABLE armor (
    id integer UNIQUE,
    defense_manipulator numeric
);

DROP TABLE IF EXISTS weapon CASCADE;
CREATE TABLE weapon (
    id integer UNIQUE,
    strength_manipulator numeric
);

DROP TABLE IF EXISTS ring CASCADE;
CREATE TABLE ring (
    id integer UNIQUE,
    health_manipulator numeric
);

DROP TABLE IF EXISTS game_character_owns_item CASCADE;
CREATE TABLE game_character_owns_item (
    game_character_id integer REFERENCES game_character(id) ON DELETE CASCADE,
    item_id integer REFERENCES items(id)
);

DROP TABLE IF EXISTS base_attributes CASCADE;
CREATE TABLE base_attributes (
    id serial UNIQUE,
    defense numeric,
    strength numeric,
    health numeric
);

DROP TABLE IF EXISTS character_class CASCADE;
CREATE TABLE character_class (
    id serial UNIQUE,
    type varchar,
    description varchar
);

DROP TABLE IF EXISTS class_belongs_race CASCADE;
CREATE TABLE class_belongs_race (
    character_class_id integer REFERENCES character_class(id) ON DELETE CASCADE,
    race_id integer REFERENCES race(id) ON DELETE CASCADE,
    UNIQUE(character_class_id, race_id)
);

DROP TABLE IF EXISTS race CASCADE;
CREATE TABLE race (
    id serial UNIQUE,
    type varchar,
    description varchar
);

DROP TABLE IF EXISTS abilities CASCADE;
CREATE TABLE abilities (
    id integer UNIQUE,
    name varchar,
    description varchar,
    strength_factor numeric,
    attack_id integer REFERENCES attack(id),
    heal_id integer REFERENCES heal(id)
);

DROP TABLE IF EXISTS ability_belongs_class CASCADE;
CREATE TABLE ability_belongs_class (
    character_class_id integer REFERENCES character_class(id) ON DELETE CASCADE,
    ability_id integer REFERENCES abilities(id) ON DELETE CASCADE,
    UNIQUE(character_class_id, ability_id)
);

DROP TABLE IF EXISTS attack CASCADE;
CREATE TABLE attack (
    id integer UNIQUE
);

DROP TABLE IF EXISTS heal CASCADE;
CREATE TABLE heal (
    id integer UNIQUE
);

DROP TABLE IF EXISTS battle_history CASCADE;
CREATE TABLE battle_history (
    id serial UNIQUE,
    game_character_id integer REFERENCES game_character(id) ON DELETE CASCADE,
    opponent_id integer REFERENCES game_character(id) ON DELETE CASCADE,
    date_battle TIMESTAMP,
    result_state varchar
);