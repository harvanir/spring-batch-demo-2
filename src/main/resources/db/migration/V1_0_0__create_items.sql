create table items
(
    id         bigint signed           not null primary key auto_increment,
    name       varchar(50)             not null,
    quantity   int signed              not null,
    price      double precision signed not null,
    created_at timestamp(6),
    created_by varchar(50),
    updated_at timestamp(6),
    updated_by varchar(50),
    version    int signed              not null default 0
) engine = InnoDB;