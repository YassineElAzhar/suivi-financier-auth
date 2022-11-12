
/*We drop tables */
DROP TABLE password;
DROP TABLE secure_question;
DROP TABLE secure_response;
DROP TABLE token;
DROP TABLE type_profile;
DROP TABLE "user";




/*We create tables*/
CREATE TABLE password (
  id serial PRIMARY KEY,
  user_id varchar(8) NOT NULL,
  password varchar(128) NOT NULL,
  date_creation date NOT NULL,
  date_modification date NOT NULL
);


/*We create tables*/
CREATE TABLE secure_question (
  id serial PRIMARY KEY,
  question varchar(130) NOT NULL,
  user_id int NOT NULL,
  date_creation date NOT NULL,
  date_modification date NOT NULL
);


/*We create tables*/
CREATE TABLE secure_response (
  id serial PRIMARY KEY,
  user_id int NOT NULL,
  secure_question_id int NOT NULL,
  response text NOT NULL,
  date_creation date NOT NULL,
  date_modification date NOT NULL,
  question varchar(255) DEFAULT NULL
);


/*We create tables*/
CREATE TABLE token (
  id serial PRIMARY KEY,
  user_id varchar(8) NOT NULL,
  token_context varchar(128) NOT NULL,
  token text NOT NULL,
  date_creation date NOT NULL,
  date_modification date NOT NULL
);

/*We create tables*/
CREATE TABLE type_profile (
  id serial PRIMARY KEY,
  type_profile varchar(32) NOT NULL,
  date_creation date NOT NULL,
  date_modification date NOT NULL
);


/*We create tables*/
CREATE TABLE "user" (
  id serial PRIMARY KEY,
  nom varchar(32) NOT NULL,
  prenom varchar(32) NOT NULL,
  email varchar(128) NOT NULL,
  date_naissance varchar(32) NOT NULL,
  adresse text NOT NULL,
  ville varchar(64) NOT NULL,
  zip varchar(16) NOT NULL,
  type_profil int NOT NULL,
  actif int NOT NULL,
  password int NOT NULL,
  date_creation date NOT NULL,
  date_modification date NOT NULL
);