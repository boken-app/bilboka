create table maintenanceitems
(
    id   serial       not null
        constraint maintenanceitems_pkey
            primary key,
    item varchar(100) not null
        constraint maintenanceitems_item_unique
            unique
);

alter table bookentries
    add comment varchar(255) null;
alter table bookentries
    add maintenance_item varchar(100) null
        constraint fk_bookentries_maintenance_item__id
            references maintenanceitems
            on update restrict on delete restrict
;
