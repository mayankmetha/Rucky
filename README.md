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

# Download
## GitHub
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/mayankmetha/Rucky)](https://github.com/mayankmetha/Rucky/releases/latest)
[![Github nightly](https://img.shields.io/badge/nightly-v2.3%20(541)-blueviolet)](https://raw.githubusercontent.com/mayankmetha/Rucky/master/nightly/rucky-nightly.apk)

## Nethunter App Store
[![Nethuter Release](https://img.shields.io/badge/release-v2.2-blue)](https://store.nethunter.com/en/packages/com.mayank.rucky/)

---

# Readme
An android app to perform USB HID Attacks (Rubber Duck) in multiple ways:
- <b><i>Wired Mode:</i></b> Needs a custom kernel with usb hid feature or a kernel with configfs to be enabled for this mode to be used.
- <b><i>Wireless:</i></b> Provides a socket server to extend this USB HID with external tools/hardwares like raspberry pi, socket services, nc, and much more.

---

# Ducky Script

Ducky Script syntax is simple. Each command resides on a new line and may have options follow. Commands are written in ALL CAPS, because ducks are loud and like to quack with pride. Most commands invoke keystrokes, key-combos or strings of text, while some offer delays or pauses. Below is a list of commands and their function, followed by some example usage. Some syntax extended from the original [Hak5 Ducky Script Syntax](https://github.com/hak5darren/USB-Rubber-Ducky/wiki/Duckyscript). Mouse ducky scripts are different from those seen online and have been defined to keep the similarity with the keyboard ducky scripts.

<b>Note:</b> In parameters `[num]` represents a number, `[char]` represents characters A-Z, a-z. [Customizable HID](https://mayankmetha.github.io/Rucky-KeyMap/) support too has been added.

## Keyboard

### DEFAULTDELAY or DEFAULT_DELAY

DEFAULT_DELAY or DEFAULTDELAY is used to define how long (milliseconds) to wait between each subsequent command. DEFAULT_DELAY must be issued at the beginning of the ducky script and is optional. Not specifying the DEFAULT_DELAY will result in faster execution of ducky scripts. This command is mostly useful when debugging.

<b>Syntax:</b>

Command | Parameter
:---: | :---:
DEFAULTDELAY or DEFAULT_DELAY | [num]

<b>Example:</b>
```
DEFAULTDELAY 100
DEFAULT_DELAY 100
```

### DELAY

DELAY creates a momentary pause in the ducky script. It is quite handy for creating a moment of pause between sequential commands that may take the target computer some time to process. DELAY time is specified in milliseconds. Multiple DELAY commands can be used to create longer delays.

<b>Syntax:</b>

Command | Parameter
:---: | :---:
DELAY | [num]

<b>Example:</b>
```
DELAY 500
```

### REM

Similar to the REM command in Basic and other languages, lines beginning with REM will not be processed. REM is a comment.

<b>Syntax:</b>

Command | Parameter
:---: | :---:
REM | comment string

<b>Example:</b>
```
REM This is a comment
```

### REPEAT

Repeats the last command `[num]` times

<b>Syntax:</b>

Command | Parameter
:---: | :---:
REPEAT | [num]

<b>Example:</b>
```
DELAY 1
REPEAT 5
```
### STRING_DELAY or STRINGDELAY

STRING processes the text following taking special care to auto-shift with additional delay between each character. STRING can accept a single or multiple characters.

<b>Syntax:</b>

Command | Delay Parameter | Parameter
:---: | :---: | :---:
STRING_DELAY | [num] | all possible printable characters supported on a specific keyboard layout
STRINGDELAY  | [num] | all possible printable characters supported on a specific keyboard layout

<b>Example:</b>
```
STRING_DELAY 1000 hello world
STRINGDELAY 1000 hello world
```

### STRING

STRING processes the text following taking special care to auto-shift. STRING can accept a single or multiple characters.

<b>Syntax:</b>

Command | Parameter
:---: | :---:
STRING | all possible printable characters supported on a specific keyboard layout

<b>Example:</b>
```
STRING hello world
```

### GUI or WINDOWS or COMMAND or META

Emulates the Windows-Key, sometimes referred to as the Super-key.

<b>Syntax:</b>

Command | Parameter
:---: | :---:
GUI or WINDOWS or COMMAND or META | Combo Key

<b>Example:</b>
```
GUI r
```

### SHIFT

Unlike CAPSLOCK, cruise control for cool, the SHIFT command can be used when navigating fields to select text, among other functions.

<b>Syntax:</b>

Command | Optional Parameter | Parameter
:---: | :---: | :---:
SHIFT | ALT or GUI or WINDOWS or COMMAND or META or CTRL or CONTROL | Combo Key

<b>Example:</b>
```
SHIFT a
SHIFT ALT 4
```

### ALT

Found to the left of the space key on most keyboards, the ALT key is instrumental in many automation operations.

<b>Syntax:</b>

Command | Optional Parameter | Parameter
:---: | :---: | :---:
ALT | SHIFT or CTRL or CONTROL | Combo Key

<b>Example:</b>
```
ALT a
ALT SHIFT e
```

### CONTROL or CTRL

The king of key-combos, CONTROL is all mighty.

<b>Syntax:</b>

Command | Optional Parameter | Parameter
:---: | :---: | :---:
CTRL or CONTROL | SHIFT or ALT | Combo Key

<b>Example:</b>
```
CTRL s
CTRL ALT DELETE
```

### Extended Keys

These keys have no parameters.

<b>Syntax:</b>

Command | Description
:---: | :---
MENU or APP | Emulates the App key, sometimes referred to as the menu key or context menu key. On Windows systems this is similar to the SHIFT F10 key combo, producing the menu similar to a right-click. Has no parameters
DOWNARROW or DOWN | Emulates down arrow key
UPARROW or UP | Emulates down arrow key
LEFTARROW or LEFT | Emulates left arrow key
RIGHTARROW or RIGHT | Emulates right arrow key
DELETE | Emulates delete key
END | Emulates end key
HOME | Emulates home key
INSERT | Emulates insert key
PAGEUP | Emulates page up key
PAGEDOWN | Emulated page down key
PRINTSCREEN or PRINTSCRN or PRNTSCRN or PRTSCN or PRSC or PRTSCR | Typically takes screenshots
BREAK or PAUSE | For the infamous combo CTRL BREAK
NUMLOCK | Toggle numlock
CAPSLOCK | Toggle capslock
SCROLLLOCK | Toggle scroll lock
ESC or ESCAPE | Emulates esc key
SPACE | Emulates spacebar
TAB | Emulates tab key
BACKSPACE or BKSP | Emulates backspace key. On MacOS this is the delete key.
ENTER | Emulates enter key
F1 | Emulates F1 key
F2 | Emulates F2 key
F3 | Emulates F3 key
F4 | Emulates F4 key
F5 | Emulates F5 key
F6 | Emulates F6 key
F7 | Emulates F7 key
F8 | Emulates F8 key
F9 | Emulates F9 key
F10 | Emulates F10 key
F11 | Emulates F11 key
F12 | Emulates F12 key
F13 | Emulates F13 key
F14 | Emulates F14 key
F15 | Emulates F15 key
F16 | Emulates F16 key
F17 | Emulates F17 key
F18 | Emulates F18 key
F19 | Emulates F19 key
F20 | Emulates F20 key
F21 | Emulates F21 key
F22 | Emulates F22 key
F23 | Emulates F23 key
F24 | Emulates F24 key

### Combo Key

Some commands has a parameter which is a combo key. These keys belong to US English keyboard. List of supported combo key are:

- ESCAPE or ESC
- ENTER
- SPACE
- BACKSPACE or BKSP
- TAB
- INSERT
- DELETE
- PAGEUP
- PAGEDOWN
- HOME
- END
- DOWNARROW or DOWN
- UPARROW or UP
- LEFTARROW or LEFT
- RIGHTARROW or RIGHT
- BREAK or PAUSE
- F1 to F24
- single number `[num]`
- Single character `[char]`
- ` or ~
- [ or {
- ] or }
- \ or |
- ; or :
- , or <
- . or >
- / or ?
- \- or _
- = or +

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

Command | Command Type | Parameter 1 | Parameter 2 | Parameter 3 | Parameter 4 | Description
:---: | :---: | :---: | :---: | :---: | :---: | :---:
MOUSE or POINTER | CLICK or TOUCH or PRESS | [button] | [num] | | | Mouse button click. Mouse does not move along `[x]` and `[y]` directions.
MOUSE or POINTER | HOLD or DRAG | [button] | [x] | [y] | [num] | Mouse button click and hold. Mouse can be moved along `[x]` and `[y]` directions.
MOUSE or POINTER | MOVE or TRANSLATE | [x] | [y] | [num] | | Mouse button does not click. Mouse can be moved along `[x]` and `[y]` directions.
MOUSE or POINTER | KNOB or WHEEL or SCROLL | [scroll] | [num] | | | Mouse button does not click. Mouse can be scrolled up or down.

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
Status      |Code    |Language           |Translated
:---:       |:---:   |:---:              |:---:
❌|b+ach     |Acholi              |0%
❌|aa        |Afar                |0%
❌|af        |Afrikaans           |0%
❌|ak        |Akan                |0%
❌|sq        |Albanian            |0%
❌|am        |Amharic             |0%
✅|ar        |Arabic              |100%
❌|an        |Aragonese           |0%
❌|hy        |Armenian            |0%
❌|as        |Assamese            |0%
❌|b+ast     |Asturian            |0%
❌|av        |Avaric              |0%
❌|ae        |Avestan             |0%
❌|ay        |Aymara              |0%
✅|az        |Azerbaijani         |100%
❌|b+ban     |Balinese            |0%
❌|b+bal     |Balochi             |0%
❌|bm        |Bambara             |0%
❌|ba        |Bashkir             |0%
🚧|eu        |Basque              |7%
❌|be        |Belarusian          |0%
❌|bn        |Bengali             |0%
🚧|b+ber     |Berber              |41%
❌|bh        |Bihari              |0%
❌|bi        |Bislama             |0%
❌|bs        |Bosnian             |0%
🚧|br        |Breton              |14%
✅|bg        |Bulgarian           |100%
❌|my        |Burmese             |0%
❌|ca        |Catalan             |0%
❌|b+ceb     |Cebuano             |0%
❌|ch        |Chamorro            |0%
❌|ce        |Chechen             |0%
❌|b+chr     |Cherokee            |0%
❌|ny        |Chewa               |0%
✅|zh-rCN    |Chinese Simplified  |100%
✅|zh-rTW    |Chinese Traditional |100%
❌|cv        |Chuvash             |0%
❌|kw        |Cornish             |0%
❌|co        |Corsican            |0%
❌|cr        |Cree                |0%
✅|hr        |Croatian            |100%
✅|cs        |Czech               |100%
✅|da        |Danish              |100%
❌|dv        |Dhivehi             |0%
✅|nl        |Dutch               |100%
❌|dz        |Dzongkha            |0%
✅|en-rGB    |English (UK)        |100%
✅|en-rUS    |English (US)        |100%
❌|eo        |Esperanto           |0%
✅|et        |Estonian            |100%
❌|ee        |Ewe                 |0%
❌|fo        |Faroese             |0%
❌|fj        |Fijian              |0%
❌|b+fil     |Filipino            |0%
✅|fi        |Finnish             |100%
✅|fr        |French              |100%
❌|fy        |Frisian             |0%
❌|b+fur     |Friulian            |0%
❌|ff        |Fula                |0%
❌|b+gaa     |Ga                  |0%
❌|gl        |Galician            |0%
❌|ka        |Georgian            |0%
✅|de        |German              |100%
❌|b+got     |Gothic              |0%
✅|el        |Greek               |100%
❌|kl        |Greenlandic         |0%
❌|gn        |Guarani             |0%
❌|gu        |Gujarati            |0%
❌|ht        |Haitian Creole      |0%
❌|ha        |Hausa               |0%
❌|b+haw     |Hawaiian            |0%
✅|iw        |Hebrew              |100%
❌|hz        |Herero              |0%
❌|b+hil     |Hiligaynon          |0%
✅|hi        |Hindi               |100%
❌|ho        |Hiri Motu           |0%
❌|b+hmn     |Hmong               |0%
🚧|hu        |Hungarian           |25%
🚧|is        |Icelandic           |1%
❌|io        |Ido                 |0%
❌|ig        |Igbo                |0%
❌|b+ilo     |Ilokano             |0%
✅|in        |Indonesian          |100%
❌|iu        |Inuktitut           |0%
❌|ga        |Irish               |0%
✅|it        |Italian             |100%
✅|ja        |Japanese            |100%
❌|jv        |Javanese            |0%
❌|b+kab     |Kabyle              |0%
❌|kn        |Kannada             |0%
❌|b+pam     |Kapampangan         |0%
❌|ks        |Kashmiri            |0%
❌|b+csb     |Kashubian           |0%
❌|kk        |Kazakh              |0%
❌|km        |Khmer               |0%
❌|rw        |Kinyarwanda         |0%
❌|b+tlh     |Klingon             |0%
❌|kv        |Komi                |0%
❌|kg        |Kongo               |0%
❌|b+kok     |Konkani             |0%
✅|ko        |Korean              |100%
🚧|ku        |Kurdish             |1%
❌|kj        |Kwanyama            |0%
❌|ky        |Kyrgyz              |0%
❌|lo        |Lao                 |0%
❌|la        |Latin               |0%
✅|lv        |Latvian             |100%
❌|li        |Limburgish          |0%
❌|ln        |Lingala             |0%
✅|lt        |Lithuanian          |100%
❌|b+jbo     |Lojban              |0%
❌|b+nds     |Low German          |0%
❌|b+dsb     |Lower Sorbian       |0%
❌|lg        |Luganda             |0%
❌|b+luy     |Luhya               |0%
❌|lb        |Luxembourgish       |0%
❌|mk        |Macedonian          |0%
❌|b+mai     |Maithili            |0%
❌|mg        |Malagasy            |0%
✅|ms        |Malay               |100%
❌|ml        |Malayalam           |0%
❌|mt        |Maltese             |0%
❌|gv        |Manx                |0%
❌|mi        |Maori               |0%
❌|b+arn     |Mapudungun          |0%
❌|mr        |Marathi             |0%
❌|mh        |Marshallese         |0%
❌|b+moh     |Mohawk              |0%
❌|mn        |Mongolian           |0%
❌|b+mos     |Mossi               |0%
❌|na        |Nauru               |0%
❌|ng        |Ndonga              |0%
❌|ne        |Nepali              |0%
❌|se        |Northern Sami       |0%
❌|b+nso     |Northern Sotho      |0%
✅|no        |Norwegian           |100%
✅|nb        |Norwegian Bokmal    |100%
✅|nn        |Norwegian Nynorsk   |100%
❌|oc        |Occitan             |0%
🚧|or        |Odia                |7%
❌|oj        |Ojibwe              |0%
❌|om        |Oromo               |0%
❌|os        |Ossetian            |0%
❌|pi        |Pali                |0%
❌|b+pap     |Papiamento          |0%
❌|ps        |Pashto              |0%
🚧|fa        |Persian             |9%
✅|pl        |Polish              |100%
✅|pt        |Portuguese          |100%
❌|pa        |Punjabi             |0%
❌|qu        |Quechua             |0%
✅|ro        |Romanian            |100%
❌|rm        |Romansh             |0%
❌|rn        |Rundi               |0%
✅|ru        |Russian             |100%
❌|sg        |Sango               |0%
❌|sa        |Sanskrit            |0%
🚧|b+sat     |Santali             |57%
❌|sc        |Sardinian           |0%
❌|b+sco     |Scots               |0%
❌|gd        |Scottish Gaelic     |0%
❌|sr        |Serbian (Cyrillic)  |0%
❌|b+sr+Latn |Serbian (Latin)     |0%
❌|sn        |Shona               |0%
❌|ii        |Sichuan Yi          |0%
❌|sd        |Sindhi              |0%
❌|si        |Sinhala             |0%
✅|sk        |Slovak              |100%
✅|sl        |Slovenian           |100%
❌|so        |Somali              |0%
❌|b+son     |Songhay             |0%
❌|nr        |Southern Ndebele    |0%
❌|b+sma     |Southern Sami       |0%
❌|st        |Southern Sotho      |0%
✅|es        |Spanish             |100%
❌|su        |Sundanese           |0%
❌|sw        |Swahili             |0%
❌|ss        |Swati               |0%
✅|sv        |Swedish             |100%
❌|b+syc     |Syriac              |0%
❌|tl        |Tagalog             |0%
❌|ty        |Tahitian            |0%
❌|tg        |Tajik               |0%
✅|ta        |Tamil               |100%
❌|tt        |Tatar               |0%
✅|te        |Telugu              |100%
✅|th        |Thai                |100%
❌|bo        |Tibetan             |0%
❌|ti        |Tigrinya            |0%
❌|ts        |Tsonga              |0%
❌|tn        |Tswana              |0%
✅|tr        |Turkish             |100%
❌|tk        |Turkmen             |0%
✅|uk        |Ukrainian           |100%
❌|b+hsb     |Upper Sorbian       |0%
✅|ur-rIN    |Urdu (IN)           |100%
✅|ur-rPK    |Urdu (PK)           |100%
❌|ug        |Uyghur              |0%
❌|uz        |Uzbek               |0%
❌|ve        |Venda               |0%
✅|vi        |Vietnamese          |100%
❌|wa        |Walloon             |0%
❌|cy        |Welsh               |0%
❌|wo        |Wolof               |0%
❌|xh        |Xhosa               |0%
❌|b+sah     |Yakut/Sakha         |0%
❌|ji        |Yiddish             |0%
❌|yo        |Yoruba              |0%
❌|zu        |Zulu                |0%

---

# Features
Status| Feature
:---:| :---
✅| USB HID (Root & custom kernel required)
✅| USB HID over network socket
✅| USB connection detection
✅| Run attack UI button
✅| Auto launch attack on USB connect
✅| Auto launch attack on network socket connect
✅| Save scripts
✅| Load scripts
✅| Delete scripts
✅| Day/Night theme
✅| Launch app via dialer app
✅| Hide launcher app (till Android 10)
✅| Disable app launch via launcher (Android 11 onwards)
✅| Biometric support
✅| App lock support
✅| Encrypt scripts on save
✅| Decrypt scripts on load
✅| Customize network socket address
✅| In-app update service (GitHub release only)
✅| Kali Linux NetHunter Support
✅| Nightly Builds
✅| ConfigFS auto configuration
✅| Multiple HID languages support
✅| Customizable HID for additional language support
✅| Platform independent HID support
✅| Can attack virtually any system with USB HID support
✅| USB HID mouse support
✅| Root detection
✅| Magisk Module
✅| Runtime process name masking
✅| Emulator detection
✅| Monet Theme
🚧| Bluetooth HID
💡| More feature addition on request

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
