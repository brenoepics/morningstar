INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.place.under', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.custom.enabled', '0');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_stalk.forgot_username', 'Specify the username of the Habbo you want to follow!');

-- Enable or Disable TTY in console (Default is enabled)
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('console.mode', '1');

-- Camera update how habbo saves pictures
UPDATE `emulator_settings` SET `value` = '{\"n\":\"%username%\", \"s\":\"%userid%\", \"u\":\"%id%\", \"t\":\"%timestamp%\", \"m\":\"This Picture was taken in room: %room_id%\", \"w\":\"%url%\"}' WHERE `emulator_settings`.`key` = 'camera.extradata';
