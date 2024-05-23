# Blue Triangle Tech Android Analytics SDK

[![jitpack.io build status](https://jitpack.io/v/blue-triangle-tech/btt-android-sdk.svg)](https://jitpack.io/#blue-triangle-tech/btt-android-sdk)

The Blue Triangle SDK for Android enables application owners to track their users’ experience so they can focus on user experience issues that impact their business outcomes.

## Supported metrics
- Performance & Network Timings
 - Main Timers
 - Network Timers
 - Custom Timers
- Errors & Crashes
- Application Not Responding (ANR)
- HTTP Response Codes
- App Crashes
- Device Stats & Session Attributes
 - OS/OS Version
 - App Version
 - Device Type
 - Geographical/Country
 - CPU Usage
 - Memory Warnings
 - Memory/Out of Memory
 - Hot/Cold Launch
Coming Soon
- Network Type
 

## Mandatory Installation Steps

### SDK Installation

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
    implementation 'com.github.blue-triangle-tech:btt-android-sdk:2.10.0'
}
```

**Note: For Gradle Plugin Version 8.2.0+**

If project uses gradle plugin version 8.2.0 and above, use exclude with dependency like below.

```groovy
implementation("com.github.blue-triangle-tech:btt-android-sdk:2.10.0") {
    exclude("com.squareup.okhttp3", "okhttp-bom")
}
```

### Site ID Configuration

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

### Native View Performance Tracking- Mandatory
Screen tracking automatically captures all activities and fragments in your app. You will see fragment and activity class names on our dashboard with view count.

Tracking Composables (If using composables, these steps are mandatory) 
Unlike Activities and Fragments, Composable screens are not automatically tracked. You need to call BttTimerEffect side-effect for each screen you want to track. If your app uses Jetpack Compose UI, use our side-effect BttTimerEffect(<screen name>) shown below. The only parameter to this side-effect is screen name:

```kotlin

@Composable
fun UserProfileScreen() {
    BttTimerEffect("User Profile")
    // ...
}
```

If your app is using both Composables and Fragments, then for those composables which are added to a fragment it is not necessary to call BttTimerEffect, because the fragment is automatically tracked.

 
You can disable Screen tracking by adding the following meta-data:

```kotlin

<meta-data android:name="com.blue-triangle.screen-tracking.enable" android:value="false"/>
```

**Note:**
Screen tracking also allows co-relating errors and network requests with the screens. The crash or network request associates screen name on which this crash or network request occurred. Thus, if screen tracking is disabled, the page name of most recently started manual Timer's page name will be sent along with the crash or network request.

 
### Native View/Webview Tracking/Session Stitching- Mandatory
Native Webviews that are integrated into your native application can be tracked in the same session as the native app. To achieve this, follow the steps below to configure the webview:

Implement a **WebViewClient** as shown below:

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

or if you already have a WebViewClient, call the following in its onLoadResource method:

```kotlin
BTTWebViewTracker.onLoadResource(view, url) 
```

**Enable JavaScript and Dom Storage and set the WebViewClient**

WebView tracking full example with Layout:

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

### Network Capture  - Mandatory
To capture network details, you need to add one of these code snippets.

#### OkHttp Support
The SDK provides plug and play support for OkHttp which allows you to easily track all your network requests going through an OkHttpClient. Add the BlueTriangleOkHttpInterceptor & BlueTriangleOkHttpEventListener 

to your OkHttpClient as follows:

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

If your app already implements and sets an EventListener object to the OkHttpClient, the SDK provides a constructor for BlueTriangleOkHttpEventListener that takes in another EventListener object. You can use this as shown below:

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


#### Manual Network Capture
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
### Network State Capture- Mandatory

The BlueTriangle SDK allows capturing of network state data. Network state refers to the availability of any network interfaces on the device. Network interfaces include wifi, ethernet, cellular, etc. Once Network state capturing is enabled, the Network state is associated with all Timers, Errors and Network Requests captured by the SDK.


This requires the android.permission.ACCESS_NETWORK_STATE permission. Include the permission into your AndroidManifest.xml file as follows:

```kotlin

<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

```

To disable Network state capture, add the following meta-data:

```kotlin
<meta-data android:name="com.blue-triangle.track-network-state.enable" android:value="false"/>
```

**Note:**

- If Network state capture is enabled and the ACCESS_NETWORK_STATE permission is not granted, then the SDK will not track network state.
- **This feature is only available on API Level 21 and above**

## Recommended (Optional) Configurations

### Network Capture Sample Rate
The network sample rate will determine the percentage of session network requests that are captured. For example a value of 0.05 means that network capture will be randomly enabled for 5% of user sessions. Network sample rate value should be between 0.0 to 1.0 representing fraction value of percent 0 to 100. The default value is 0.05, i.e only 5% of sessions network request are captured.

Configure the sample rate by adding the following meta-data in your AndroidManifest.xml:
```kotlin

<meta-data android:name="com.blue-triangle.sample-rate.network" android:value="0.05"/>

```

To disable network capture, set this value to 0.0 during configuration.

It is recommended to have 100% sample rate while developing/debugging, by setting this value to 1.0 during configuration.

### Blue Triangle Campaign Configuration Fields

The following fields can be used to identify and segment users for optimized analytics contextualization. They can be configured in the SDK and modified in the app in real time, and they show in the Blue Triangle portal as parameters for reporting.


| Field                                        | Implication                                                                                                                                                           |  
|----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| abTestID="MY_AB_TEST_ID"                     | Capture a variable that allows us to understand a live AB test of two variants in the app.                                                                            | 
| campaignMedium="MY_CAMPAIGN_MEDIUM"          | Understand the general reason the journey started (email, paid search, organic search, etc)                                                                           | 
| campaignName="MY_CAMPAIGN_NAME"              | Understand the campaign name that started the journey.                                                                                                                | 
| campaignSource="MY_CAMPAIGN_SOURCE"          | Understanding the type of marketing campaign.                                                                                                                         | 
| dataCenter="MY_DATA_CENTER"                  | Understand if you have multiple data centers that serve your customers you can group data by them.                                                                    | 
| trafficSegmentName="MY_SEGMENT_NAME"         | This can be used to segment environment type.  For instance, we can use this to understand if you have beta vs prod but both are live versions of the app.            | 

```kotlin
BlueTriangle.configure { config in
    config.abTestID = "MY_AB_TEST_ID"
    config.campaignMedium = "MY_CAMPAIGN_MEDIUM"
    config.campaignName = "MY_CAMPAIGN_NAME"
    config.campaignSource = "MY_CAMPAIGN_SOURCE"
    config.dataCenter = "MY_DATA_CANTER "
    config.trafficSegmentName = "MY_TRAFFIC_SEGEMENT_NAME"
}
```

### Custom Timers 
While **Screen Views are automatically tracked upon installation**, Custom Timers can also be configured if needed. 

```kotlin

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
Timers implement a Parcelable to allow timers to be passed via Bundle such as between activities in an Intent.

```kotlin

// MainActivity.java
final Timer timer=new Timer("Next Page","Android Traffic").start();
final Intent intent=new Intent(this,NextActivity.class);
intent.putExtra(Timer.EXTRA_TIMER,timer);
startActivity(intent);

// NextActivity.java
final Timer timer=getIntent().getParcelableExtra(Timer.EXTRA_TIMER);
timer.end().submit();
```

When a timer is submitted to the tracker, the tracker sets any global fields such as site ID, session ID, and user ID. Additional global fields may be set as needed and applied to all timers. The timer's fields are then converted to JSON and sent via HTTP POST to the configured tracker URL.


### ANR Detection

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

### Memory Warning 
When the Java Virtual Machine cannot allocate an object because it is out of memory, and no more memory could be made available by the garbage collector, an OutOfMemoryError is thrown.

The SDK automatically tracks the memory consumption of your app and reports a Memory warning error if your code uses up more than 80% of the app's available memory (heap capacity).

To disable memory warning, add the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.memory-warning.enable" android:value="false"/>
```


### Memory Usage
Memory usage is the amount of memory used by the code during the Timer interval. This is measured in number of bytes.

Against each timer, 3 Memory measurements are being sent, minimum, maximum and average.

The memory that an app can actually take into use is just a fraction of the whole device's memory capacity. So, the memory data that is sent along with the timer is captured based on our App-level memory usage.

To set the interval (in ms) at which the Memory usage is being captured, set the following field:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.performanceMonitorIntervalMs = 1000

```
To disable Memory usage set the following field:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isPerformanceMonitorEnabled = false

```

### CPU Usage
CPU Usage is the amount of CPU being used by the code during the Timer interval. This is measured in the form of 0-100%.

Against each timer, 3 CPU measurements are being sent, minimum, maximum and average.

In analytics.rcv payload data json, minCPU, maxCPU and avgCPU are being used to send the respective CPU usage. In addition to this, NATIVEAPP.numberOfCPUCores field is added to the payload data to report the number of cores in the device. To express this in a 0% to 100% format, Blue Triangle calculates the CPU usage by dividing number of CPU cores. This will give you a percentage value between 0% and 100%. 
 
0% to 100% format = Total current CPU usage on Instruments / Number of CPU cores. 
 
For example, if you have 4 CPU cores and your current usage is 300%. then actual BTT CPU usage 300% / 4 = 75%. This indicates that CPU is being utilized at 75% of its total capacity.

 

To set the interval (in ms) at which the CPU usage is being captured, set the following field in BlueTriangleConfiguration:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.performanceMonitorIntervalMs = 1000
```

To disable CPU usage set the following field in BlueTriangleConfiguration:

```kotlin
val configuration = BlueTriangleConfiguration()
configuration.isPerformanceMonitorEnabled = false
``` 



### Track Crashes

The SDK automatically tracks and reports all crashes. To disable this feature, add the following meta-data:

```xml
<meta-data android:name="com.blue-triangle.track-crashes.enable" android:value="false"/>
```


### Caching

To support offline usage tracking, timer and crash reports that cannot be sent immediately will be
cached in the application's cache directory and retried when a successful submission of a timer
occurs.

#### Memory Limit

The amount of memory the cache uses (in bytes) can be configured by setting the following meta-data in the `AndroidManifest.xml`:

```xml
<meta-data android:name="com.blue-triangle.cache.memory-limit" value="200000"></meta-data>
```

If new data is sent to the cache after the memory limit exceeds, the cache deletes the oldest data
and then adds the new data. So, only the most recently captured user data is tracked by the cache. By default the memory limit is 30Mb.

#### Expiry Duration

The amount of time (in milliseconds) the data is kept in the cache before it expires can be configured by setting the following meta-data in the `AndroidManifest.xml`:

```xml
<meta-data android:name="com.blue-triangle.cache.expiry" value="86400000"></meta-data>
```

The data that is kept longer in cache than the expiry duration is automatically deleted. By default the expiry duration is 48 hours.

### Launch Time

BlueTriangle tracks app launch performance. Launch time refers to the duration it takes for an app to become ready for user interaction after it has been started. BlueTriangle automatically tracks both hot launch and cold launch.

#### Cold Launch

A cold launch is a launch wherein the app process was not already in main memory. This can happen if the System or user terminated your apps process or the app is launching for the first time since it's installed/updated or since the device was booted.  

The SDK measures the cold launch latency, which is the time between the `onCreate` of the BlueTriangle SDK's `ContentProvider` and `onResume` call for the first `Activity`.

#### Hot Launch

A Hot Launch occurs when the app is already running in the background and is brought to the foreground. This type of launch is typically faster since the app's state is preserved in memory.

The BlueTriangle SDK measures the hot launch latency, which is the time between the `onStart` and `onResume` of the first `Activity` after the app is brought into foreground.

When user lock the device while app was on screen and unlocks it, the System calls the same lifecycle callbacks as when the user puts the app in background and brings it to foreground. Hence, unlocking followed by lock while app was active is tracked as Hot Launch.

You can disable Launch Time feature by adding the following meta-data in your `AndroidManifest.xml` file.

```xml
<meta-data android:name="com.blue-triangle.launch-time.enable" android:value="false"/>
```

> Note:
> This feature is only available on API Level 29 and above


## How to Test the Android SDK Integration

### Site ID

Log onto your account on the [Blue Triangle Portal](https://portal.bluetriangle.com), head over to "Native App -> Performance overview" and see that you can observe some data.

### Memory Warning

You can declare and call the following function to generate a memory warning:

```kotlin

fun testMemoryWarning() { 
 Thread { 
 val timer = Timer("MemoryWarningTimer", "AndroidNative") 
 timer.start() 
 Thread.sleep(1000) 
 val memoryBlock = ByteArray((Runtime.getRuntime().maxMemory() * 0.85).toInt()) 
 Log.d("MemoryWarningTest", "AllocatedMemory: ${memoryBlock.size}") 
 Thread.sleep(1000) 
 timer.submit() 
 }.start() 
}
```

### ANR Tracking
To test ANR Tracking, you can declare and call the following function on the main thread:

```kotlin
fun testANRTracking() {  val startTime = System.currentTimeMillis()  while(System.currentTimeMillis() - startTime <= (6000)) {
  } 
}
```

### Crash Tracking

To test Crash Tracking, you can declare and call the following function:

```kotlin

fun testCrashTracking() {  throw ArithmeticException() }
```

## Further General Information

### Automated Native View Timers
The Blue Triangle SDK automatically sets up timers to measure the appearance of Native Views in the Mobile app:

![image](https://github.com/blue-triangle-tech/btt-android-sdk/assets/147184142/ec34a2bc-3a26-4c89-9f8f-5d1f8440fa02)
