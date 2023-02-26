# Rucky 
[![GitHub](https://img.shields.io/github/license/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/blob/master/LICENSE)
[![Crowdin](https://badges.crowdin.net/rucky/localized.svg)](https://crwd.in/rucky)
[![Android](https://img.shields.io/badge/android-6.x%2B-lightgrey)](https://github.com/mayankmetha/Rucky)
[![Architecture](https://img.shields.io/badge/architecture-Independent-blueviolet)](https://github.com/mayankmetha/Rucky)
[![Kernel <=3.18](https://img.shields.io/badge/kernel%20%3C%3D3.18-USB%20HID%20Patch%20Required-red)](https://github.com/mayankmetha/Rucky)
[![Kernel >=3.19](https://img.shields.io/badge/kernel%20%3E%3D3.19-ConfigFS%20Composite%20Gadget%20for%20USB%20Required-red)](https://github.com/mayankmetha/Rucky)

---

# Status
## Build
[![Debug Build](https://github.com/mayankmetha/Rucky/actions/workflows/debug_build.yml/badge.svg)](https://github.com/mayankmetha/Rucky/actions/workflows/debug_build.yml)
[![Github Nightly Build](https://github.com/mayankmetha/Rucky/actions/workflows/github_nightly_build.yml/badge.svg)](https://github.com/mayankmetha/Rucky/actions/workflows/github_nightly_build.yml)
[![Github Release Build](https://github.com/mayankmetha/Rucky/actions/workflows/github_release_build.yml/badge.svg)](https://github.com/mayankmetha/Rucky/actions/workflows/github_release_build.yml)
[![Nethunter Build](https://github.com/mayankmetha/Rucky/actions/workflows/nethunter_build.yml/badge.svg)](https://github.com/mayankmetha/Rucky/actions/workflows/nethunter_build.yml)

## Code Analysis
[![CodeQL](https://github.com/mayankmetha/Rucky/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/mayankmetha/Rucky/actions/workflows/codeql-analysis.yml)
[![mobsfscan sarif](https://github.com/mayankmetha/Rucky/actions/workflows/mobsfscan_sarif.yml/badge.svg)](https://github.com/mayankmetha/Rucky/actions/workflows/mobsfscan_sarif.yml)

---

# Download App
## GitHub
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/releases/latest)
[![Github nightly](https://img.shields.io/badge/nightly-v2.3%20(590)-blueviolet)](https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky-nightly.apk)

## Nethunter App Store
[![Nethuter Release](https://img.shields.io/badge/release-v2.2-blue)](https://store.nethunter.com/en/packages/com.mayank.rucky/)

---

# Download Magisk Module
## GitHub
<!-- [![GitHub release (latest by date)](https://img.shields.io/github/v/release/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/releases/latest) -->
[![Github nightly](https://img.shields.io/badge/nightly-v2.3%20(590)-blueviolet)](https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky.zip)

---

# Readme
An android app to perform USB HID Attacks (Rubber Duck) in multiple ways:
- <b><i>Wired Mode:</i></b> Needs a custom kernel with usb hid feature or a kernel with configfs to be enabled for this mode to be used.
- <b><i>Wireless [Deprecated]:</i></b> Provides a socket server to extend this USB HID with external tools/hardwares like raspberry pi, socket services, nc, and much more.

---

# Ducky Script

Ducky Script syntax is simple. Each command resides on a new line and may have options follow. Commands are written in ALL CAPS, because ducks are loud and like to quack with pride. Most commands invoke keystrokes, key-combos or strings of text, while some offer delays or pauses. Below is a list of commands and their function, followed by some example usage. Some syntax extended from the original [Hak5 Ducky Script Syntax](https://github.com/hak5darren/USB-Rubber-Ducky/wiki/Duckyscript). Mouse ducky scripts are different from those seen online and have been defined to keep the similarity with the keyboard ducky scripts.

<b>Note:</b> In parameters `[num]` represents a number, `[char]` represents characters A-Z, a-z. [Customizable HID](https://mayankmetha.github.io/Rucky-KeyMap/) support too has been added.

## HID2 Features
All HID2/Legacy HID commands are supported

## HID3 Features
 |        Feature         |             Sub Feature              | Supported |                                                                                           Description                                                                                            |
|:----------------------:|:------------------------------------:|:---------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|  Keystroke Injection   |     Character Keys: Alphanumeric     |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |     Character Keys: Punctuation      |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |                STRING                |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |               STRINGLN               |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |             Cursor Keys              |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |             System Keys              |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |         Basic Modifier Keys          |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |         Basic Modifier Keys          |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |         Basic Modifier Keys          |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |  Keystroke Injection   |         Basic Modifier Keys          |     ✅     |                                                 [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/keystroke-injection)                                                 |
 |        Comments        |                 REM                  |     ✅     |                                                         [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/rem)                                                         |
 |         Delays         |                DELAY                 |     ✅     |                                                        [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/ducky-script-basics/delay)                                                        |
|       The Button       |        WAIT_FOR_BUTTON_PRESS         |     ❌     |                                          [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/wait_for_button_press) Checking for feasibility                                          |
|       The Button       |              BUTTON_DEF              |     ❌     |                                          [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/wait_for_button_press) Checking for feasibility                                          |
|       The Button       |            DISABLE_BUTTON            |     ❌     |                                          [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/wait_for_button_press) Checking for feasibility                                          |
|       The Button       |            ENABLE_BUTTON             |     ❌     |                                          [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/wait_for_button_press) Checking for feasibility                                          |
|       The Button       |          Internal Variables          |     ❌     |                                          [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/wait_for_button_press) Checking for feasibility                                          |
 |        The LED         |          Default Behaviors           |     ❌     |                                                   [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/LED) Checking for feasibility                                                   |
 |        The LED         |               LED_OFF                |     ❌     |                                                   [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/LED) Checking for feasibility                                                   |
 |        The LED         |                LED_R                 |     ❌     |                                                   [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/LED) Checking for feasibility                                                   |
 |        The LED         |                LED_G                 |     ❌     |                                                   [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/LED) Checking for feasibility                                                   |
 |        The LED         |          Internal Variables          |     ❌     |                                                   [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/button/LED) Checking for feasibility                                                   |
 |      Attack Modes      |              ATTACKMODE              |    🚧     | [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/attack-modes-constants-and-variables/attack-modes) STORAGE MODE not supported as newer Android versions do not support mass storage mode |
 |      Attack Modes      |             VID and PID              |     ✅     |                                            [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/attack-modes-constants-and-variables/attack-modes)                                            |
 |      Attack Modes      |         MAN, PROD and SERIAL         |    🚧     |                       [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/attack-modes-constants-and-variables/attack-modes) SERIAL_RANDOM is currently not supported                        |
 |      Attack Modes      |           SAVE and RESTORE           |     ❌     |                                            [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/attack-modes-constants-and-variables/attack-modes)                                            |
 |      Attack Modes      |          Internal Variables          |     ❌     |                                            [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/attack-modes-constants-and-variables/attack-modes)                                            |
 |       Constants        |                DEFINE                |     ✅     |                                             [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/attack-modes-constants-and-variables/constants)                                              |
 |       Variables        |                 VAR                  |     ✅     |                                             [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/attack-modes-constants-and-variables/variables)                                              |
 |       Operators        |            Math Operators            |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/operators)                                            |
 |       Operators        |         Comparison Operators         |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/operators)                                            |
 |       Operators        |         Order of Operations          |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/operators)                                            |
 |       Operators        |          Logical Operators           |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/operators)                                            |
 |       Operators        |    Augmented Assignment Operators    |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/operators)                                            |
 |       Operators        |          Bitwise Operators           |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/operators)                                            |
 | Conditional Statements |                  IF                  |     ✅     |                                     [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/conditional-statements)                                     |
 | Conditional Statements |                 ELSE                 |     ✅     |                                     [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/conditional-statements)                                     |
 | Conditional Statements |         Nested IF Statements         |     ✅     |                                     [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/conditional-statements)                                     |
 | Conditional Statements | IF Statements with logical operators |     ✅     |                                     [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/conditional-statements)                                     |
 | Conditional Statements |      IF Statement Optimization       |     ✅     |                                     [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/conditional-statements)                                     |
 |         Loops          |                WHILE                 |     ✅     |                                             [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/loops)                                              |
 |         Loops          |            Infinite Loop             |     ✅     |                                             [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/loops)                                              |
 |       Functions        |               FUNCTION               |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/functions)                                            |
 |       Functions        | Passing Arguments and Return Values  |     ✅     |                                           [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/operators-conditions-loops-and-functions/functions)                                            |
 |     Randomization      |                                      |     ❌     |                                                     [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/randomization)                                                     |
 |      Holding Keys      |           HOLD and RELEASE           |     ✅     |                                                     [Hak5 Docs](hhttps://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/holding-keys)                                                     |
 |      Holding Keys      |        Holding Modifier Keys         |     ✅     |                                                     [Hak5 Docs](hhttps://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/holding-keys)                                                     |
 |      Holding Keys      |        Holding Multiple Keys         |     ✅     |                                                     [Hak5 Docs](hhttps://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/holding-keys)                                                     |
 |    Payload Control     |                                      |     ❌     |                                                    [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/payload-control)                                                    |
 |         Jitter         |                                      |     ❌     |                                                        [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/jitter)                                                         |
 |     Payload Hiding     |                                      |     ❌     |                                                    [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/payload-hiding)                                                     |
 |    Storage Activity    |                                      |     ❌     |                                                   [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/storage-activity)                                                    |
 |       Lock Keys        |                                      |     ❌     |                                                       [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/lock-keys)                                                       |
 |      Exfiltration      |                                      |     ❌     |                                                     [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/exfiltration)                                                      |
 |       Extensions       |                                      |     ❌     |                                                      [Hak5 Docs](https://docs.hak5.org/hak5-usb-rubber-ducky/advanced-features/extensions)                                                       |


## Mouse/Pointer

### MOUSE or POINTER
All mouse/pointer commands will start with this command. REPEAT command and DELAY commands effect can also be applied to mouse commands.

<b>Note:</b>
1. In parameter field `[button]`, which emulates a mouse button action and can have the following value:
    - LEFT
    - RIGHT
    - MIDDLE
2. In parameter field `[x]` and `[y]`, which emulates mouse movement action and can have the value range from -127 to 127. This represents pixels and is relative to where the mouse currently is. If you are at the point (150,150) then you can move the mouse to (127,127) pixels of this point followed by (23,23). Therefore, if the mouse is at (0,0) [top left corner] and you want to move to (1920, 1080) [bottom right corner, assuming a 1080p display], then you would need to move the mouse a few times 127 pixels at a time (or less) in either direction.
3. In parameter field `[scroll]`, which emulates mouse scroll whell action and can have the following value:
    - UP
    - DOWN
4. In parameters `[num]` represents a number. This is used to repeat the action. Alternatively, REPEAT command can be used.

<b>Syntax:</b>

|     Command      |      Command Type       | Parameter 1 | Parameter 2 | Parameter 3 | Parameter 4 |                                    Description                                    |
|:----------------:|:-----------------------:|:-----------:|:-----------:|:-----------:|:-----------:|:---------------------------------------------------------------------------------:|
| MOUSE or POINTER | CLICK or TOUCH or PRESS |  [button]   |    [num]    |             |             |     Mouse button click. Mouse does not move along `[x]` and `[y]` directions.     |
| MOUSE or POINTER |      HOLD or DRAG       |  [button]   |     [x]     |     [y]     |    [num]    | Mouse button click and hold. Mouse can be moved along `[x]` and `[y]` directions. |
| MOUSE or POINTER |    MOVE or TRANSLATE    |     [x]     |     [y]     |    [num]    |             | Mouse button does not click. Mouse can be moved along `[x]` and `[y]` directions. |
| MOUSE or POINTER | KNOB or WHEEL or SCROLL |  [scroll]   |    [num]    |             |             |          Mouse button does not click. Mouse can be scrolled up or down.           |

<b>Example:</b>
```
REM double left click
MOUSE CLICK LEFT 2
REM drag a folder
MOUSE HOLD LEFT 127 45
REM move pointer
MOUSE MOVE 0 0 5
REM scroll a document
MOUSE SCROLL DOWN 10
REM using repeat
MOUSE CLICK LEFT
REPEAT 1
```

---

# Localization
| Status |   Code    |        Language         | Translated |
|:------:|:---------:|:-----------------------:|:----------:|
|   ❌    |   b+ach   |         Acholi          |     0%     |
|   ❌    |    aa     |          Afar           |     0%     |
|   ✅    |    af     |        Afrikaans        |    100%    |
|   ✅    |    ak     |          Akan           |    100%    |
|   ✅    |    sq     |        Albanian         |    100%    |
|   ✅    |    am     |         Amharic         |    100%    |
|   ✅    |    ar     |         Arabic          |    100%    |
|   ❌    |    an     |        Aragonese        |     0%     |
|   ✅    |    hy     |        Armenian         |    100%    |
|   ❌    |    as     |        Assamese         |     0%     |
|   ❌    |   b+ast   |        Asturian         |     0%     |
|   ❌    |    av     |         Avaric          |     0%     |
|   ❌    |    ae     |         Avestan         |     0%     |
|   ❌    |    ay     |         Aymara          |     0%     |
|   ✅    |    az     |       Azerbaijani       |    100%    |
|   ❌    |   b+ban   |        Balinese         |     0%     |
|   ❌    |   b+bal   |         Balochi         |     0%     |
|   ❌    |    bm     |         Bambara         |     0%     |
|   ❌    |    ba     |         Bashkir         |     0%     |
|   ✅    |    eu     |         Basque          |    100%    |
|   ✅    |    be     |       Belarusian        |    100%    |
|   ✅    |    bn     |         Bengali         |    100%    |
|   🚧   |   b+ber   |         Berber          |    41%     |
|   ❌    |    bh     |         Bihari          |     0%     |
|   ❌    |    bi     |         Bislama         |     0%     |
|   ✅    |    bs     |         Bosnian         |    100%    |
|   ✅    |    br     |         Breton          |    100%    |
|   ✅    |    bg     |        Bulgarian        |    100%    |
|   ❌    |    my     |         Burmese         |     0%     |
|   ✅    |    ca     |         Catalan         |    100%    |
|   ❌    |   b+ceb   |         Cebuano         |     0%     |
|   ❌    |    ch     |        Chamorro         |     0%     |
|   ❌    |    ce     |         Chechen         |     0%     |
|   ❌    |   b+chr   |        Cherokee         |     0%     |
|   ✅    |    ny     |          Chewa          |    100%    |
|   ✅    |  zh-rCN   |   Chinese Simplified    |    100%    |
|   ✅    |  zh-rTW   |   Chinese Traditional   |    100%    |
|   ❌    |    cv     |         Chuvash         |     0%     |
|   ❌    |    kw     |         Cornish         |     0%     |
|   ✅    |    co     |        Corsican         |    100%    |
|   ❌    |    cr     |          Cree           |     0%     |
|   ✅    |    hr     |        Croatian         |    100%    |
|   ✅    |    cs     |          Czech          |    100%    |
|   ✅    |    da     |         Danish          |    100%    |
|   ❌    |    dv     |         Dhivehi         |     0%     |
|   ✅    |    nl     |          Dutch          |    100%    |
|   ❌    |    dz     |        Dzongkha         |     0%     |
|   ✅    |  en-rGB   |      English (UK)       |    100%    |
|   ✅    |  en-rUS   |      English (US)       |    100%    |
|   ✅    |    eo     |        Esperanto        |    100%    |
|   ✅    |    et     |        Estonian         |    100%    |
|   ✅    |    ee     |           Ewe           |    100%    |
|   ❌    |    fo     |         Faroese         |     0%     |
|   ❌    |    fj     |         Fijian          |     0%     |
|   ✅    |   b+fil   |        Filipino         |    100%    |
|   ✅    |    fi     |         Finnish         |    100%    |
|   ✅    |    fr     |         French          |    100%    |
|   ✅    |    fy     |         Frisian         |    100%    |
|   ❌    |   b+fur   |        Friulian         |     0%     |
|   ❌    |    ff     |          Fula           |     0%     |
|   ❌    |   b+gaa   |           Ga            |     0%     |
|   ✅    |    gl     |        Galician         |    100%    |
|   ✅    |    ka     |        Georgian         |    100%    |
|   ✅    |    de     |         German          |    100%    |
|   ❌    |   b+got   |         Gothic          |     0%     |
|   ✅    |    el     |          Greek          |    100%    |
|   ❌    |    kl     |       Greenlandic       |     0%     |
|   ✅    |    gn     |         Guarani         |    100%    |
|   ✅    |    gu     |        Gujarati         |    100%    |
|   ✅    |    ht     |     Haitian Creole      |    100%    |
|   ✅    |    ha     |          Hausa          |    100%    |
|   ✅    |   b+haw   |        Hawaiian         |    100%    |
|   ✅    |    iw     |         Hebrew          |    100%    |
|   ❌    |    hz     |         Herero          |     0%     |
|   ❌    |   b+hil   |       Hiligaynon        |     0%     |
|   ✅    |    hi     |          Hindi          |    100%    |
|   ❌    |    ho     |        Hiri Motu        |     0%     |
|   ❌    |   b+hmn   |          Hmong          |     0%     |
|   ✅    |    hu     |        Hungarian        |    100%    |
|   ✅    |    is     |        Icelandic        |    100%    |
|   ❌    |    io     |           Ido           |     0%     |
|   ✅    |    ig     |          Igbo           |    100%    |
|   ❌    |   b+ilo   |         Ilokano         |     0%     |
|   ✅    |    in     |       Indonesian        |    100%    |
|   ❌    |    iu     |        Inuktitut        |     0%     |
|   ✅    |    ga     |          Irish          |    100%    |
|   ✅    |    it     |         Italian         |    100%    |
|   ✅    |    ja     |        Japanese         |    100%    |
|   ✅    |    jv     |        Javanese         |    100%    |
|   ❌    |   b+kab   |         Kabyle          |     0%     |
|   ✅    |    kn     |         Kannada         |    100%    |
|   ✅    |   b+pam   |       Kapampangan       |    100%    |
|   ❌    |    ks     |        Kashmiri         |     0%     |
|   ❌    |   b+csb   |        Kashubian        |     0%     |
|   ✅    |    kk     |         Kazakh          |    100%    |
|   ❌    |    km     |          Khmer          |     0%     |
|   ✅    |    rw     |       Kinyarwanda       |    100%    |
|   ❌    |   b+tlh   |         Klingon         |     0%     |
|   ❌    |    kv     |          Komi           |     0%     |
|   ❌    |    kg     |          Kongo          |     0%     |
|   ❌    |   b+kok   |         Konkani         |     0%     |
|   ✅    |    ko     |         Korean          |    100%    |
|   ✅    |    ku     |         Kurdish         |    100%    |
|   ❌    |    kj     |        Kwanyama         |     0%     |
|   ✅    |    ky     |         Kyrgyz          |    100%    |
|   ✅    |    lo     |           Lao           |    100%    |
|   ✅    |    la     |          Latin          |    100%    |
|   ✅    |    lv     |         Latvian         |    100%    |
|   ❌    |    li     |       Limburgish        |     0%     |
|   ✅    |    ln     |         Lingala         |    100%    |
|   ✅    |    lt     |       Lithuanian        |    100%    |
|   ❌    |   b+jbo   |         Lojban          |     0%     |
|   ❌    |   b+nds   |       Low German        |     0%     |
|   ❌    |   b+dsb   |      Lower Sorbian      |     0%     |
|   ✅    |    lg     |         Luganda         |    100%    |
|   ❌    |   b+luy   |          Luhya          |     0%     |
|   ❌    |    lb     |      Luxembourgish      |     0%     |
|   ✅    |    mk     |       Macedonian        |    100%    |
|   ❌    |   b+mai   |        Maithili         |     0%     |
|   ✅    |    mg     |        Malagasy         |    100%    |
|   ✅    |    ms     |          Malay          |    100%    |
|   ✅    |    ml     |        Malayalam        |    100%    |
|   ✅    |    mt     |         Maltese         |    100%    |
|   ❌    |    gv     |          Manx           |     0%     |
|   ✅    |    mi     |          Maori          |    100%    |
|   ❌    |   b+arn   |       Mapudungun        |     0%     |
|   ✅    |    mr     |         Marathi         |    100%    |
|   ❌    |    mh     |       Marshallese       |     0%     |
|   ❌    |   b+moh   |         Mohawk          |     0%     |
|   ✅    |    mn     |        Mongolian        |    100%    |
|   ❌    |   b+mos   |          Mossi          |     0%     |
|   ❌    |    na     |          Nauru          |     0%     |
|   ❌    |    ng     |         Ndonga          |     0%     |
|   ✅    |    ne     |         Nepali          |    100%    |
|   ❌    |    se     |      Northern Sami      |     0%     |
|   ✅    |   b+nso   |     Northern Sotho      |    100%    |
|   ✅    |    no     |        Norwegian        |    100%    |
|   ✅    |    nb     |    Norwegian Bokmal     |    100%    |
|   ✅    |    nn     |    Norwegian Nynorsk    |    100%    |
|   ❌    |    oc     |         Occitan         |     0%     |
|   🚧   |    or     |          Odia           |    26%     |
|   ❌    |    oj     |         Ojibwe          |     0%     |
|   ✅    |    om     |          Oromo          |    100%    |
|   ❌    |    os     |        Ossetian         |     0%     |
|   ❌    |    pi     |          Pali           |     0%     |
|   ❌    |   b+pap   |       Papiamento        |     0%     |
|   ✅    |    ps     |         Pashto          |    100%    |
|   ✅    |    fa     |         Persian         |    100%    |
|   ✅    |    pl     |         Polish          |    100%    |
|   ✅    |    pt     |     Portuguese (PT)     |    100%    |
|   ✅    |  pt-rBR   |     Portuguese (BR)     |    100%    |
|   ✅    |    pa     |         Punjabi         |    100%    |
|   ✅    |    qu     |         Quechua         |    100%    |
|   ✅    |    ro     |        Romanian         |    100%    |
|   ❌    |    rm     |         Romansh         |     0%     |
|   ❌    |    rn     |          Rundi          |     0%     |
|   ✅    |    ru     |         Russian         |    100%    |
|   ❌    |    sg     |          Sango          |     0%     |
|   ❌    |    sa     |        Sanskrit         |     0%     |
|   🚧   |   b+sat   |         Santali         |    80%     |
|   ❌    |    sc     |        Sardinian        |     0%     |
|   ❌    |   b+sco   |          Scots          |     0%     |
|   ✅    |    gd     |     Scottish Gaelic     |    100%    |
|   ✅    |    sr     |   Serbian (Cyrillic)    |    100%    |
|   ❌    | b+sr+Latn |     Serbian (Latin)     |     0%     |
|   ✅    |    sn     |          Shona          |    100%    |
|   ❌    |    ii     |       Sichuan Yi        |     0%     |
|   ✅    |    sd     |         Sindhi          |    100%    |
|   ✅    |    si     |         Sinhala         |    100%    |
|   ✅    |    sk     |         Slovak          |    100%    |
|   ✅    |    sl     |        Slovenian        |    100%    |
|   ✅    |    so     |         Somali          |    100%    |
|   ❌    |   b+son   |         Songhay         |     0%     |
|   ❌    |    nr     |    Southern Ndebele     |     0%     |
|   ❌    |   b+sma   |      Southern Sami      |     0%     |
|   ❌    |    st     |     Southern Sotho      |     0%     |
|   ✅    |    es     |      Spanish (ES)       |    100%    |
|   ✅    | b+es+419  | Spanish (Latin America) |    100%    |
|   ✅    |    su     |        Sundanese        |    100%    |
|   ✅    |    sw     |         Swahili         |    100%    |
|   ❌    |    ss     |          Swati          |     0%     |
|   ✅    |    sv     |         Swedish         |    100%    |
|   ❌    |   b+syc   |         Syriac          |     0%     |
|   ❌    |    tl     |         Tagalog         |     0%     |
|   ❌    |    ty     |        Tahitian         |     0%     |
|   ✅    |    tg     |          Tajik          |    100%    |
|   ✅    |    ta     |          Tamil          |    100%    |
|   ✅    |    tt     |          Tatar          |    100%    |
|   ✅    |    te     |         Telugu          |    100%    |
|   ✅    |    th     |          Thai           |    100%    |
|   ❌    |    bo     |         Tibetan         |     0%     |
|   ✅    |    ti     |        Tigrinya         |    100%    |
|   ❌    |    ts     |         Tsonga          |     0%     |
|   ❌    |    tn     |         Tswana          |     0%     |
|   ✅    |    tr     |         Turkish         |    100%    |
|   ✅    |    tk     |         Turkmen         |    100%    |
|   ✅    |    uk     |        Ukrainian        |    100%    |
|   ❌    |   b+hsb   |      Upper Sorbian      |     0%     |

---

# Featuring Rucky
- [Kali Linux 2019.3 Release](https://www.kali.org/blog/kali-linux-2019-3-release/)
- [Kali Linux 2021.1 Release](https://www.kali.org/blog/kali-linux-2021-1-release/)
- [Prog.World](https://prog.world/kali-linux-nethunter-on-android-part-3-breaking-the-distance/)
- [ProgrammerSought](https://www.programmersought.com/article/30497171179/)
- [Dark_Mechanic YouTube Channel](https://youtu.be/ic-X-FCLNk8)
- [AV SHIVA NORO YouTube Channel](https://youtu.be/4clbu41cEQ0)
- [Android Infosec YouTube Channel](https://www.youtube.com/watch?v=_NDXzGPh_BQ)
- [Android Infosec Facebook Post](https://www.facebook.com/AndroidInfoSec/posts/4101537619869708)

---

# Legacy Work
[![RPi](https://img.shields.io/badge/Raspberry%20Pi-0%20W-maroon)](https://github.com/mayankmetha/Rucky-Ext-RPi)
[![HID](https://img.shields.io/badge/Project-Legacy%20HID-lightgreen)](https://github.com/mayankmetha/Rucky-Legacy-HID)
[![Android](https://img.shields.io/badge/android-4.4.x-green)](https://github.com/mayankmetha/Rucky/releases/tag/1.9)
[![Android](https://img.shields.io/badge/android-5.x-green)](https://github.com/mayankmetha/Rucky/releases/tag/1.9)
