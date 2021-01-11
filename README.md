# SwipeDismissImage

![build](https://github.com/KaustubhPatange/SwipeDismissImage/workflows/build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.kaustubhpatange/swipe-dismiss-image)

A custom view for Android to display image with features like swipe to dismiss, zooming, pinning, etc.

<img height="500px" width="250px" src="art/demo.gif" />

### Q. Why I made this?

TL;DR... It all started when I got curious how gestures (specifically dragging) are implementated. Looking at the source code of some libraries which responds to such touch events, I was keen that I had to make something to learn/practice ;)

So I started making an activity which shows an image with various features despite not unique, (considering various alternatives) but I had to learn it. So anyways, after it become solid (IMO) I made it as a library. There are still some improvements needed which I'll keep on working as I improve my knowledge.

## Usage

You can check out the [sample](/sample) which show this usage in action, along with some more advance usage as well.

A short summary on how-to use,

- Create an activity with the following layout as the `contentView`.

```xml
<com.kpstv.dismiss.image.SwipeDismissImageLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/sdl_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    ...
</com.kpstv.dismiss.image.SwipeDismissImageLayout>
```

```kotlin
class ImageActivity : AppCompatActivity() {
   override fun onCreate(...) {
      ...
      sdl_layout.setSwipeDismissListener { finish() }
   }
}
```

- Delcare a translucent `NoActionBar` theme for that activity. Make sure to set this theme for the activity.

```xml
<style name="Theme.Translucent" parent="...">
   <item name="android:windowBackground">@android:color/transparent</item>
   <item name="android:windowIsTranslucent">true</item>
   <item name="android:colorBackgroundCacheHint">@null</item>
   <item name="android:statusBarColor" tools:targetApi="l">@android:color/transparent</item>
</style>
```

- Finally start the activity and get reference to `SwipeDismissImageLayout` from which you can get `ImageView` using `getRootImageView()` function.

| Attributes                |                                                                                                               |
| ------------------------- | ------------------------------------------------------------------------------------------------------------- |
| `app:swipeDismissEnabled` | Sets if swipe to dismiss action is enabled or not, default `true`.                                            |
| `app:affectOpacity`       | Sets if by translation it should produce a fade/alpha effect, default `true`.                                 |
| `app:swipeOffsetDistance` | Sets the offset distance which when crossed will invoke `SwipeDistanceListener`, default 1/3 \* screenHeight. |
| `android:drawable`        | Sets a drawable on the `ImageView`.                                                                           |
| `app:rootBackground`      | Sets the default background color of the layout, default `android.R.color.black`.                             |
| `app:imageTransitionName` | Can be used for shared element transition (see [sample](/sample)).                                            |

<!-- - Do not forget to declare the activity in your manifest file along with the theme attribute.

```xml
<application>
   ...
   <activity
       android:name=".ImageActivity"
       android:theme="@style/Theme.Translucent"/>
</application>
```

- Finally, start the `ImageActivity` we just delcared.

```kotlin
startActivity(Intent(this, ImageActivity::class.java))
``` -->

## Download

Library is available at `MavenCentral()`.

```gradle
implementation 'io.github.kaustubhpatange:swipe-dismiss-image:<version>'
```

## License

- [The Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

```
Copyright 2020 Kaustubh Patange

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
