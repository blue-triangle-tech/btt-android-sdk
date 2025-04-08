# Blue Triangle 2.17.0 Latest, April 7 2025
### New Features
- Added support for Blue Triangle and Microsoft Clarity session mapping. Added ability to detect Clarity SDK present in host app, if present associate clarity session url with Timers.

# Blue Triangle 2.16.2, Feb 3 2025
### New Features
- Ability to remotely disable SDK. SDK now has a remote configuration field that can enable/disable SDK on the fly without needing any code push. Once the SDK receives the setting from portal as disabled, the SDK will turn off all tracking and reporting.

# Blue Triangle 2.16.1, Dec 27 2024
### New Features
- Ability to remotely ignore automatically tracked screen names. Developers can configure a list of page names from the BlueTriangle portal, which will be ignored from tracking. Any Activity/Fragment class name or page name given in Compose `BttTimerEffect(_)` side-effect will also be ignored. These names are case-sensitive. This feature allows developers to remotely calibrate the list of Activities/Fragments or Composables they want to track at any time.

# Blue Triangle 2.16.0, Dec 6 2024
### New Features
- Ability to remotely override Network Sample Rate
- Improved method for testing SDK integration using system properties via adb shell, for testing full Network Sample Rate within debug environment

# Blue Triangle 2.15.0, Oct 23 2024
### New Features
- Added support for Custom Variables

# Blue Triangle 2.14.0, Oct 8 2024
### Feature Improvements
- Adding support for collecting Cellular Network Type

# Blue Triangle 2.13.1, Sept 20 2024
### Feature Improvements
- Adding support for collecting Android Device Model

# Blue Triangle 2.13.0, Sept 4 2024
### Feature Improvements
- Added session expiry after 30 minutes of inactivity
- Session will now be maintained within 30 minutes duration across app background, app kills and system reboots
- Automatically updates session in WebView on session expiry

# Blue Triangle 2.12.2, Aug 5 2024
### Bug Fixes
- Hot fix for FragmentLifecycleTracker crash

# Blue Triangle 2.12.1, Jul 24 2024
### Bug Fixes
- Hot Fix for ConcurrentModificationException and NullPointerException

# Blue Triangle 2.12.0, Jun 17 2024
### New Features
- Implemented Warm Launch
- Added Cart Count and Cart Count Checkout Revenue fields to Timer
- Automatically taking Order Time to be Timer's end time

# Blue Triangle 2.11.1, Jun 10 2024
### Bug Fixes
- Fixed crash in NetworkTimelineTracker

# Blue Triangle 2.11.0, May 21 2024
### New Features
- Automatic Hot and Cold Launch Time Tracking
### Bug Fixes
- Fixed cases where Network State was not tracked with client side Network Errors
- Fixed crash in Network State implementation for Android Versions 11 and below

# Blue Triangle 2.10.0, May 2 2024
### Feature Improvements
- SDK can now be configured with only the Site ID, with all stat tracking enabled by default
### Minor Bug Fixes
- Fixed bug related to debug logging

# Blue Triangle 2.9.0, Mar 18 2024
### New Features
- Network state capture
- WebView tracking
- Memory Warning
### Feature Improvements
- Optimized CPU and Memory Tracking
- Improved offline caching mechanism with the inclusion of Memory limit and Expiration.
- Added support for capturing Network Errors

# Blue Triangle 2.8.1, Sept 21 2023
### New Features
- App Launch Time tracking
### Bug Fixes
- Fixed edge case where Screen Tracking performance time reported incorrectly for Composable.
- Fixed page name reported for ANR Warnings.

# Blue Triangle 2.8.0, Jul 14 2023
### New Features
- Automated Screen View Tracking activities, fragments, and composables
- Application Not Responding tracking and reporting as 'ANRWarnings'
### Bug Fixes
- All crashes and ANRWarnings now correctly report the screen where the error occurs