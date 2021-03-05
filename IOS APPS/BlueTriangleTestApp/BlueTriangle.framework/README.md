# Blue Triangle Tech iOS Analytics SDK

## Requirements:

- iOS 8.0+
- Xcode 9.0+

## Installation:

Drag and drop the framework `BlueTriangle.framework` to the host project.  Select the appropriate options for your project when adding the framework. Navigate to the "General" tab in Xcode, add the framework to
"Embedded Binaries" and "Linked Frameworks and Libraries".

Navigate to the "Build Phases" tab and add a new run script build phase. Add the following as the script to be run:

```
bash "${BUILT_PRODUCTS_DIR}/${FRAMEWORKS_FOLDER_PATH}/BlueTriangle.framework/strip-frameworks.sh"
```

Note: This step removes Simulator slices from the SDK binary. This is critical for successful app submission to the App Store.

## Usage:

### Setting up the tracker

The `BTTracker` class is responsible for global configuration and timer submission.  A `BTTracker` instance must be initialized and configured before timers can be sent to Blue Triangle for processing. The best place to
initialize and configure the tracker is in the `AppDelegate` class:

* Objective-C

```objective-c
#import BlueTriangle

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    [[BTTracker sharedTracker] setSiteID:@"CF3FDD86-ED2E-4744-ADF1-258E9FF19DDC"];
    return YES;
}
```

* Swift

```swift
import BlueTriangle

func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {

    BTTracker.shared().setSiteID("CF3FDD86-ED2E-4744-ADF1-258E9FF19DDC")
    return true
}
```

### Setting up a timer

The `BTTimer` class is a simple data object used to time and track fields for Blue Triangle analytic session.  There are several pre-established fields which can be set on `BTTimer`, including page name, traffic segment,
brand value, etc. For a full list, see the constants in `BTTimer.h`.  `BTTimer` inherits from `NSObject` and can be passed from class to class allowing for easy field configuration.


* Objective-C

```objective-c
BTTimer *timer = [BTTimer timerWithPageName:@"page-name-1" trafficSegment:@"traffic-segment-1"];
[timer setCampaignName:@"campaign-1"];
[timer setCampaignMedium:@"mobile-ios"];
}
```

* Swift

```swift
let timer = BTTimer(pageName: "page-name-1", trafficSegment: "traffic-segment-s")
timer.setCampaignName("campaign-1")
timer.setCampaignMedium("mobile-ios")
}
```

### Submitting a timer

Timers are submitted to Blue Triangle through an instance of  `BTTracker`.  Any global fields set on the tracker will be applied to the timer before submission. Global fields take precedence over timer fields. Once a timer
instance is submitted, the instance can be disposed of.

* Objective-C

```objective-c
...
[[BTTracker sharedTracker] submitTimer:timer];
}
```

* Swift

```swift
...
BTTracker.shared().submitTimer(self.timer)
}
```
