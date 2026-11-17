# AndroidManifest file

The manifest file describes the essential information about your app that is required by Android operating system. After receiving this information, the system can execute any application code. Every Android project must have an *AndroidManifest.xml *file. In this topic, you will learn how to work with it.

## <application> tag

Let's take a look at the XML code inside the file. Expand the* manifests* folder and double-click *AndroidManifest.xml* to open it. Inside the tag ``, you will see the element ``. This is the main element that manages the structure of your app.

In the tag ``, there are attributes `icon` and `label`. These attributes specify a small icon and a text label, which is what the user will see in the list of applications.



```xml
<manifest ...>
   <application
       android:allowBackup="true"
       android:icon="@mipmap/ic_launcher"
       android:label="Android Manifest"
       android:roundIcon="@mipmap/ic_launcher_round"
       android:supportsRtl="true"
       android:theme="@style/AppTheme">
       ...
</manifest>
```



Let's take a look at other attributes and what they do:

`allowBackup` allows the application to participate in backup and restore infrastructure. The default value of this attribute is *true*.

`roundIcon` defines a circle-shaped version of your application logo.

`supportsRtl`** **declares whether your application will support right-to-left (RTL) layouts. The default value of this attribute is *true*.

`theme` sets a default theme for all activities in the application. You can override it for some activities.

## <intent-filter> tag

The next block in the file has a tag `` which tells Android which Activity should be started when the user taps on the application icon. If you have several activities, one of them should contain the following lines:



```xml
<activity android:name=".MainActivity">
   <intent-filter>
      <action android:name="android.intent.action.MAIN" />

      <category android:name="android.intent.category.LAUNCHER" />
   </intent-filter>
</activity>
```



The elements `` and `` indicate that the activity icon should be shown in the launcher.

> **Note:** Note that MainActivity is referenced in the element ``. Any `Activity` in your app must be declared in the manifest.

## App components

You will have to edit the file, change its structure, and add new elements and attributes during application development. The file *AndroidManifest.xml* is also the place where you declare the following:

- The **package name** for the application uniquely identifies it.

- The components of the app, which include **activities**, **services**, **broadcast receivers**, and **content providers**. Based on these declarations, the Android system can determine what components are included in the application and under what conditions they are launched. **Service** is a component of an app that performs a longer operation without user interaction, for example, playing an MP3 player. Don't worry about other components now: we will consider them in detail later.

- The **permissions** that your app needs. **Permissions** include the ability for your app to access [sensitive user data](https://developer.android.com/guide/topics/permissions/overview#prompts_access_sensitive_user_info) such as contacts and SMS, or certain [system features](https://developer.android.com/guide/topics/permissions/overview#permission_enforcement) such as hardware and Internet access. You can use **system permissions** defined by Android or define your [custom permissions](https://developer.android.com/guide/topics/permissions/defining).

If you need to get access to the Internet, you will need to write the following line:



```xml
<manifest ... >
    <uses-permission android:name="android.permission.INTERNET"/>
    <application ...
</manifest>
```



- Finally,** the hardware and software feature** the app requires. Depending on this, Play Store determines which apps can be installed on a specific device. The element `` declares specific functionality required for the application to function. Note: your application will not be installed on devices that do not have the necessary functionality.

For example, the application might require an autofocus camera. If the device does not have a built-in autofocus camera, the application will not be installed. You can declare the autofocus camera as *required* with the following manifest tag:



```xml
<manifest ... >
    <uses-feature android:name="android.hardware.camera.autofocus"
                  android:required="true"/>
    <application ...
</manifest>
```



All `` tags must be *before* `` tag.

## Conclusion

*AndroidManifest.xml* fully describes the structure that makes up the application and its requirements. Keep in mind: to create any project for Android, the file must be composed entirely and correctly. Good luck!
