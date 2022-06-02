# Blue Triangle Tech Android Analytics SDK


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

Add the package dependency to your application's `build.gradle` file:

```
dependencies {
    ...
    implementation 'com.github.blue-triangle-tech:btt-android-sdk:2.6.8'
}
```

## Using the Analytics library

### Initializing the Tracker

The `Tracker` is a singleton instance that is responsible for sending timers to Blue Triangle. Before any timers can be tracked, the `Tracker` needs to be initialized. The best place to do this in the Android `Application`. In the `Application`, the tracker can be initialized via the `init` static methods. If a site ID is not set during the initialization, it will attempt to look up the site ID via the application's String resources with a name `btt_site_id` as shown below. If a tracker URL is not provided, the default tracker URL will be used.

```
<string name="btt_site_id" translatable="false">BTT_SITE_ID</string>
```

```java
// init with all defaults, use site ID in string resources file
Tracker.init(getApplicationContext());
// init with given site ID
Tracker.init(getApplicationContext(), "BTT_SITE_ID");
// init with given site ID and tracker URL
Tracker.init(getApplicationContext(), "BTT_SITE_ID", "https://webhook.site/5afd62e7-acde-4cf3-825c-c40c491b0714");
```

### Using Timers

Timers are simple data objects that contain the associated times, fields related to the timer instance, and methods to start, mark interactive, and end a timer. Fields associated with the timer such as page name, traffic segment, brand value, etc can be set on the timer via convenience constructors and methods. Associated fields can be set anytime during the lifetime of the timer until submitted to the Tracker.

```java
// create and start a timer
final Timer timer = new Timer("Page Name", "Traffic Segment Name").start();

// do work

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

Timers implement `Parcelable` to allow timers to be passed via `Bundle` such as between activities in an `Intent`.

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

When a timer is submitted to the tracker, the tracker sets any global fields such as site ID, session ID, and user ID. Additional global fields may be set as needed and applied to all timers. The timer's fields are then converted to JSON and sent via HTTP POST to the configured tracker URL.


## Publishing the Analytics SDK Package

The Analytics SDK is published through [JitPack](https://jitpack.io/).

To publish a new version of the library:

1. Update the version in the Analytics library `build.gradle` file and in this readme file.
2. Create a [GitHub tag/release](https://github.com/blue-triangle-tech/btt-android-sdk/releases) for the new version.
