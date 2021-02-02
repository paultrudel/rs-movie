DROP SCHEMA IF EXISTS `keycloak`;
CREATE SCHEMA `keycloak`;

DROP SCHEMA IF EXISTS `rs-movie`;
CREATE SCHEMA `rs-movie`;
USE `rs-movie`;

CREATE TABLE IF NOT EXISTS `rs-movie`.`community` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
    `community_number` BIGINT DEFAULT NULL,
	PRIMARY KEY (`id`),
    UNIQUE (`community_number`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `rs-movie`.`topic` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `rs-movie`.`movie` (
	`id` VARCHAR(50) NOT NULL,
	`score` DOUBLE DEFAULT NULL,
	`topic_id` BIGINT,
	PRIMARY KEY (`id`),
	KEY `fk_movie_topic` (`topic_id`),
	CONSTRAINT `fk_movie_topic` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `rs-movie`.`user` (
	`id` VARCHAR(50) NOT NULL,
    `username` VARCHAR(30) DEFAULT NULL,
    `avg_score` DOUBLE DEFAULT NULL,
	`community_id` BIGINT,
	PRIMARY KEY (`id`),
    UNIQUE (`username`),
	KEY `fk_user_community` (`community_id`),
	CONSTRAINT `fk_user_community` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `rs-movie`.`review` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`score` SMALLINT DEFAULT NULL,
    `summary` MEDIUMTEXT DEFAULT NULL,
	`content` MEDIUMTEXT DEFAULT NULL,
	`movie_id` VARCHAR(50) NOT NULL,
	`user_id`  VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`),
	KEY `fk_review_movie` (`movie_id`),
	KEY `fk_review_user` (`user_id`),
	CONSTRAINT `fk_review_movie` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`),
	CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT	EXISTS `rs-movie`.`prediction` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
    `score` DOUBLE DEFAULT NULL,
    `movie_id` VARCHAR(50) NOT NULL,
    `user_id` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_prediction_movie` (`movie_id`),
    KEY `fk_prediction_user` (`user_id`),
    CONSTRAINT `fk_prediction_movie` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`),
    CONSTRAINT `fk_prediction_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `rs-movie`.`term` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`term_rank` MEDIUMINT DEFAULT NULL,
	`value` VARCHAR(1023) DEFAULT NULL,
	`topic_id` BIGINT,
	PRIMARY KEY (`id`),
	KEY `fk_term_topic` (`topic_id`),
	CONSTRAINT `fk_term_topic` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `rs-movie`.`community_topic` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`community_id` BIGINT,
	`topic_id` BIGINT,
	PRIMARY KEY (`id`),
	KEY `fk_community_topic` (`community_id`),
	KEY `fk_topic_community` (`topic_id`),
	CONSTRAINT `fk_community_topic` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`),
	CONSTRAINT `fk_topic_community` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS `rs-movie`.`user_topic` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`user_id` VARCHAR(50),
	`topic_id` BIGINT,
	PRIMARY KEY (`id`),
	KEY `fk_user_topic` (`user_id`),
	KEY `fk_topic_user` (`topic_id`),
	CONSTRAINT `fk_user_topic` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
	CONSTRAINT `fk_topic_user` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`)
)
ENGINE=InnoDB
AUTO_INCREMENT = 1;
