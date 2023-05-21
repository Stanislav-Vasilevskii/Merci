create table user_points
(
    id         UUID,
    version    BIGINT not null default 0,
    user_id    UUID,
    amount     INTEGER not null,
    is_deleted BOOLEAN not null,
    constraint user_points_pk
        primary key (id),
    constraint user_points_pk2
        unique (user_id),
    constraint check_amount
        check (amount >= 0)
);