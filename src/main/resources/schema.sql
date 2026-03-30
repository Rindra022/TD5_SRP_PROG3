create type category as enum ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
create type dish_type as enum ('START', 'MAIN', 'DESSERT');

create table dish
(
    id        serial primary key,
    name      varchar(150) not null,
    dish_type dish_type    not null
);

create table ingredient
(
    id       serial primary key,
    name     varchar(100)   not null,
    price    numeric(10, 2) not null,
    category category       not null,
    id_dish  int,
    constraint fk_dish
        foreign key (id_dish)
            references Dish (id)
);

alter table ingredient
    add column if not exists selling_price numeric(10, 2);

alter table ingredient
    drop column if exists id_dish;

create type unit_type as enum ('KG', 'PCS', 'L');

create table if not exists dish_ingredient
(
    id                serial primary key,
    id_dish           int            not null,
    id_ingredient     int            not null,
    quantity_required numeric(10, 2) not null,
    unity             unit_type      not null
);

alter table dish_ingredient
    add constraint fk_dish
        foreign key (id_dish)
            references dish (id),
    add constraint fk_ingredient
        foreign key (id_ingredient)
            references ingredient (id);


create type mouvement_type as enum ('IN', 'OUT');

create table stock_movement
(
    id                serial primary key,
    id_ingredient     int            not null,
    quantity          numeric(10, 2) not null,
    type              mouvement_type not null,
    unit              unit_type      not null,
    creation_datetime timestamp      not null
);

alter table stock_movement
    add constraint fk_ingredient foreign key (id_ingredient)
        references ingredient (id);

alter table ingredient
    add column if not exists stock numeric(10,2)
        not null default 0.00;

ALTER TABLE dish
    ADD COLUMN IF NOT EXISTS selling_price NUMERIC(10, 2);