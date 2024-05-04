-- create table
create table loans
(
    id                 bigserial               not null primary key,
    user_id            bigint                  not null,
    name               varchar(100)            not null,
    description        text,
    start_date         date                    not null,
    end_date           date                    not null,
    interest_rate      real                    not null,
    type               varchar(50)             not null,
    loan_amount        bigint                  not null,
    remaining_amount   bigint    default 0     not null,
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
    amount             bigint                  not null,
    created_date       timestamp default now() not null,
    last_modified_date timestamp,

    foreign key (loan_id) references loans (id)
);
