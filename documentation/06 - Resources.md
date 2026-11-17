# Resources

Resources play a very important role in the app: they define layouts, animations, icons, styles, and more. You will probably use them very often, so you have to understand how to work with them. In this topic, we will look at what resources are and how to access them.

## Resources

A **resource** in an Android app is a file (for example, a layout file), or some value like a simple string. Resources can be layout files, strings, sound files, image files, and so on. All those resources are stored in the *res* folder.

If you open an Android project, you will notice several folders for various resources in the *res* directory:

![res directory tree](https://ucarecdn.com/919a3a49-a126-4eb7-8d66-6baa22ad2d10/)

Subdirectories are created in the *res* directory according to the resource types:

**animator**

XML files that define property animation.
Creates an animation by modifying an object's property values over a set period of time.

**color**

XML files that define colors

**drawable**

Raster (**.png**, **9.png**,** .jpg**,** .gif**) or XML (shape, selector, vector) files.

**font**

Font files (**.ttf**, .**otf**, or **.ttc**) or XML files that include a `` element

**mipmap**

Drawable files used for app icons of different screen densities       ** **

**layout**

XML files that define the user interface of our app

**menu**

XML files that define menus

**raw**

Arbitrary files to save in their raw format (sounds, for example)

**values**

XML files that contain strings, styles, dimensions

**xml**

Arbitrary XML files that can be parsed manually

Some subdirectories are present by default, while some others can be added manually.

> **Note:**

Note that you cannot simply create a custom subdirectory with resources like "my_awesome_drawables", "icon1", "best_fonts", and so on. This requires some tweaks to build system.

There are two ways to access resources: in Java or Kotlin files, and in XML files. Let's take a look at both ways in detail.

## Accessing resources in XML

Sometimes it is necessary to access a resource in an XML file. For example, imagine that you need to write the path to your picture in the XML file, or set the color of the text from the file `colors.xml`.

Links to the resources in XML files have the following syntax: `@[package_name:]resource_type/resource_name`, where:

- `package_name` represents the name of the package where the resource is located. If your resource is in the same package, `package_name` is optional.

- `resource_type` matches a resource subdirectory like `drawable` or `layout`, or a value resource type like `color` or `string`.

- `resource_name` is the filename without extension or the value of `android:name` attribute in the XML element.

For example, say we have the following `strings.xml` file:



```xml
<resources>
    <string name="app_name">App Resource</string>
</resources>
```



To make a **TextView** show this string, we make the following:



```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/app_name" />
```



In this case, `text` attribute will receive the `app_name` string resource value.

We can also use built-in Android resources by specifying `android` package: `android:text="@android:string/ok"`.

> **Note:**

All value resources of the same type live in the same namespace. Technically, you can declare ``s in `themes.xml` file, for example, and Android will still be able to find them. Please don't do this as it may confuse other developers.

## Accessing resources in code

The build system generates `R.java` file containing identifiers for all application resources.

Even an empty project contains `res/layout/activity_main.xml`, and `MainActivity` makes it show in `onCreate()` method:



```kotlin
setContentView(R.layout.activity_main)
```



Through `R.layout.activity_main` we get access to `activity_main.xml`. *Layout* is the **type** of the resource and *activity_main *is the **name** of the resource.

As another example, you can get an `ImageView` with id `my_image_view` using `findViewById` method and make it show `res/drawable/my_image.png` resource using `setImageResource(Int)` method:



```kotlin
setContentView(R.layout.activity_main)
...
val imageView = findViewById<ImageView>(R.id.my_image_view)
imageView.setImageResource(R.drawable.my_image)
```



Similarly, we can receive other resources. For example, we use the same `app_name` from `res/values/strings.xml`.



```kotlin
val appTextView = findViewById<TextView>(R.id.app)
appTextView.setText(R.string.app_name)
```



Keep in mind: `R` class contains integer identifiers, not values. This is especially important when dealing with colors: they are integers, too.

You can use `resources` accessible in Activity, View, and some other classes to fetch individual values:



```kotlin
appTextView.setColor(resources.getColor(R.color.dark))
```



## Conclusion

Now you know what kinds of resources do exist and how to declare and use them in your project. To access resources from other XML resources, we use `@[package:]type/name` syntax, and in Kotlin code we use identifiers from `R` class. Now you know how to make certain views use some resources, and how to access individual resource values.
