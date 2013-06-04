create table statik_user (
    username varchar(100) not null primary key,
    password varchar(100) not null,
    is_default boolean
)