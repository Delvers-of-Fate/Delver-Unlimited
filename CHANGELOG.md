# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).

## [1.3.0] - 2017-07-28
### Added
- Support for DelvEdit!
- The fade in the main menu is disabled if the user is skipping the intro videos
- Load strait into a save file using `--save=<int>`, must be used with `--skipintro`
- You can now un-select a save in the main menu!

### Changed
- Major debug overlay overhaul
- Implemented healing ability in debug overlay
- Replaced `DONE` with `BACK` in the debug overlay, and selecting something no longer closes the overlay
- The `escape` and `enter` key behaviour has been changed
- A lot of minor cleanup!

### Fixed
- Fixed exiting the game screen and erasing save files for people with low resolutions

## [1.2.0] - 2017-06-08
### Added
- The folder `save` will now be created if it doesn't exist.
- Added confirmation to close game in main menu.
- The escape button is now handled differently in the menus, not closing the game.
- The amount of wins will now be displayed even if the player doesn't have one.

### Changed
- Now targeting Java 1.6 instead of 1.8 for compatibility with base game.
- Improved borderless mode (changed default location for window).
- A lot of cleanup!
- Re-vamped the main menu! Erase save button moved and all buttons now have fancy colors.

## [1.1.0] - 2017-04-21
### Added
- Added exit button on main screen.
- Source is now included in the `src` folder.

### Changed
- Moved to translatable strings.

## [1.0.0] - 2017-04-13
I am happy with the current state of the mod, so here is a v1 version of the mod finally released!
### Added
- Skippable intro with `--skipintro`.
- You now have to confirm a deletion of a save file, finally got that working correctly.
- Tracking for testing builds (internal boolean).

## [0.5.0] - 2017-04-06
### Added
- Borderless mode (as close as it gets), can be enabled by the `--borderless` command line argument and requires you to disable fullscreen ingame.
- Added game icon.
- Support for MSAA (forced off?).
- Updated to latest version of game (Update 14).
- If the debug mode is enabled, it will now display all the command line arguments passed.
- Option to skip Steamworks.

### Changed
- Minor changes.

## [0.4.0] - 2017-01-22
### Added
- Added support to force a custom width and height.

### Changed
- Removed config file system, and replaced with command line arguments. Type --help for available commands.

## [0.3.0] - 2016-12-15
### Added
- Configuration file, Delver-Unlimited.properties will now be created. You can modify the fps limit and a toggle for V-Sync.
- Warning message that game is modded.

## [0.2.0] - 2016-12-12
### Added
- Unlocked FPS to 250.

## [0.1.0] - 2016-12-12
Initial public release.
### Added
- Disabled V-Sync.
