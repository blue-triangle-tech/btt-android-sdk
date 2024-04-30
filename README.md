# Blue Triangle Tech Android Analytics SDK

[![jitpack.io build status](https://jitpack.io/v/blue-triangle-tech/btt-android-sdk.svg)](https://jitpack.io/#blue-triangle-tech/btt-android-sdk)

## Getting Started

### Installation

Add the Maven repository to the project's `build.gradle` file:

```groovy
allprojects {
	repositories {
		//...
		maven { url 'https://jitpack.io' }
	}
}
```

**Or** `settings.gradle` file:

```groovy
dependencyResolutionManagement {
    //...
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the package dependency to your application's `build.gradle` file:

```groovy
dependencies {
    //...
    implementation 'com.github.blue-triangle-tech:btt-android-sdk:2.9.0'
}
```

**Note: For Gradle Plugin Version 8.2.0+**

If project uses gradle plugin version 8.2.0 and above, use exclude with dependency like below.

```groovy
implementation("com.github.blue-triangle-tech:btt-android-sdk:2.9.0") {
    exclude("com.squareup.okhttp3", "okhttp-bom")
}
```

### Configuration

In order to start using the SDK, you need to first configure the SDK. To do that, perform the following steps:

1. Add BlueTriangle site ID metadata in the manifest as follows

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <meta-data android:name="com.blue-triangle.site-id" android:value="<BTT_SITE_ID>"/>
        
        <!-- Your other manifest stuff goes here -->
    </application>
</manifest>
```

2. Initialize the Tracker in your Application class's onCreate as follows

```kotlin
import android.app.Application
import com.bluetriangle.analytics.Tracker // Import BlueTriangle Tracker
//...

class YourApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        
        Tracker.init(this) // Initialize the BlueTriangle Tracker
        
        //...
        
    }
    
}
```

Alternatively, if you don't want to add meta-data in your Manifest file, you can provide the site ID programmatically as follows

```kotlin
import android.app.Application
import com.bluetriangle.analytics.Tracker // Import BlueTriangle Tracker
//...

class YourApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        Tracker.init(this, "<BTT_SITE_ID>") // Initialize the BTT SDK with the given site ID

        //...
        
    }
    
}
```

Replace `<BTT_SITE_ID>` with your **site ID**. You can find instructions on how to find your **site ID** [**here**](https://help.bluetriangle.com/hc/en-us/articles/28809592302483-How-to-find-your-Site-ID-for-the-BTT-SDK).

## Using Timers

Timers are simple data objects that contain the associated times, fields related to the timer
instance, and methods to start, mark interactive, and end a timer. Fields associated with the timer
such as page name, traffic segment, brand value, etc can be set on the timer via convenience
constructors and methods. Associated fields can be set anytime during the lifetime of the timer
until submitted to the Tracker.

```java
// create and start a timer
final Timer timer=new Timer("Page Name","Traffic Segment Name").start();

// do work...

// optionally, mark the timer as interactive
timer.interactive();

// maybe set a field
timer.setCartValue(99.99);

// do some more work

// end the timer and submit
timer.end().submit();

// or end the timer, set fields such as brand value, and finally submit the timer.
timer.end();
timer.setBrandValue(99.99);
timer.submit();
```

Timers implement `Parcelable` to allow timers to be passed via `Bundle` such as between activities
in an `Intent`.

```java
// MainActivity.java
final Timer timer=new Timer("Next Page","Android Traffic").start();
final Intent intent=new Intent(this,NextActivity.class);
intent.putExtra(Timer.EXTRA_TIMER,timer);
startActivity(intent);

// NextActivity.java
final Timer timer=getIntent().getParcelableExtra(Timer.EXTRA_TIMER);
timer.end().submit();
```

When a timer is submitted to the tracker, the tracker sets any global fields such as site ID,
session ID, and user ID. Additional global fields may be set as needed and applied to all timers.
The timer's fields are then converted to JSON and sent via HTTP POST to the configured tracker URL.

## Screen View Tracking

Screen tracking automatically captures all activities and fragments in your app. You will see fragment and activity class names on our
dashboard with view count.

**Tracking Composables**  
If your app uses Jetpack Compose UI, use our side-effect `BttTimerEffect(<screen name>)` like below. Unlike Activities and Fragments,
Composable screens are not automatically tracked. You need to call `BttTimerEffect` side-effect for each screen you want
to track. The only parameter to this side-effect is screen name.  
```kotlin
@Composable
fun UserProfileScreen() {
    BttTimerEffect("User Profile")
    // ...
}
```

If your app is using both Composables and Fragments. Then for those composables which are added to fragment no need to
use `BttTimerEffect`, because its fragment is automatically tracked.

<br/>

You can disable Screen tracking by adding the following meta-data:
```xml
<meta-data android:name="com.blue-triangle.screen-tracking.enable" android:value="false"/>
```

<br/>

> **Note:**<br/>
> Screen tracking also allows co-relating errors and network requests with the screens. The crash or network
> request associates screen name on which this crash or network request occurred. Thus, if screen tracking is disabled,
> the page name of most recently started manual Timer's page name will be sent along with the crash or network request.

## ANR Detection

The ANR Detector identifies blocks in the main thread over a specified period of time and reports them as Application
Not Responding (ANR) incidents. By default, the ANR is reported if your app's main thread is blocked for 5 seconds or more. You can modify this duration (in seconds) by setting
the `com.blue-triangle.track-anr.interval-sec` metadata as follows:

```xml
<meta-data android:name="com.blue-triangle.track-anr.interval-sec" android:value="5"/>
```

You can disable ANR detection by setting the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.track-anr.enable" android:value="false"/>
```

## Track Crashes

The SDK automatically tracks and reports all crashes. To disable this feature, add the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.track-crashes.enable" android:value="false"/>
```

## Network Capture

Network capture refers to capturing of Http calls that are made through your app.

### OkHttp Support

The SDK provides plug and play support for OkHttp which allows you to easily track all your network requests going through an OkHttpClient. Just add the `BlueTriangleOkHttpInterceptor` interceptor and `BlueTriangleOkHttpEventListener` event listener to
your `OkHttpClient` as follows:

```kotlin
import com.bluetriangle.analytics.okhttp.BlueTriangleOkHttpInterceptor
import com.bluetriangle.analytics.okhttp.BlueTriangleOkHttpEventListener
//...

val okHttpClient = OkHttpClient.Builder()
    //...
    // Add BlueTriangleOkHttpInterceptor
    .addInterceptor(BlueTriangleOkHttpInterceptor(Tracker.instance!!.configuration))
    // Add BlueTriangleOkHttpEventListener
    .eventListener(BlueTriangleOkHttpEventListener(Tracker.instance!!.configuration))
    //...
    .build()
```

If your app already implements and sets an EventListener object to the OkHttpClient. The SDK provides a constructor for `BlueTriangleOkHttpEventListener` that takes in another EventListener object. You can use that as shown below:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    //...
    // Add BlueTriangleOkHttpInterceptor
    .addInterceptor(BlueTriangleOkHttpInterceptor(Tracker.instance!!.configuration))
    // Add BlueTriangleOkHttpEventListener
    .eventListener(BlueTriangleOkHttpEventListener(Tracker.instance!!.configuration, `<your event listener instance>`))
    //...
    .build()
```

### Manual Network Capture

If you are not using OkHttp or you don't want to track all requests done by OkHttp, you can manually track and submit network requests by using CapturedRequest object as shown below:

```kotlin
// Import CapturedRequest
import com.bluetriangle.analytics.networkcapture.CapturedRequest
//...

// create a captured request object
val capturedRequest = CapturedRequest()
// set the URL which also sets the host, domain, and file parameters that could be set otherwise
capturedRequest.url = "https://bluetriangle.com/platform/business-analytics/"
// start timing the request
capturedRequest.start()
// make the network request
// end timing the request on response/error 
capturedRequest.stop()

// (Optional) set HTTP response status code
capturedRequest.responseStatusCode = 200
// (Optional) set encoded body size based on response content length header
capturedRequest.encodedBodySize = 12341
// (Optional) set based on response content type
capturedRequest.requestType = RequestType.html

// submit the captured request to the tracker instance
Tracker.instance?.submitCapturedRequest(capturedRequest)
```

### Sample rate

Sample rate defines the percentage of user sessions for which network calls will be captured. A value of 0.025 means
that 2.5% of user session's network requests will be tracked.
A value of 0.0 means that no network requests will be captured for any user sessions, and a value of 1.0 will track all
network requests for all user sessions. Whether network requests will be
tracked is determined on application start, and will either be set to on or off for the entirety of the user session.

You can configure the sample rate by adding the following meta-data in your AndroidManifest.xml:

```xml
<meta-data android:name="com.blue-triangle.sample-rate.network" android:value="0.025"/>
```

The recommended setting is 1.0.

## Caching

To support offline usage tracking, timer and crash reports that cannot be sent immediately will be
cached in the application's cache directory and retried when a successful submission of a timer
occurs.

**Memory Limit**

The amount of memory the cache uses (in bytes) can be configured by setting the following meta-data in the `AndroidManifest.xml`:

```xml
<meta-data android:name="com.blue-triangle.cache.memory-limit" value="200000"></meta-data>
```

If new data is sent to the cache after the memory limit exceeds, the cache deletes the oldest data
and then adds the new data. So, only the most recently captured user data is tracked by the cache. By default the memory limit is 30Mb.

**Expiry Duration**

The amount of time (in milliseconds) the data is kept in the cache before it expires can be configured by setting the following meta-data in the `AndroidManifest.xml`:

```xml
<meta-data android:name="com.blue-triangle.cache.expiry" value="86400000"></meta-data>
```

The data that is kept longer in cache than the expiry duration is automatically deleted. By default the expiry duration is 48 hours.

## Launch Time

The Launch Time feature tracks the time it took from the start of your app launch (i.e. onCreate of your Application
class) to the time your app became fully interactive (i.e. The onResume of your launcher Activity). 

You can disable Launch Time feature by adding the following meta-data in your `AndroidManifest.xml` file.

```xml
<meta-data android:name="com.blue-triangle.launch-time.enable" android:value="false"/>
```

> Note:
> This feature is only available on API Level 29 and above

## Memory Warning

When the Java Virtual Machine cannot allocate an object because it is out of memory, and no more memory could be made available by the garbage collector, an OutOfMemoryError is thrown.

The SDK automatically tracks the memory consumption of your app and reports a Memory warning error if your code uses up more than 80% of the app's available memory (heap capacity).

To disable memory warning, add the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.memory-warning.enable" android:value="false"/>
```

## OkHttp Dependency

This SDK is dependent on OkHttp. However, `btt-android-sdk` has a dependency free submodule. To use `btt-android-sdk` without OkHttp dependency, you can import `com.github.blue-triangle-tech.btt-android-sdk:btt-android-sdk` as shown in below snippet:

```groovy
implementation 'com.github.blue-triangle-tech.btt-android-sdk:btt-android-sdk:2.8.1'
```
As a result, you won't be able to use `BlueTriangleOkHttpInterceptor` as mentioned in [OkHttp Support](#okhttp-support). Instead, you have to use [Manual Network Capture](#manual-network-capture).

## Network State Capture

BlueTriangle SDK allows capturing of network state data. Network state refers to the availability of any network interfaces on the device. Network interfaces include wifi, ethernet, cellular, etc. Once Network state capturing is enabled, the Network state is associated with all Timers, Errors and Network Requests captured by the SDK. 

To enable Network state capture, use the `isTrackNetworkStateEnabled` property on the configuration object as follows:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isTrackNetworkStateEnabled = true
```

or add the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.track-network-state.enable" android:value="true"/>
```

Network state capturing requires `android.permission.ACCESS_NETWORK_STATE` permission. So, include the permission into your `AndroidManifest.xml` file as follows:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

> Note:
> 1. If Network state capture is enabled and `ACCESS_NETWORK_STATE` permission is not granted, then the SDK won't track network state as it won't be able to.
> 2. This feature is only available on API Level 21 and above

## WebView Tracking

Websites integrated in native application that are tracked by BlueTriangle can be tracked in the
same session as the
native app. To achieve this, follow the steps below to configure the WebView:

1. Implement a WebViewClient as shown below:

```kotlin
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bluetriangle.analytics.BTTWebViewTracker
//...

class BTTWebViewClient : WebViewClient() {

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        BTTWebViewTracker.onLoadResource(view, url)
        //...
    }

}
```
or if you already have a WebViewClient, just call the `BTTWebViewTracker.onLoadResource(view, url)` in it's onLoadResource method.


2. Enable JavaScript and Dom Storage and set the WebViewClient

```kotlin 
//...
val webView = getWebViewInstance()
webView.settings.javascriptEnabled = true
webView.settings.domStorageEnabled = true
webView.webViewClient = BTTWebViewClient()
//...
```


**WebView tracking full example with Layout:**

```kotlin
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.bluetriangle.bluetriangledemo.utils.BTTWebViewClient
import com.bluetriangle.bluetriangledemo.R

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = BTTWebViewClient() // Set BTTWebViewClient declared above
        // Enable Javascript and DOM Storage
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        binding?.webView?.loadUrl("https://www.bluetriangle.com/")
    }
}

```

**WebView tracking full example with Compose:**

```kotlin
import androidx.compose.runtime.Composable
import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView
import com.bluetriangle.bluetriangledemo.utils.BTTWebViewClient

@Composable
fun WebViewScreen() {
    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = BTTWebViewClient() // Set BTTWebViewClient declared above
            // Enable Javascript and DOM Storage
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }, update = {
        it.loadUrl("https://www.bluetriangle.com/")
    })
}
```