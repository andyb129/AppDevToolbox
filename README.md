# AppDevToolbox

A collection of tools for Android app development in one place.

:construction_worker: :hammer: NOTE: This is still a work in progress :hammer: :construction_worker:

<p>
<a href="https://play.google.com/store/apps/details?id=uk.co.barbuzz.appdevtoolbox"><img src="https://github.com/andyb129/AppDevToolkit/blob/master/screenshots/google_play_badge.png" height="80" width="210" alt="AppDevToolkit"/></a>
</p>

<table border="0">
<tr>
<td>
<img src="https://github.com/andyb129/AppDevToolkit/blob/master/screenshots/appdevtoolkit_screen_1.png" alt="App Dev Toolkit Screen 1"/>
</td>
<td>
<img src="https://github.com/andyb129/AppDevToolkit/blob/master/screenshots/appdevtoolkit_screen_2.png" alt="App Dev Toolkit Screen 2"/>
</td>
<td>
<img src="https://github.com/andyb129/AppDevToolkit/blob/master/screenshots/appdevtoolkit_screen_3.png" alt="App Dev Toolkit Screen 3"/>
</td>
<td>
<img src="https://github.com/andyb129/AppDevToolkit/blob/master/screenshots/appdevtoolkit_screen_4.png" alt="App Dev Toolkit Screen 4"/>
</td>
<td>
<img src="https://github.com/andyb129/AppDevToolkit/blob/master/screenshots/appdevtoolkit_screen_6.png" alt="App Dev Toolkit Screen 5"/>
</td>
<td>
<img src="https://github.com/andyb129/AppDevToolkit/blob/master/screenshots/appdevtoolkit_screen_5.png" alt="App Dev Toolkit Screen 6"/>
</td>
</tr>
</table>

This app brings together the following amazing Android development apps into one. (Thanks to all for their brilliant tools)

I've also tweeked a few parts (e.g. language button on widget) and added some other features.

* **Device Stats** (device info) by [scottyab](https://github.com/scottyab) - [https://github.com/scottyab/android-device-stats](https://github.com/scottyab/android-device-stats)
* **Material Keylines** (layout overlay) by [esnaultdev](https://github.com/esnaultdev) - [https://github.com/esnaultdev/MaterialKeylines](https://github.com/esnaultdev/MaterialKeylines)
* **Venom** (process killer) by [YarikSOffice](https://github.com/YarikSOffice) - [https://github.com/YarikSOffice/venom](https://github.com/YarikSOffice/venom)
* **Dev Widget** (app widget) by [tasomaniac](https://github.com/tasomaniac) - [https://github.com/tasomaniac/DevWidget](https://github.com/tasomaniac/DevWidget)
* **Dev Tiles** (notification tiles for dev settings) by [mustafa01ali](https://github.com/mustafa01ali) - [https://github.com/mustafa01ali/Dev-Tiles](https://github.com/mustafa01ali/Dev-Tiles)
* **Snippet** (quick text pasting) by Me! :smile: [andyb129](https://github.com/andyb129) - [https://github.com/andyb129/Snippet](https://github.com/mustafa01ali/Snippet)

### PERMISSIONS

To use the DEV TILES part of the app remember to launch these ADB shell commands to allow the app to access the dev settings

```
adb shell pm grant uk.co.barbuzz.appdevtoolbox android.permission.WRITE_SECURE_SETTINGS

adb shell pm grant uk.co.barbuzz.appdevtoolbox android.permission.DUMP
```

### TODO

1. This is my first attempt at this and I hope to improve it over time.
3. I will try to add any other useful tools/shortcuts as I find them

### Licence
```
Copyright (c) 2017 Andy Barber

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
