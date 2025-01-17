# 일일 정산 테이블
create table SettleDetail
(
    id         bigint auto_increment
        primary key,
    customerId bigint null,
    serviceId  bigint null,
    count      bigint null,
    fee        bigint null,
    targetDate date   null
);

# 주간 정산 테이블
create table SettleGroup
(
    id         bigint auto_increment
        primary key,
    customerId bigint   null,
    serviceId  bigint   null,
    totalCount bigint   null,
    totalFee   bigint   null,
    createdAt  datetime null
);