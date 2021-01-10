# SwipeDismissImage

![build](https://github.com/KaustubhPatange/SwipeDismissImage/workflows/build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.kaustubhpatange/swipe-dismiss-image)

A custom activity for Android to display image with features like swipe to dismiss, zooming, pinning, etc.

### Q. Why I made this?

TL;DR... It all started when I got curious how gestures (specifically dragging) are implementated in Android. Looking at the source code of certain libraries which responds to such touch events, I was keen that I had to make something to learn/practice (curious ;)).

So I started making an activity which will show image which various features despite not unique, (considering various alternatives) but I had to learn it. So anyways, after it become solid (IMO) I made it as a library. There is still improvements which I'll keep working as I improve my knowledge and will find some time.

## Usage

You can check out the [sample](/sample) which show this usage in action, along with some more advance usage as well.

A short summary on how-to use,

- Create a class that extends `SwipeDismissImageActivity`.

```kotlin
class ImageActivity : SwipeDismissImageActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      /** Use any one of these methods. */
      setImageBitmap(...)
      setImageDrawable(...)
   }
}
```

- Delcare a translucent `NoActionBar` theme for that activity.

```xml
<style name="Theme.Translucent" parent="Theme.MaterialComponents.NoActionBar">
   <item name="android:windowIsTranslucent">true</item>
   <item name="android:statusBarColor" tools:targetApi="l">@android:color/transparent</item>
   <item name="android:colorBackgroundCacheHint">@null</item>
</style>
```

- Do not forget to declare the activity in your manifest file along with the theme attribute.

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
```

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
