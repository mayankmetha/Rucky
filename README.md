# Rucky 
[![GitHub](https://img.shields.io/github/license/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/blob/master/LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/releases/latest)
[![Crowdin](https://badges.crowdin.net/rucky/localized.svg)](https://mayankmetha.github.io/Rucky/)
[![Android](https://img.shields.io/badge/android-6.x%2B-lightgrey)](https://github.com/mayankmetha/Rucky)
[![Architecture](https://img.shields.io/badge/architecture-Independent-blueviolet)](https://github.com/mayankmetha/Rucky)
[![Kernel](https://img.shields.io/badge/kernel-USB%20HID%20Patch%20Required-red)](https://github.com/mayankmetha/Rucky)

## Readme
An android app to perform USB HID Attacks (Rubber Duck) in multiple ways:
- Wired Mode: Needs a custom kernel with usb hid feature or a kernel with configfs to be enabled for this mode to be used.
- Wireless: Provides a socket server to extend this USB HID with external tools/hardwares like raspberry pi, socket services, nc, and much more.

## Features 
| Status           | Feature                                                           |
| :---:            | :---                                                              |
|:white_check_mark:| USB HID (Root & custom kernel required)                           |
|:white_check_mark:| USB HID over network socket                                       |
|:x:               | USB HID over bluetooth                                            |
|:white_check_mark:| USB connection detection                                          |
|:white_check_mark:| Run attack UI button                                              |
|:white_check_mark:| Autolaunch attack on USB connect                                  |
|:white_check_mark:| Autolaunch attack on network socket connect                       |
|:white_check_mark:| Save scripts                                                      |
|:white_check_mark:| Load scripts                                                      |
|:white_check_mark:| Delete scripts                                                    |
|:white_check_mark:| Day/Night theme                                                   |
|:white_check_mark:| Customize accent colours                                          |
|:white_check_mark:| Launch app via dialer app                                         |
|:white_check_mark:| Hide launcher app (till Android 10)                               |
|:white_check_mark:| Disable app launch via launcher (Android 11 onwards)              |
|:white_check_mark:| Biometric support                                                 |
|:white_check_mark:| App lock support                                                  |
|:white_check_mark:| Encrypt scripts on save                                           |
|:white_check_mark:| Decrypt scripts on load                                           |
|:white_check_mark:| Customize network socket address                                  |
|:white_check_mark:| In-app update service (GitHub release only)                       |
|:white_check_mark:| Compactabile with Kali Linux NetHunter                            |
|:x:               | ConfigFS auto configuration                                       |
|:white_check_mark:| Multiple HID languages support                                    |
|:construction:    | Customizable HID for additional language support                  |
|:white_check_mark:| Platform independent HID support                                  |
|:white_check_mark:| Can attack virtually any system with USB HID support              |
|:x:               | USB HID mouse support (awaiting Hak5 Rubber Duck spec to support) |
|:x:               | Disable app level virtualization (parallel app prevention)        |
|:x:               | App anti-tamper detection                                         |
|:x:               | Runtime process name masking                                      |
|:x:               | Memory dump prevention                                            |
|:x:               | Anti-debug protection (Frida probe prevention)                    |
|:bulb:            | More feature addition on request                                  |

## Featuring Rucky
- [Kali Linux 2019.3 Release](https://www.kali.org/blog/kali-linux-2019-3-release/)
- [Prog.World](https://prog.world/kali-linux-nethunter-on-android-part-3-breaking-the-distance/)
- [ProgrammerSought](https://www.programmersought.com/article/30497171179/)
- [Dark_Mechanic YouTube Channel](https://youtu.be/ic-X-FCLNk8)
- [AV SHIVA NORO YouTube Channel](https://youtu.be/4clbu41cEQ0)
- [Android Infosec YouTube Channel](https://www.youtube.com/watch?v=_NDXzGPh_BQ)
- [Android Infosec Facebook Post](https://www.facebook.com/AndroidInfoSec/posts/4101537619869708)

## Legacy Work
[![RPi](https://img.shields.io/badge/Raspberry%20Pi-0%20W-maroon)](https://github.com/mayankmetha/Rucky-Ext-RPi)
[![HID](https://img.shields.io/badge/Project-Legacy%20HID-lightgreen)](https://github.com/mayankmetha/Rucky-Legacy-HID)
[![Android](https://img.shields.io/badge/android-4.4.x-green)](https://github.com/mayankmetha/Rucky/releases/tag/1.9)
[![Android](https://img.shields.io/badge/android-5.x-green)](https://github.com/mayankmetha/Rucky/releases/tag/1.9)
