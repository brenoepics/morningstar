INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.place.under', '0');
INSERT INTO `emulator_settings`(`key`, `value`) VALUES ('wired.custom.enabled', '0');

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_stalk.forgot_username', 'Specify the username of the Habbo you want to follow!');

-- Enable or Disable TTY in console (Default is enabled)
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('console.mode', '1');

-- Habbo uses "png"-files for badges since 2019. You can change it back to "gif" (default is png)
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.catalogue.badgeimage.type', 'png');
