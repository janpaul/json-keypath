# JSON Keypath Navigator

![Build](https://github.com/janpaul/json-keypath/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

Navigate deeply nested JSON files with ease using dot-separated keypaths.

<!-- Plugin description -->
Navigate to any element in a JSON file using a dot-separated keypath like `foo.bar.baz` — think XPath, but for JSON.

### Features

- **Navigate by keypath** — open the keypath dialog via `Ctrl+Shift+K` (or `Navigate → Go to JSON Keypath`), type a keypath and jump straight to it
- **Autocomplete** — the dialog shows all available keypaths in the current file with live filtering as you type
- **Array support** — navigate into arrays using numeric indices, e.g. `foo.items.0.name`
- **Status bar widget** — shows the keypath of the element under your cursor at all times
- **Copy to clipboard** — click the status bar widget to copy the current keypath to your clipboard
- **History** — the dialog remembers your last 10 navigations

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "JSON Keypath Navigator"</kbd> > <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/janpaul/json-keypath/releases/latest) and install via
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).