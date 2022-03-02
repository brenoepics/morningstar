INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.place.under', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.custom.enabled', '0');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_stalk.forgot_username', 'Specify the username of the Habbo you want to follow!');

-- Enable or Disable TTY in console (Default is enabled)
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('console.mode', '1');

-- Add friendship categories table
CREATE TABLE `messenger_categories` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(25) NOT NULL,
    `user_id` int NOT NULL,
    UNIQUE KEY `identifier` (`id`)
);

-- Set an ID (tinyint[3]) from category list items
ALTER TABLE messenger_friendships ADD category TINYINT(3) NOT NULL DEFAULT '0' AFTER friends_since;