create table order_items
(
    id         bigint signed           not null primary key auto_increment,
    order_id   bigint signed           not null,
    item_id    bigint signed           not null,
    quantity   int signed              not null,
    price      double precision signed not null,
    created_at timestamp(6),
    created_by varchar(50),
    updated_at timestamp(6),
    updated_by varchar(50),
    version    int signed              not null default 0,
    index (order_id),
    foreign key (order_id) references orders (id),
    foreign key (item_id) references items (id),
    unique index (order_id, item_id)
) engine = InnoDB;

create index idx_order_items_created_at on order_items (created_at asc);