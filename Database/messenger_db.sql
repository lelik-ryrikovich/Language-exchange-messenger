/*==============================================================*/
/* DBMS name:      PostgreSQL 9.x                               */
/* Created on:     12.04.2025 22:17:37                          */
/*==============================================================*/


/*==============================================================*/
/* Table: Chat                                                  */
/*==============================================================*/
create table Chat (
   chat_id              SERIAL               not null,
   chat_name            VARCHAR(74)          null,
   constraint PK_CHAT primary key (chat_id),
   constraint chk_chat_name CHECK (chat_name !~ '^\s*$' OR chat_name IS NULL)
);

/*==============================================================*/
/* Table: Chat_Member                                           */
/*==============================================================*/
create table Chat_Member (
   Chat                 INT4                 not null,
   App_User             INT4                 not null,
   constraint PK_CHAT_MEMBER primary key (Chat, App_User)
);

/*==============================================================*/
/* Table: City                                                  */
/*==============================================================*/
create table City (
   city_id              SERIAL               not null,
   city_name            VARCHAR(32)          null,
   Country              VARCHAR(32)          null,
   constraint PK_CITY primary key (city_id)
);

/*==============================================================*/
/* Table: Country                                               */
/*==============================================================*/
create table Country (
   country_name         VARCHAR(32)          not null,
   constraint PK_COUNTRY primary key (country_name),
   constraint chk_country_name CHECK (country_name ~ '^[A-Za-zА-Яа-я\s-]+$')
);

/*==============================================================*/
/* Table: Lang_For_Teach                                        */
/*==============================================================*/
create table Lang_For_Teach (
   App_User             INT4                 not null,
   Language             VARCHAR(32)          not null,
   Proficiency_Level    VARCHAR(32)          not null,
   constraint PK_LANG_FOR_TEACH primary key (App_User, Language),
   constraint uk_appuser_language_teach UNIQUE (App_User, Language),
   constraint chk_proficiency_level CHECK (Proficiency_Level IN ('C1', 'C2', 'Native'))
);

/*==============================================================*/
/* Table: Lang_To_Learn                                         */
/*==============================================================*/
create table Lang_To_Learn (
   App_User             INT4                 not null,
   Language             VARCHAR(32)          not null,
   Proficiency_Level    VARCHAR(32)          not null,
   constraint PK_LANG_TO_LEARN primary key (App_User, Language),
   constraint uk_appuser_language_learn UNIQUE (App_User, Language),
   constraint chk_proficiency_level CHECK (Proficiency_Level IN ('A1', 'A2', 'B1', 'B2', 'C1', 'C2'))
);

/*==============================================================*/
/* Table: Language                                              */
/*==============================================================*/
create table Language (
   language_name        VARCHAR(32)          not null,
   constraint PK_LANGUAGE primary key (language_name),
   constraint chk_language_name CHECK (language_name ~ '^[A-Za-zА-Яа-я\s-]+$')
);

/*==============================================================*/
/* Table: Message                                               */
/*==============================================================*/
create table Message (
   message_id           SERIAL               not null,
   Sender               INT4                 not null,
   Chat                 INT4                 not null,
   date_of_sending      TIMESTAMP WITH TIME ZONE not null,
   encrypted_text       TEXT                 null,
   encrypted_aes_key_recipient    TEXT         null,
   encrypted_aes_key_sender       TEXT         null,        
   constraint PK_MESSAGE primary key (message_id)
);

/*==============================================================*/
/* Table: Proficiency_Level                                     */
/*==============================================================*/
create table Proficiency_Level (
   proficiency_level_name VARCHAR(32)          not null,
   constraint PK_PROFICIENCY_LEVEL primary key (proficiency_level_name)
);

/*==============================================================*/
/* Table: App_User                                                */
/*==============================================================*/
create table App_User (
   app_user_id          SERIAL               not null,
   nickname             VARCHAR(32)          null,
   login                VARCHAR(32)          null,
   password             VARCHAR(255)         null,
   City                 INT4                 null,
   day_of_birth         DATE                 null,
   registration_date    DATE                 null,
   email                VARCHAR(254)         null,
   translation_language VARCHAR(10)          DEFAULT 'en',
   public_key           TEXT                 null,
   private_key          TEXT                 null,
   constraint PK_APP_USER primary key (app_user_id),
   constraint chk_appuser_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
   constraint chk_appuser_password_length CHECK (LENGTH(password) >= 8),
   constraint uk_appuser_email UNIQUE (email),
   constraint uk_appuser_login UNIQUE (login),
   constraint uk_appuser_nickname UNIQUE (nickname)
);

/*==============================================================*/
/* Foreign Keys                                                 */
/*==============================================================*/
alter table Chat_Member
   add constraint FK_CHATMEM_CHATID_CHAT foreign key (Chat)
      references Chat (chat_id)
      on delete restrict on update cascade;

alter table Chat_Member
   add constraint FK_CHATMEM_APPUSERID_USER foreign key (App_User)
      references App_User (app_user_id)
      on delete restrict on update cascade;

alter table City
   add constraint FK_CITY_COUNTRYNAME_COUNTRY foreign key (Country)
      references Country (country_name)
      on delete restrict on update cascade;

alter table Lang_For_Teach
   add constraint FK_LANGTEACH_LANGNAME_LANG foreign key (Language)
      references Language (language_name)
      on delete restrict on update cascade;

alter table Lang_For_Teach
   add constraint FK_LANGTEACH_PROFLVLNAME_PROFLVL foreign key (Proficiency_Level)
      references Proficiency_Level (proficiency_level_name)
      on delete restrict on update cascade;

alter table Lang_For_Teach
   add constraint FK_LANGTEACH_APPUSERID_USER foreign key (App_User)
      references App_User (app_user_id)
      on delete restrict on update cascade;

alter table Lang_To_Learn
   add constraint FK_LANGLEARN_LANGNAME_LANG foreign key (Language)
      references Language (language_name)
      on delete restrict on update cascade;

alter table Lang_To_Learn
   add constraint FK_LANGLEARN_PROFLVLNAME_PROFLVL foreign key (Proficiency_Level)
      references Proficiency_Level (proficiency_level_name)
      on delete restrict on update cascade;

alter table Lang_To_Learn
   add constraint FK_LANGLEARN_APPUSERID_USER foreign key (App_User)
      references App_User (app_user_id)
      on delete restrict on update cascade;

alter table Message
   add constraint FK_MESSAGE_CHATID_CHAT foreign key (Chat)
      references Chat (chat_id)
      on delete restrict on update cascade;

alter table Message
   add constraint FK_MESSAGE_USERID_SENDER foreign key (Sender)
      references App_User (app_user_id)
      on delete restrict on update cascade;

alter table App_User
   add constraint FK_APPUSER_CITYID_CITY foreign key (City)
      references City (city_id)
      on delete restrict on update cascade;