create table trips
(
    id                serial       not null
        constraint trips_pkey
            primary key,
    trip_name         varchar(255) not null,
    vehicle           integer      not null
        constraint fk_trips_vehicle__id
            references vehicles
            on update restrict on delete restrict,
    datetime_start    timestamp    not null,
    datetime_end      timestamp    null,
    odometer_start    integer      not null,
    odometer_end      integer      null,
    comment           varchar(255),
    entered_by        integer
        constraint fk_trips_entered_by__id
            references users
            on update restrict on delete restrict,
    created_timestamp timestamp    not null
);
