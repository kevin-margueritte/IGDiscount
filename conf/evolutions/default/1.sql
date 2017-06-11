# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Downs

alter table if exists admin drop constraint if exists fk_admin_token_authentification_id;

alter table if exists admin drop constraint if exists fk_admin_token_reinitialisation_email_id;

alter table if exists basket_row drop constraint if exists fk_basket_row_product_id;
drop index if exists ix_basket_row_product_id;

alter table if exists basket_row drop constraint if exists fk_basket_row_simple_user_id;
drop index if exists ix_basket_row_simple_user_id;

alter table if exists image drop constraint if exists fk_image_product_id;

alter table if exists orderproduct drop constraint if exists fk_orderproduct_product_id;
drop index if exists ix_orderproduct_product_id;

alter table if exists orderproduct drop constraint if exists fk_orderproduct_simple_user_id;
drop index if exists ix_orderproduct_simple_user_id;

alter table if exists product drop constraint if exists fk_product_seller_company_id;
drop index if exists ix_product_seller_company_id;

alter table if exists product drop constraint if exists fk_product_image_id;

alter table if exists seller_company drop constraint if exists fk_seller_company_token_authentification_id;

alter table if exists seller_company drop constraint if exists fk_seller_company_token_reinitialisation_email_id;

alter table if exists simple_user drop constraint if exists fk_simple_user_token_authentification_id;

alter table if exists simple_user drop constraint if exists fk_simple_user_token_reinitialisation_email_id;

drop table if exists admin cascade;

drop table if exists basket_row cascade;

drop table if exists image cascade;

drop table if exists orderproduct cascade;

drop table if exists product cascade;

drop table if exists seller_company cascade;

drop table if exists simple_user cascade;

drop table if exists token cascade;

# --- !Ups

create table admin (
  id                            bigserial not null,
  email                         varchar(255),
  password                      varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  token_authentification_id     bigint,
  token_reinitialisation_email_id bigint,
  constraint uq_admin_email unique (email),
  constraint uq_admin_token_authentification_id unique (token_authentification_id),
  constraint uq_admin_token_reinitialisation_email_id unique (token_reinitialisation_email_id),
  constraint pk_admin primary key (id)
);

create table basket_row (
  id                            bigserial not null,
  quantity                      integer,
  product_id                    bigint,
  simple_user_id                bigint,
  constraint uq_basket_row_product_id_simple_user_id unique (product_id,simple_user_id),
  constraint pk_basket_row primary key (id)
);

create table image (
  id                            bigserial not null,
  name                          varchar(255),
  mime                          varchar(255),
  content                       bytea,
  product_id                    bigint,
  constraint uq_image_product_id unique (product_id),
  constraint pk_image primary key (id)
);

create table orderproduct (
  id                            bigserial not null,
  quantity                      integer,
  state_date                    timestamp,
  state                         varchar(255),
  price_order                   float,
  product_id                    bigint,
  simple_user_id                bigint,
  constraint pk_orderproduct primary key (id)
);

create table product (
  id                            bigserial not null,
  description                   varchar(1000),
  name                          varchar(255),
  price                         float,
  quantity                      bigint,
  available                     boolean,
  seller_company_id             bigint,
  image_id                      bigint,
  constraint uq_product_image_id unique (image_id),
  constraint pk_product primary key (id)
);

create table seller_company (
  id                            bigserial not null,
  email                         varchar(255),
  password                      varchar(255),
  postal_code                   varchar(255),
  street                        varchar(255),
  city                          varchar(255),
  street_number                 varchar(255),
  siret                         varchar(255),
  company_name                  varchar(255),
  token_authentification_id     bigint,
  token_reinitialisation_email_id bigint,
  constraint uq_seller_company_email unique (email),
  constraint uq_seller_company_token_authentification_id unique (token_authentification_id),
  constraint uq_seller_company_token_reinitialisation_email_id unique (token_reinitialisation_email_id),
  constraint pk_seller_company primary key (id)
);

create table simple_user (
  id                            bigserial not null,
  email                         varchar(255),
  password                      varchar(255),
  postal_code                   varchar(255),
  street                        varchar(255),
  city                          varchar(255),
  street_number                 varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  log_facebook                  boolean,
  token_authentification_id     bigint,
  token_reinitialisation_email_id bigint,
  constraint uq_simple_user_email unique (email),
  constraint uq_simple_user_token_authentification_id unique (token_authentification_id),
  constraint uq_simple_user_token_reinitialisation_email_id unique (token_reinitialisation_email_id),
  constraint pk_simple_user primary key (id)
);

create table token (
  id                            bigserial not null,
  expiration_date               timestamp,
  token                         varchar(255),
  constraint uq_token_token unique (token),
  constraint pk_token primary key (id)
);

alter table admin add constraint fk_admin_token_authentification_id foreign key (token_authentification_id) references token (id) on delete restrict on update restrict;

alter table admin add constraint fk_admin_token_reinitialisation_email_id foreign key (token_reinitialisation_email_id) references token (id) on delete restrict on update restrict;

alter table basket_row add constraint fk_basket_row_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_basket_row_product_id on basket_row (product_id);

alter table basket_row add constraint fk_basket_row_simple_user_id foreign key (simple_user_id) references simple_user (id) on delete restrict on update restrict;
create index ix_basket_row_simple_user_id on basket_row (simple_user_id);

alter table image add constraint fk_image_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;

alter table orderproduct add constraint fk_orderproduct_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_orderproduct_product_id on orderproduct (product_id);

alter table orderproduct add constraint fk_orderproduct_simple_user_id foreign key (simple_user_id) references simple_user (id) on delete restrict on update restrict;
create index ix_orderproduct_simple_user_id on orderproduct (simple_user_id);

alter table product add constraint fk_product_seller_company_id foreign key (seller_company_id) references seller_company (id) on delete restrict on update restrict;
create index ix_product_seller_company_id on product (seller_company_id);

alter table product add constraint fk_product_image_id foreign key (image_id) references image (id) on delete restrict on update restrict;

alter table seller_company add constraint fk_seller_company_token_authentification_id foreign key (token_authentification_id) references token (id) on delete restrict on update restrict;

alter table seller_company add constraint fk_seller_company_token_reinitialisation_email_id foreign key (token_reinitialisation_email_id) references token (id) on delete restrict on update restrict;

alter table simple_user add constraint fk_simple_user_token_authentification_id foreign key (token_authentification_id) references token (id) on delete restrict on update restrict;

alter table simple_user add constraint fk_simple_user_token_reinitialisation_email_id foreign key (token_reinitialisation_email_id) references token (id) on delete restrict on update restrict;



