# ![Octomoji](src/main/resources/META-INF/pluginIcon.png) GitHub Markdown Emojis

> A plugin for the IntelliJ platform to instantly preview Emojis of GitHub-flavoured markdown.

## Table of Contents

* [Screenshot](#camera-screenshot)
* [HowTo](#book-howto)
  * [Install](#gear-install)
  * [Use](#partying_face-use)
  * [Build](#hammer-build)
* [License](#balance_scale-license)
* [References](#link-references)
* [Credits](#star-credits)
* [Disclaimer](#point_up-disclaimer)
* [Donate](#heart-donate)

## :camera: Screenshot

![screenshot](screenshot.png)

## :book: HowTo

### :gear: Install

Use the IDE's built-in plugin system:

* `File` --> `Settings...` --> `Plugins` --> `Marketplace`
* search for: `GitHub Markdown Emojis`
* click the `Install`-button

Or go to the [plugin page](https://plugins.jetbrains.com/plugin/20705) on the [JetBrains](https://www.jetbrains.com)-website, download the archive-file and install manually.

### :partying_face: Use

There's nothing more to set up.  
Simply use the GitHub-Emojis (e.g. `:smile:`) in your markdown file and active the preview window/pane.  
You should see the rendered images instead of just plain text.

NEW :heavy_check_mark: ...  
The toolbar window will show you all available Emojis. Clicking on an Emoji will put the shortcode into the clipboard.

### :hammer: Build

* clone the repo:  
`git clone https://github.com/4ch1m/github-markdown-emojis.git`
* change directory:  
`cd github-markdown-emojis`
* use the [Gradle](https://gradle.org)-wrapper to build the plugin:  
`./gradlew buildPlugin`
* a deployable ZIP file can then be found here:  
`./build/distributions/GitHub Markdown Emojis-*.zip`

## :balance_scale: License

Please read the [license](LICENSE) file.

## :link: References

* [Github Emoji API](https://api.github.com/emojis)
* [Emoji Cheat Sheet](https://github.com/ikatyang/emoji-cheat-sheet)

## :star: Credits

* _Smiley_ - Microsoft's [Fluent Emoji](https://github.com/microsoft/fluentui-emoji) collection
* _Octocat_ - based on [johan](https://gist.github.com/johan/1007813)'s SVG
* _Heart_ icon - courtesy of [FontAwesome](https://fontawesome.com/icons/heart?s=solid&f=classic)

## :point_up: Disclaimer

This project is not affiliated with or endorsed by [GitHub](https://github.com).

## :heart: Donate

If you like this plugin, please consider a [donation](https://paypal.me/AchimSeufert). Thank you!
