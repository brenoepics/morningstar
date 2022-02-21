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

-- ----------------------------
-- Table structure for calendar_campaigns
-- ----------------------------
DROP TABLE IF EXISTS `calendar_campaigns`;
CREATE TABLE `calendar_campaigns` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL DEFAULT '',
    `image` varchar(255) NOT NULL DEFAULT '',
    `start_timestamp` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `total_days` int NOT NULL DEFAULT '30',
    `lock_expired` enum('1','0') NOT NULL DEFAULT '1',
    `enabled` enum('1','0') NOT NULL DEFAULT '1',
     UNIQUE KEY `id` (`id`)
);

-- ----------------------------
-- Records of calendar_campaigns
-- ----------------------------
INSERT INTO `calendar_campaigns` VALUES ('1', 'test', '', '2022-02-09 16:49:13', '31', '1', '1');

-- ----------------------------
-- Table structure for calendar_rewards
-- ----------------------------
DROP TABLE IF EXISTS `calendar_rewards`;
CREATE TABLE `calendar_rewards` (
     `id` int NOT NULL AUTO_INCREMENT,
     `campaign_id` int NOT NULL DEFAULT '0',
     `product_name` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
     `custom_image` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
     `credits` int NOT NULL DEFAULT '0',
     `pixels` int NOT NULL DEFAULT '0',
     `points` int NOT NULL DEFAULT '0',
     `points_type` int NOT NULL DEFAULT '0',
     `badge` varchar(25) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
     `item_id` int NOT NULL DEFAULT '0',
     `subscription_type` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT '',
     `subscription_days` int NOT NULL DEFAULT '0',
     PRIMARY KEY (`id`) USING BTREE
);

-- ----------------------------
-- Records of calendar_rewards
-- ----------------------------
INSERT INTO `calendar_rewards` VALUES ('144', '1', '%credits%credit_prize_14', 'client_static/advent_extras_coins.png', '1', '0', '0', '0', '', '0', null, '0');
INSERT INTO `calendar_rewards` VALUES ('145', '1', '%credits%credit_prize_14', 'client_static/advent_extras_coins.png', '2', '0', '0', '0', '', '0', null, '0');
INSERT INTO `calendar_rewards` VALUES ('143', '1', 'duckets_%pixels%', 'client_static/advent_extras_ducket.png', '0', '200', '0', '0', '', '0', null, '0');
INSERT INTO `calendar_rewards` VALUES ('142', '1', 'duckets_%pixels%', 'client_static/advent_extras_ducket.png', '0', '100', '0', '0', '', '0', null, '0');
INSERT INTO `calendar_rewards` VALUES ('146', '1', '%item%', '', '0', '0', '0', '0', '', '3030', null, '0');
INSERT INTO `calendar_rewards` VALUES ('147', '1', 'val_hSeat_3', '', '0', '0', '0', '0', '', '3388', null, '0');
INSERT INTO `calendar_rewards` VALUES ('148', '1', '%item%', '', '0', '0', '0', '0', '', '3371', null, '0');
INSERT INTO `calendar_rewards` VALUES ('149', '1', '%item%', '', '0', '0', '0', '0', '', '10481', null, '0');
INSERT INTO `calendar_rewards` VALUES ('150', '1', '%item%', '', '0', '0', '0', '0', '', '11259', null, '0');
INSERT INTO `calendar_rewards` VALUES ('151', '1', '%item%', '', '0', '0', '0', '0', '', '4432', null, '0');
INSERT INTO `calendar_rewards` VALUES ('152', '1', '%item%', '', '0', '0', '0', '0', '', '44480', null, '0');
INSERT INTO `calendar_rewards` VALUES ('153', '1', '%item%', '', '0', '0', '0', '0', '', '9951', null, '0');
INSERT INTO `calendar_rewards` VALUES ('154', '1', '%item%', '', '0', '0', '0', '0', '', '4926', null, '0');
INSERT INTO `calendar_rewards` VALUES ('155', '1', '%subscription_type%_VIP_%subscription_days%_DAYS', 'client_static/advent_extras_hc.png', '0', '0', '0', '0', '', '0', 'HABBO_CLUB', '3');
INSERT INTO `calendar_rewards` VALUES ('1', '1', '%subscription_type%_VIP_%subscription_days%_DAY', 'client_static/advent_extras_hc.png', '0', '0', '0', '0', '', '0', 'HABBO_CLUB', '1');
INSERT INTO `calendar_rewards` VALUES ('3', '1', 'xmas_c19_unicornfigure2', '', '0', '0', '0', '0', '', '10929', null, '0');

-- ----------------------------
-- Table structure for calendar_rewards_claimed
-- ----------------------------
DROP TABLE IF EXISTS `calendar_rewards_claimed`;
CREATE TABLE `calendar_rewards_claimed` (
        `user_id` int NOT NULL,
        `campaign_id` int NOT NULL DEFAULT '0',
        `day` int NOT NULL,
        `reward_id` int NOT NULL,
        `timestamp` timestamp NOT NULL
);

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.calendar.default', 'test');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.calendar.pixels.hc_modifier', '2.0');

-- Calendar force open
ALTER TABLE `permissions` ADD COLUMN `acc_calendar_force` enum('0','1') NULL DEFAULT '0';

-- UpdateCalendar command.
ALTER TABLE `permissions` ADD `cmd_update_calendar` ENUM('0', '1') NOT NULL DEFAULT '0';
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_update_calendar', ':update_calendar'), ('commands.keys.cmd_update_calendar', 'update_calendar');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.success.cmd_update_calendar', 'Calendar updated successfully!');

