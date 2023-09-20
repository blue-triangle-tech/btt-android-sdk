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
    implementation 'com.github.blue-triangle-tech:btt-android-sdk:2.8.1'
}
```

## Using the Analytics library

### Initializing the Tracker

The `Tracker` is a singleton instance that is responsible for sending timers to Blue Triangle.
Before any timers can be tracked, the `Tracker` needs to be initialized.

The best place to do this in the Android `Application`. In the `Application`, the tracker can be
initialized via the `init` static methods.

If a site ID is not set during the initialization, it will attempt to look up the site ID via the
application's meta data (as well as fallback to the deprecated String resource lookup). If a tracker
URL is not provided, the default tracker URL will be used.

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <meta-data android:name="com.blue-triangle.site-id" android:value="SITE_ID_HERE" />
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

The tracker's configuration can be updated via `BlueTriangleConfiguration` object returned
by `tracker.getConfiguration()` after the tracker is initialized.

The tracker's configuration can also be set using metadata tags in the application'
s `AndroidManifest.xml` file.

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <meta-data android:name="com.blue-triangle.site-id" android:value="SITE_ID_HERE" />
        <meta-data android:name="com.blue-triangle.debug" android:value="true" />
        <meta-data android:name="com.blue-triangle.debug.level" android:value="2" />
        <meta-data android:name="com.blue-triangle.cache.max-items"
            android:value="100" />
        <meta-data android:name="com.blue-triangle.cache.max-retry-attempts"
            android:value="3" />
        <meta-data android:name="com.blue-triangle.performance-monitor.enable"
            android:value="true" />
        <meta-data android:name="com.blue-triangle.performance-monitor.interval-ms"
            android:value="1000" />
        <meta-data android:name="com.blue-triangle.track-crashes.enable" android:value="true" />
        <meta-data android:name="com.blue-triangle.sample-rate.network" android:value="0.025" />
        <meta-data android:name="com.blue-triangle.track-anr.enable" android:value="true" />
        <meta-data android:name="com.blue-triangle.track-anr.interval-sec" android:value="5" />
        <meta-data android:name="com.blue-triangle.screen-tracking.enable" android:value="false" />
        <meta-data android:name="com.blue-triangle.launch-time.enable" android:value="false" />
    </application>
</manifest>
```

The current available meta data configuration names:

