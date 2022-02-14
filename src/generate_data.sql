/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  vacha
 * Created: 23.3.2020
 */

truncate table player, services, fee_history, game_character, items, armor, weapon, ring, base_attributes, character_class, class_belongs_race, race,
abilities, ability_belongs_class, attack, heal, game_character_owns_item, battle_history cascade;

--POMOCNE TABULKY:

DROP TABLE IF EXISTS usernames CASCADE;
CREATE TABLE usernames (
    username varchar
);
INSERT INTO usernames (username) VALUES
('PeterJohnson'),('PurpleTea'),('EnjoyLife'),('IceTea123'),('DarkWarrior00'),('MySword789'),('kingofmyself'),('Corny123'),('SaltyCracker'),
('Damasi'),('thaTone'),('Francis'),('manwithoutname'),('justme'),('bigbrother111'),('smallsister444'),('juicyjuice'),('sandyshore');

--GENEROVANIE / VKLADANIE DAT:

CREATE OR REPLACE FUNCTION random_username() RETURNS varchar LANGUAGE sql AS
$$
SELECT username FROM usernames ORDER BY random() LIMIT 1;
$$;

INSERT INTO player (username, password, email, credit_amount)
SELECT
    random_username() as username,
    '123456789' as password,
    'myemail'||id::varchar||'@gmail.com' as email,
    floor(random()*80::float) as credit_amount
FROM generate_series(1,500000) AS seq(id);

DROP INDEX IF EXISTS ixplayer_id;
CREATE INDEX ixplayer_id ON player(id);

INSERT INTO services (id, type, description, price) VALUES
(1, 'Microtransaction', 'Shop offers rotation', 5),(2, 'Microtransaction', 'Random armor item', 10),
(3, 'Microtransaction', 'Random weapon item', 15),(4, 'Microtransaction', 'Random ring item', 7),
(5, 'Microtransaction', 'REFILL HP +50%', 3),(6, 'Microtransaction', 'REFILL HP +100%', 5),
(7, 'Metamorphosis', 'Metamorphosed characters', 50),(8, 'Setting change', 'Character transfer', 100),
(9, 'Setting change', 'Username change', 50),(10, 'Setting change', 'Password change', 0),
(11, 'Character transfer', 'Transfering character', 10),(12, 'Change appearance', 'Changing appearance', 50);

INSERT INTO fee_history (player_id, date, service_id)
SELECT
    floor(random()*500000)+1 as player_id,
    NOW() - (random() * (NOW()+'90 days' - NOW())) as date,
    floor(random()*10) as service_id
FROM generate_series(1,200000) as seq(id);

INSERT INTO game_character (player_id, name, current_xp, current_health, gender, eye_color, hair_color, face_expression, 
is_alive, base_attributes_id, character_class_id, level_number)
SELECT
    floor(random()*500000)+1 as player_id,
    substring((SELECT username FROM player WHERE id = MOD(ida,80)),2,4) as name,
    0 as current_xp,
    100 as current_health,
    CASE floor(random()*2)
        WHEN 0 THEN 'MALE'
        WHEN 1 THEN 'FEMALE'
    END AS gender,
    CASE floor(random()*4)
        WHEN 0 THEN 'blue'
        WHEN 1 THEN 'black'
        WHEN 2 THEN 'green'
        WHEN 3 THEN 'grey'
    END AS eye_color,
    CASE floor(random()*5)
        WHEN 0 THEN 'black'
        WHEN 1 THEN 'brown'
        WHEN 2 THEN 'red'
        WHEN 3 THEN 'blue'
        WHEN 4 THEN 'silver'
    END AS hair_color,
    CASE floor(random()*3)
        WHEN 0 THEN 'angry'
        WHEN 1 THEN 'smile'
        WHEN 2 THEN 'neutral'
    END AS face_expression,
    true as is_alive,
    ida as base_attributes_id,
    floor(random()*3)+1 as character_class_id,
    floor(random()*10)+1 as level_number
FROM generate_series(1,1500000) as seq(ida);

DROP INDEX IF EXISTS ix_game_character_id;
CREATE INDEX ix_game_character_id ON game_character(id);

UPDATE game_character SET current_health = level_number*100;

