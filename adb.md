# Bypass Clipboard restrictions on Android Q

## Problem
Android 10 (API level 29) introduces a number of features and behavior changes to better protect users' privacy. Some of this changes is [Limited access to clipboard data](https://developer.android.com/about/versions/10/privacy/changes#clipboard-data). In practice, this means that your app cannot access clipboard data in the background anymore.

## Solution
Fortunately we still have a way to bypass this restriction:
* [Install ADB on your computer](https://www.xda-developers.com/install-adb-windows-macos-linux/)
* Connect your device to your PC/Mac with your USB cable
* Execute the following commands
* Done!

```
adb -d shell appops set im.dacer.kata SYSTEM_ALERT_WINDOW allow;
adb -d shell pm grant im.dacer.kata android.permission.READ_LOGS;
adb -d shell am force-stop im.dacer.kata;
```

## About Android 12
ADB method may not work in Android 12, so if you are using an Android 12 device and have run the above command, please use the following command to revoke the permission and try the accessibility service method.

```
adb -d shell pm revoke im.dacer.kata android.permission.READ_LOGS;
adb -d shell am force-stop im.dacer.kata;
```

## References:
* [Bypass Clipboard restrictions on Android Q](https://clipto.pro/#/note?id=VTJGc2RHVmtYMSsrMnlnWWNCclpiVTVlS1hIaWVnMzArbXFvQ3k2WWpOYWFwM3lOUE8vQzJ1U0FMdlpFb1ZEL3o5dVlldmUvcDJJd2ZCeTVhM3lab2JzN1ZQTjZSdDY0Y0dmM1g1YzdqZDZSZXUwbmhkY0RDWHFkNHZhSjYwaEY%3D)
