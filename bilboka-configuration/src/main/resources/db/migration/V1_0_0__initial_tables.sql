create table vehicles
(
    id                          serial      not null
        constraint vehicles_pkey
            primary key,
    name                        varchar(50) not null
        constraint vehicles_name_unique
            unique,
    nicknames                   text default ''::text not null,
    tegnkombinasjon_normalisert varchar(15),
    odo_unit                    varchar(20),
    fuel_type                   varchar(15),
    created_timestamp           timestamp   not null
);

create table users
(
    id                serial      not null
        constraint users_pkey
            primary key,
    username          varchar(30) not null
        constraint users_username_unique
            unique,
    created_timestamp timestamp   not null
);

create table bookentries
(
    id                serial      not null
        constraint bookentries_pkey
            primary key,
    datetime          timestamp   not null,
    vehicle           integer     not null
        constraint fk_bookentries_vehicle__id
            references vehicles
            on update restrict on delete restrict,
    odometer          integer,
    type              varchar(50) not null,
    amount            double precision,
    cost_nok          double precision,
    is_full           boolean,
    entered_by        integer
        constraint fk_bookentries_entered_by__id
            references users
            on update restrict on delete restrict,
    source            varchar(50) not null,
    created_timestamp timestamp   not null
);

create table userregistrations
(
    id                   serial      not null
        constraint userregistrations_pkey
            primary key,
    "user"               integer     not null
        constraint fk_userregistrations_user__id
            references users
            on update restrict on delete restrict,
    "registrationTypeID" varchar(50) not null,
    "registeredID"       varchar(50) not null,
    created_timestamp    timestamp   not null
);

create table registrationkeys
(
    id     serial               not null
        constraint registrationkeys_pkey
            primary key,
    "user" integer              not null
        constraint fk_registrationkeys_user__id
            references users
            on update restrict on delete restrict,
    key    varchar(100)         not null,
    usable boolean default true not null
);
