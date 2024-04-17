# Blue Triangle Tech Android Analytics SDK

[![jitpack.io build status](https://jitpack.io/v/blue-triangle-tech/btt-android-sdk.svg)](https://jitpack.io/#blue-triangle-tech/btt-android-sdk)

## Installation

Add the Maven repository to the project's `build.gradle` file:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Or** `settings.gradle` file:

```
dependencyResolutionManagement {
    ...
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the package dependency to your application's `build.gradle` file:

```
dependencies {
    ...
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

## Using the Analytics library

### Initializing the Tracker

The `Tracker` is a singleton instance that is responsible for sending timers to Blue Triangle.
Before any timers can be tracked, the `Tracker` needs to be initialized.

The best place to do this in the Android `Application`. In the `Application`, the tracker can be
initialized via the `init` static methods.

If a site ID is not set during the initialization, it will attempt to look up the site ID via the
application's meta-data (as well as fallback to the deprecated String resource lookup). If a tracker
URL is not provided, the default tracker URL will be used.

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <meta-data android:name="com.blue-triangle.site-id" android:value="SITE_ID_HERE"/>
    </application>
</manifest>
```

```java
// init with all defaults, use site ID from meta data
Tracker.init(getApplicationContext());
// init with given site ID
        Tracker.init(getApplicationContext(),"BTT_SITE_ID");
// init with given site ID and tracker URL
        Tracker.init(getApplicationContext(),"BTT_SITE_ID","https://webhook.site/5afd62e7-acde-4cf3-825c-c40c491b0714");
```

### Configuration

Blue Triangle SDK allows you to enable/disable or configure to certain features through the
use of configurations. Each feature mentioned in this document provides ways to customize it through available
configurations.
There are two possible ways to use any configuration:

1. **Programmatically**<br/>
   For each configuration, there is a property in the `BlueTriangleConfiguration`. To apply configuration
   programmatically, set the corresponding property while initializing the
   SDK. [See Initialization](#initializing-the-tracker).

   For instance, to enable logging programmatically, the initialization code will look like below:

   ```kotlin
   val config = BlueTriangleConfiguration()
   config.isDebug = true
   Tracker.init(application, config)
   ```

2. **Manifest Meta-data**<br/>
   To provide the configuration in AndroidManifest.xml, add `<meta-data>` tags inside the `<application>` tag as
   follows:

   ```xml
   <manifest xmlns:android="http://schemas.android.com/apk/res/android">
       <application>
           <meta-data android:name="<configuration-name>" android:value="<configuration-value>"/>
       </application>
   </manifest>
   ```

> **Note:** <br/>
> If you provide configuration both in AndroidManifest.xml and through BlueTriangleConfiguration object, then
> the value from the AndroidManifest.xml meta-data will be given precedence.

### Using Timers

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

Screen tracking captures screen views which can be seen on our dashboard. Screen tracking can be enabled
using `isScreenTrackingEnabled` configuration flag as shown below.

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isScreenTrackingEnabled = true
```

or by adding the following meta-data:

```xml

<meta-data android:name="com.blue-triangle.screen-tracking.enable" android:value="false"/>
```

All activities and fragments will be captured automatically. You will see fragment and activity class names on our
dashboard with view count.

For composables use our side-effect `BttTimerEffect(<screen name>)` like below. Unlike Activities and Fragments,
Composable screens are not automatically tracked. You need to call `BttTimerEffect` side-effect for each screen you want
to track. The only parameter to this side-effect is screen name.

```kotlin
@Composable
fun UserProfileScreen() {
    BttTimerEffect("User Profile")
    // ...
}
```

For more such usage examples you can refer to our [Demo app](https://github.com/blue-triangle-tech/btt-android-demo).

If your app is using both Composables and Fragments. Then for those composables which are added to fragment no need to
use `BttTimerEffect`, because its fragment is automatically tracked.

> **Note:**<br/>
> Enabling screen tracking also allows co-relating errors and network requests with the screens. The crash or network
> request associates screen name on which this crash or network request occurred. Thus, if screen tracking is disabled,
> the page name of most recently started manual Timer's page name will be sent along with the crash or network request.

## ANR Detection

The ANR Detector identifies blocks in the main thread over a specified period of time and reports them as Application
Not Responding (ANR) incidents. ANR detection can be enabled by adding the `com.blue-triangle.track-anr.enable` metadata
to the manifest file. Additionally, you can configure the interval duration that qualifies as an ANR state by using
the `com.blue-triangle.track-anr.interval-sec` metadata.

Alternatively, you can set these configurations in the `BlueTriangleConfiguration` object when initializing your Tracker
instance as shown below.

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isTrackAnrEnabled = true
configuration.trackAnrIntervalSec = 3
```

or by adding the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.track-anr.enable" android:value="true"/>
<meta-data android:name="com.blue-triangle.track-anr.interval-sec" android:value="5"/>
```

By default, the ANR interval is set to 5 seconds.

## Track Crashes

The SDK provides automatic tracking and reporting of crashes. To enable this feature, just set
the `isTrackCrashesEnabled` property to true in the configuration object as shown below:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isTrackCrashesEnabled = true
```

or add the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.track-crashes.enable" android:value="true"/>
```

## Network Capture

The tracker now also supports capturing network requests. This can be done automatically
using [OkHttp Interceptors](https://square.github.io/okhttp/features/interceptors/) or manually.

#### Sample rate

Sample rate defines the percentage of user sessions for which network calls will be captured. A value of 0.025 means
that 2.5% of user session's network requests will be tracked.
A value of 0.0 means that no network requests will be captured for any user sessions, and a value of 1.0 will track all
network requests for all user sessions. Whether network requests will be
tracked is determined on application start, and will either be set to on or off for the entirety of the user session.

You can configure the sample rate by setting the `networkSampleRate` property in the configuration object as shown
below:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.networkSampleRate = 0.025
```

or by adding the following meta-data in your AndroidManifest.xml:

```xml
<meta-data android:name="com.blue-triangle.sample-rate.network" android:value="0.025"/>
```

The recommended setting is 1.0 to capture all network requests.

### OkHttp Support

OkHttp support is provided out of the box with the SDK. Just add the `BlueTriangleOkHttpInterceptor` interceptor to
your `OkHttpClient` as follows:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(BlueTriangleOkHttpInterceptor(Tracker.instance!!.configuration))
    .build()
```

The `BlueTriangleOkHttpInterceptor` will automatically handle capturing network requests and
submitting them to the tracker.

### Manual Network Capture

For other network capture requirements, captured requests can be manually created and submitted to
the tracker.

```kotlin
// create a captured request object
val capturedRequest = CapturedRequest()
// set the URL which also sets the host, domain, and file parameters that could be set otherwise
capturedRequest.url = "https://bluetriangle.com/platform/business-analytics/"
// start timing the request
capturedRequest.start()
// make the network request
// end timing the request
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

## Caching

To support offline usage tracking, timer and crash reports that cannot be sent immediately will be
cached in the application's cache directory and retried when a successful submission of a timer
occurs.

**Memory Limit**

The amount of memory the cache uses (in bytes) can be configured using the `cacheMemoryLimit` property of the
configuration object:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.cacheMemoryLimit = 200000L
```
or by setting the following meta-data in the `AndroidManifest.xml`:

```xml
<meta-data android:name="com.blue-triangle.cache.memory-limit" value="200000"></meta-data>
```

If new data is sent to the cache after the memory limit exceeds, the cache deletes the oldest data
and then adds the new data. So, only the most recently captured user data is tracked by the cache. By default the memory limit is 30Mb.

**Expiry Duration**

The amount of time (in milliseconds) the data is kept in the cache before it expires can be configured using the `cacheExpiryDuration` property of the configuration object:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.cacheExpiryDuration = 86400000L
```

or by setting the following meta-data in the `AndroidManifest.xml`:

```xml
<meta-data android:name="com.blue-triangle.cache.expiry" value="86400000"></meta-data>
```

The data that is kept longer in cache than the expiry duration is automatically deleted. By default the expiry duration is 48 hours.

## Launch Time

The Launch Time feature tracks the time it took from the start of your app launch (i.e. onCreate of your Application
class) to the time your app became fully interactive (i.e. The onResume of your launcher Activity). Launch time feature
can be enabled using `isLaunchTimeEnabled` configuration flag as shown below.

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isLaunchTimeEnabled = true
```

or by adding the following meta-data in your `AndroidManifest.xml` file.

```xml
<meta-data android:name="com.blue-triangle.launch-time.enable" android:value="false"/>
```

> Note:
> This feature is only available on API Level 29 and above

## Memory Warning

Memory warning is an error that is reported when the code uses up more than 80% of the app's available memory (heap capacity).

To enable memory warning, use the `isMemoryWarningEnabled` property on the configuration object as follows:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isMemoryWarningEnabled = true
```

or add the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.memory-warning.enable" android:value="true"/>
```

## OkHttp Dependency

This SDK is dependent on OkHttp. However, `btt-android-sdk` has a dependency free submodule. To use `btt-android-sdk` without OkHttp dependency, you can import `com.github.blue-triangle-tech.btt-android-sdk:btt-android-sdk` as shown in below snippet:

```groovy
implementation 'com.github.blue-triangle-tech.btt-android-sdk:btt-android-sdk:2.8.1'
```
As a result, you won't be able to use `BlueTriangleOkHttpInterceptor` as mentioned in [OkHttp Support](#okhttp-support). Instead, you have to use [Manual Network Capture](#manual-network-capture).

### Network State Capture

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
class BTTWebViewClient : WebViewClient() {

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        BTTWebViewTracker.onLoadResource(view, url)
    }

}
```
or if you already have a WebViewClient, just call the `BTTWebViewTracker.onLoadResource(view, url)` in it's onLoadResource method.


2. Enable JavaScript and Dom Storage and set the WebViewClient

```kotlin 
val webView = getWebViewInstance()
webView.settings.javascriptEnabled = true
webView.settings.domStorageEnabled = true
webView.webViewClient = BTTWebViewClient()
```


