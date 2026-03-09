<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# JSON Keypath Navigator Changelog

## [Unreleased]

## [0.0.1] - 2026-03-07
### Added
- Navigate to any JSON element by keypath using `Ctrl+Shift+K`
- Autocomplete dialog with live filtering of all keypaths in the current file
- Array support using numeric indices (e.g. `foo.items.0.name`)
- Status bar widget showing the keypath of the element under the cursor
- Click the status bar widget to copy the current keypath to the clipboard
- Navigation history — dialog remembers the last 10 used keypaths