--ARMORS (id 1-5), WEAPONS (id 6-10), RINGS (id 11-15)
INSERT INTO items (id, name, description, armor_id, weapon_id, ring_id, price) VALUES
(1,'Wooden barrel','Perfectly suits your body shape and slightly increases your defense.', 1, null, null, 5),
(2,'Geppetto´s newest product','Does not provide much defense, but makes your opponent jealous.', 2, null, null, 8),
(3,'Steel deathless jacket','How did the previous owner die?', 3, null, null, 30),
(4,'Golden shirt','So much defense in such a tiny shirt.', 4, null, null, 100),
(5,'Diamond suit','A massive addition of defense, making it twice as effective!', 5, null, null, 250),
(6,'Plastic sword','Can be bought in a toy shop, but still provides small strength bonus for your character.', null, 1, null, 12),
(7,'Egg launcher','How does the war in a coop look like?', null, 2, null, 20),
(8,'Luxury stick','The great choice for a mage. Provides good addition to your strength.', null, 3, null, 50),
(9,'Double gun','For warriors and archers. Provides good addition to your strength.', null, 4, null, 150),
(10,'Triple gun','Is there any ability this gun cannot handle? No. Provides massive addition to your strength.', null, 5, null, 300),
(11,'Wooden ring','Nobody knows how it works, but has strange effect for some people´s health.', null, null, 1, 25),
(12,'Ring with a skull','Heat from a tropical island where this ring was found still slightly increases your health.', null, null, 2, 60),
(13,'Invisible ring','Why do some people have so much health?', null, null, 3, 90),
(14,'Ring for a lost princess','So many years passed, but it´s still full of love! Provides stack of health bonus.', null, null, 4, 200),
(15,'Genie´s only wish','Ring found in a sand. There is only one wish it can fulfill - boost your health to sky numbers!', null, null, 5, 500);
INSERT INTO armor (id, defense_manipulator) VALUES
(1,0.05),(2,0.1),(3,0.25),(4,0.4),(5,0.6);
INSERT INTO weapon (id, strength_manipulator) VALUES
(1,0.05),(2,0.15),(3,0.2),(4,0.35),(5,0.4);
INSERT INTO ring (id, health_manipulator) VALUES
(1,0.1),(2,0.2),(3,0.3),(4,0.5),(5,1);
INSERT INTO game_character_owns_item (game_character_id, item_id)
SELECT
    floor(random()*1500000)+1 as game_character_id,
    floor(random()*15)+1 as item_id
FROM generate_series(1,1200000) as seq(id);


INSERT INTO level (number, xp_next) VALUES
(1,100),(2,250),(3,400),(4,600),(5,850),(6,1200),(7,1800),(8,2500),(9,3400),(10,999999);

DROP INDEX IF EXISTS ix_game_character_b_a_id;
CREATE INDEX ix_game_character_b_a_id ON game_character(base_attributes_id);

INSERT INTO base_attributes (defense, strength, health)
SELECT
    --ida as id,
    (SELECT level_number FROM game_character as g WHERE ida = g.base_attributes_id)*10 as defense,
    (SELECT level_number FROM game_character as g WHERE ida = g.base_attributes_id)*20 as strength,
    (SELECT level_number FROM game_character as g WHERE ida = g.base_attributes_id)*100 as health
FROM generate_series(1,1500000) as seq(ida);

INSERT INTO character_class (type, description) VALUES
('Lightrange warrior','Can fight in a short distance.'),
('Archer','Accurately hits the opponent from a long distance!'),
('Mage','Spells. Everything he needs to know.');

INSERT INTO class_belongs_race (character_class_id, race_id) VALUES
(1,1),(1,4),(2,3),(2,5),(3,3),(3,5),(3,2);

INSERT INTO race (type, description) VALUES
('Dwarf', 'Small but strong! Nobody can handle more swords than these little guys.'),
('Fairy', 'Elegant and mystical beings. Can do everything with magic.'),
('Elf', 'Usually live in forest. Can hide in bushes and have excellent archer skills.'),
('Orc', 'Heavy guys! Fighting with heavy swords is their life.'),
('Amun', 'Mystical guys coming from where her highness - Bastet - lives. Combine skills of archers and mages.');

INSERT INTO abilities (id, name, description, strength_factor, attack_id, heal_id) VALUES
(1,'Lava gun','Weapon felt into bucket full of lava. Prepare for a fiery battle!',0.4,1,null),
(2,'Rampage','Aggressivity makes it really dangerous. Your enemies should hide!',0.3,2,null),
(3,'Fight or flight!','End of the battle is closer than we might think!',1,3,null),
(4,'Divine curse','Blessing directly from the sky! How glorious!',0.2,null,1),
(5,'Blossom winds','When tousands of fairies begin waving their wings, everybody feels blossom wind.',0.4,null,2),
(6,'Bastet awakening','When her highness wakes up, everybody feels strange energy in the air. What impact this might have?',1,null,3);

INSERT INTO ability_belongs_class (character_class_id, ability_id) VALUES
(1,1),(1,2),(1,3),(2,3),(2,1),(2,6),(3,4),(3,5),(3,6);

INSERT INTO attack (id)
SELECT
    id
FROM generate_series(1,3) as seq(id);

INSERT INTO heal (id)
SELECT
    id
FROM generate_series(4,6) as seq(id);

CREATE OR REPLACE FUNCTION random_character_id() RETURNS integer LANGUAGE sql AS
$$
SELECT id FROM game_character ORDER BY random() LIMIT 1;
$$;

INSERT INTO battle_history (game_character_id, opponent_id, date_battle, result_state)
SELECT
    floor(random()*1500000)+1 as game_character_id,
    floor(random()*1500000)+1 as opponent_id,
    timestamp '2020-01-01 00:00:00' + random() * (NOW() - timestamp '2020-01-01 00:00:00') as date,
    CASE random() > 0.5 WHEN true THEN 'WINNER' ELSE 'LOOSER' end as result_state
FROM generate_series(1,1200) as seq(id);

DROP INDEX IF EXISTS ixbattle_history_ch_id;
CREATE INDEX ixbattle_history_ch_id ON battle_history(game_character_id);
DROP INDEX IF EXISTS ixbattle_history_date_battle;
CREATE INDEX ixbattle_history_date_battle ON battle_history(date_battle);

DELETE FROM battle_history WHERE game_character_id = opponent_id;