* `com.blue-triangle.site-id` configures the tracker's site ID.
* `com.blue-triangle.debug` enables or disables debug logging.
* `com.blue-triangle.debug.level` allows setting the debug logging level using the int value
  for [Android's log levels](https://developer.android.com/reference/android/util/Log#DEBUG).
* `com.blue-triangle.cache.max-items` sets the max number of timers and crashes to cache and retry
  in the event the timers cannot be sent to the server. Set this to 0 to disable this feature.
* `com.blue-triangle.cache.max-retry-attempts` sets the max number of times a timer can be re-tried.
* `com.blue-triangle.performance-monitor.enable` enables or disables tracking of memory and CPU
  usage.
* `com.blue-triangle.performance-monitor.interval-ms` adjusts the interval in milliseconds of how
  often memory and CPU measurements are taken.
* `com.blue-triangle.track-crashes.enable` enable or disable collecting and sending crash reports to
  the server.
* `com.blue-triangle.sample-rate.network` percentage of user sessions for which network calls will
  be captured. A value of 0.025 means that 2.5% of user session's network requests will be tracked.
  A value of 0.0 means that no network requests will be captured for any user sessions, and a value
  of 1.0 will track all network requests for all user sessions. Whether network requests will be
  tracked is determined on application start, and will either be set to on or off for the entirety
  of the user session.
* `com.blue-triangle.track-anr.enable` enable or disable ANR detection and sending reports to the
  server.
* `com.blue-triangle.track-anr.interval-sec` time interval for ANR warning based on track ANR is
  enabled or disabled, default to 5 seconds, minimum is 3 second, if set less then minimum allowed
  set value is ignored
* `com.blue-triangle.screen-tracking.enable` enable screen tracking which will automatically start tracking 
  activities and fragments in your app. For Jetpack Compose tracking, [see below](#screen-view-tracking).
* `com.blue-triangle.launch-time.enable` enable launch time feature which will measure the time between the user opening the app and the app being responsive.

### Using Timers

Timers are simple data objects that contain the associated times, fields related to the timer
instance, and methods to start, mark interactive, and end a timer. Fields associated with the timer
such as page name, traffic segment, brand value, etc can be set on the timer via convenience
constructors and methods. Associated fields can be set anytime during the lifetime of the timer
until submitted to the Tracker.

```java
// create and start a timer
final Timer timer = new Timer("Page Name","Traffic Segment Name").start();

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
final Timer timer = new Timer("Next Page", "Android Traffic").start();
final Intent intent = new Intent(this, NextActivity.class);
intent.putExtra(Timer.EXTRA_TIMER, timer);
startActivity(intent);

// NextActivity.java
final Timer timer = getIntent().getParcelableExtra(Timer.EXTRA_TIMER);
timer.end().submit();
```

When a timer is submitted to the tracker, the tracker sets any global fields such as site ID,
session ID, and user ID. Additional global fields may be set as needed and applied to all timers.
The timer's fields are then converted to JSON and sent via HTTP POST to the configured tracker URL.

## Screen View Tracking

Screen tracking captures screen views which can be seen on our dashboard. Screen tracking can be enabled using `isScreenTrackingEnabled` configuration flag as shown below.

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isScreenTrackingEnabled = true
```

All activities and fragments will be captured automatically. You will see fragment and activity class names on our dashboard with view count.

For composables use our side-effect `BttTimerEffect(<screen name>)` like below. Unlike Activities and Fragments, Composable screens are not automatically tracked. You need to call `BttTimerEffect` side-effect for each screen you want to track. The only parameter to this side-effect is screen name.

```kotlin
@Composable
fun UserProfileScreen() {
    BttTimerEffect("User Profile")
    // ...
}
```

For more such usage examples you can refer to our [Demo app](https://github.com/blue-triangle-tech/btt-android-demo).

If your app is using both Composables and Fragments. Then for those composables which are added to fragment no need to use `BttTimerEffect`, because its fragment is automatically tracked.

## ANR Detection

The ANR Detector identifies blocks in the main thread over a specified period of time and reports them as Application Not Responding (ANR) incidents. ANR detection can be enabled by adding the `com.blue-triangle.track-anr.enable` metadata to the manifest file. Additionally, you can configure the interval duration that qualifies as an ANR state by using the `com.blue-triangle.track-anr.interval-sec` metadata.

Alternatively, you can set these configurations in the `BlueTriangleConfiguration` object when initializing your Tracker instance as shown below.

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isTrackAnrEnabled = true
configuration.trackAnrIntervalSec = 3
```

By default, the ANR interval is set to 5 seconds.

## Network Capture

The tracker now also supports capturing network requests. This can be done automatically
using [OkHttp Interceptors](https://square.github.io/okhttp/features/interceptors/) or manually.
Check the application's `AndroidManifest.xml` file for the metadata
configuration `com.blue-triangle.sample-rate.network` The recommended setting is 1.0 to capture all
network requests.

### OkHttp Support

OkHttp support is provided in an additional library that can be included via the following
dependency:

```
dependencies {
    ...
    implementation 'com.github.blue-triangle-tech:btt-android-sdk:2.8.1'
    implementation 'com.github.blue-triangle-tech:btt-android-sdk-okhttp:2.8.1' 
}
```

Once included, the `BlueTriangleOkHttpInterceptor` becomes available to add as an interceptor to the
OkHttp client.

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

The max number of timer and crashes to cache can be configured and this feature can be completely
disabled by setting the max cache items configuration to 0. The default is 100.

If the cache becomes full, the cache will be rotated to remove the oldest item and insert the newest
item.

Also the max number of retry attempts can be configured per timer/crash report as well. If the max
number of retries is exceeded, the timer/crash is dropped. The default is 3 tries.

## Launch Time

The Launch Time feature tracks the time it took from the start of your app launch (i.e. onCreate of your Application class) to the time your app became fully interactive (i.e. The onResume of your launcher Activity). Launch time feature can be enabled using `isLaunchTimeEnabled` configuration flag as shown below.

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isLaunchTimeEnabled = true
```
or by adding the following meta-data in your `AndroidManifest.xml` file.

```xml
<meta-data android:name="com.blue-triangle.launch-time.enable" android:value="true" />
```
> Note:
This feature is only available on API Level 29 and above

## Publishing the Analytics SDK Package

The Analytics SDK is published through [JitPack](https://jitpack.io/).

To publish a new version of the library:

1. Update the version in the Analytics library `build.gradle` file and in this readme file.
2. Create a [GitHub tag/release](https://github.com/blue-triangle-tech/btt-android-sdk/releases) for
   the new version.
