-- create table
create table loans
(
    id                 bigserial               not null primary key,
    user_id            bigint                  not null,
    name               varchar(100)            not null,
    description        text,
    start_date         date                    not null,
    end_date           date                    not null,
    repayment_date     int                     not null,
    interest_rate      real      default 0     not null,
    repayment_type     varchar(50)             not null,
    total_balance      bigint                  not null,
    repayment_count    bigint    default 0     not null,
    ending_balance     bigint    default 0     not null,
    created_date       timestamp default now() not null,
    created_by         varchar(50)             not null,
    last_modified_date timestamp,
    last_modified_by   varchar(50),

    foreign key (user_id) references users (id)
);

create table loans_repayment_histories
(
    id                 bigserial               not null primary key,
    loan_id            bigint                  not null,
    principal          bigint                  not null,
    interest           bigint                  not null,
    created_date       timestamp default now() not null,
    last_modified_date timestamp,

    foreign key (loan_id) references loans (id)
);
