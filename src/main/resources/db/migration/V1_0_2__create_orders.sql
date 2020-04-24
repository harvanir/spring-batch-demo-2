create table orders
(
    id          bigint signed not null primary key auto_increment,
    status      varchar(50)   not null,
    status_code smallint      not null,
    created_at  timestamp(6),
    created_by  varchar(50),
    updated_at  timestamp(6),
    updated_by  varchar(50),
    version     int signed    not null default 0
) engine = InnoDB;

create index idx_orders_created_at on orders (created_at asc);