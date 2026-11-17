# API levels

Before we start talking about API levels, we obviously need to know the meaning of **API**. **API** stands for **Application Programming Interface**; it is a way for applications to interact with other apps and services.

This topic will provide an overview of API levels and explain how they affect your app, how to configure them in Android Studio, and what possible problems can arise if you use the wrong API level.

## What is API level?

In Android, an **API level** is a number that identifies the version of the Android platform. It helps the developer understand which functions will be available in this specific Android version. Also, with its help, the application can interact with the system.

**API levels** are unique for every Android version, which means that each Android device runs at a single specific API level. Android system checks whether your app is compatible with the device before the application can be installed. How does it do it? Every app has two preferences: **target API level** and **minimum API level**. The target level specifies the version that the app is meant to run on, and the minimum API level corresponds to the lowest compatible level.

If the device is compatible with the minimum API level settings, your app will run. Otherwise, the system will throw an error and deny installing your application.

## Android versions and codenames

Below, you can find a list of the most recent Android versions, codenames, and API levels. Android releases are organized into families in alphabetical order. The codenames used to be inspired by different sweet treats, but starting from Android 10, Google stopped using them publicly (Google still uses them internally — Android 16 was "Baklava" and Android 17 is "Cinnamon Bun" — but you won't see these names in marketing or in Android Studio).

There's no need to memorize [all the versions](https://source.android.com/setup/start/build-numbers). If you want, you can remember them starting from Ice Cream Sandwich (API 14) because older versions are no longer supported:

Codename

Version

API Level

Android 17

17

API level 37

Android 16

16

API level 36

Android 15

15

API level 35

Android 14

14

API level 34

Android 13

13

API level 33

Android 12L

12.1

API level 32

Android 12

12

API level 31

Android 11 (R)

11

API level 30

Android 10 (Q)

10

API level 29

Pie

9

API level 28

Oreo

8.1.0

API level 27

Oreo

8.0.0

API level 26

Nougat

7.1

API level 25

Nougat

7.0

API level 24

Marshmallow

6.0

API level 23

Lollipop

5.1

API level 22

Lollipop

5.0

API level 21

KitKat

4.4 – 4.4.4

API level 19

Jelly Bean

4.3.x

API level 18

Jelly Bean

4.2.x

API level 17

Jelly Bean

4.1.x

API level 16

Ice Cream Sandwich

4.0.3 – 4.0.4

API level 15

Ice Cream Sandwich

4.0.1 – 4.0.2

API level 14

## App compatibility

There's a lot of devices powered by Android out there. Some Android users prefer to upgrade their phones often, while others don't. Usually, applications work just fine after an OS update, but sometimes, things go wrong. Features may become deprecated when a newer version is released, or something dramatic could change in the Android behavior, so it's important for you as a developer to be aware of changes brought by these updates. If you don't have a way to install the newest Android version to test your app, use the Android Emulator.

It's also a good idea to support older devices because people still use old phones. Not all features will be available, so you need to be sure that everything you wrote will run correctly on the minimum API level you have chosen. For example, if some features are not available on older Android versions, you need to either disable them or find a way to make them available. If something is incompatible with the chosen minimum API level, Android Studio will alert you.

## Setting up the API level

Now that you're aware of the basics, let's see how setting the API level works.

As an example, let's take Runtime Permissions that were introduced in Android 6 (API 23). If your app is not ready to support this model of permissions, you can leave API 22 as a target API, and it will be compatible with newer OS versions without using the newer permissions model. However, Google requires apps to meet the new target API level requirements, so you should always try to update this value.

> **Note:**

As of the requirement that took effect on August 31, 2025, new apps and app updates must target Android 15 (API level 35) or higher to be submitted to Google Play. Existing apps must target Android 14 (API level 34) or higher to remain discoverable to new users on devices running a newer Android version; apps still targeting API level 33 or lower are only shown to users on that version or older.

Google raises this bar roughly once a year, usually every August 31, so expect the numbers above to move again. The next scheduled increase takes effect August 31, 2026: new apps and updates will need to target Android 16 (API level 36), and existing apps will need to target at least Android 15 (API level 35) to stay discoverable. Because these thresholds change annually, always check the current target API level requirements page in the Play Console (or the official Android Developers documentation) before submitting your app, rather than relying on a specific number you read somewhere.

When selecting the minimum API level, be aware of the Android version distribution and what API level is required by the features you use. Google no longer updates its Android version distribution webpage, so you will need to check it by creating a new project in Android Studio and selecting `help me choose` under the minimum SDK dropdown.

![API version distribution](https://ucarecdn.com/59c3eb29-d538-4143-ae49-45f755406d52/)

You can set the minimum API level when creating a new project or change the level in an already existing one. To do that, open Android Studio, start your project, head over to *Gradle Scripts* in your Android view, and open the file `build.gradle` (Module: app) (or `build.gradle.kts` in newer, Kotlin DSL projects). In `defaultConfig`, you will find both `minSdk` and `targetSdk`.

![defaultConfig block from build.gradle](https://ucarecdn.com/f912123c-e74a-4f00-9ee7-d0b285815833/)

> **Note:**

To learn more about Gradle scripts you can visit the [build.gradle files topic](https://hyperskill.org/learn/step/15053).

## Conclusion

Let's sum things up:

- API level is a unique identifier of an Android version.

- Each device runs at only one API level.

- Minimum API level refers to the lowest version that can run your app; you should choose it wisely.

- Target API level specifies the version with which your app is 100% compatible.

- The API level settings are set up in the Gradle Scripts in the file `build.gradle` (or `build.gradle.kts`).

Now you have an understanding of how things work with API levels. Time to get some hands-on practice!
