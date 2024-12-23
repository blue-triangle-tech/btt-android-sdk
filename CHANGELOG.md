# Blue Triangle 2.17.0 Latest
### New Features
- Ability to remotely ignore automatically tracked screen names. Developers can configure a list of page names from the BlueTriangle portal, which will be ignored from tracking. Any Activity/Fragment class name or page name given in Compose `BttTimerEffect(_)` side-effect will also be ignored. These names are case-sensitive. This feature allows developers to remotely calibrate the list of Activities/Fragments or Composables they want to track at any time.

# 2.16.0
### New Features
- Ability to remotely override Network Sample Rate
- Improved method for testing SDK integration using system properties via adb shell, for testing full Network Sample Rate within debug environment

# 2.15.0
### New Features
- Added support for Custom Variables

# 2.14.0
### Feature Improvements
- Adding support for collecting Cellular Network Type

# 2.13.1
### Feature Improvements
- Adding support for collecting Android Device Model

# 2.13.0
### Feature Improvements
- Added session expiry after 30 minutes of inactivity
- Session will now be maintained within 30 minutes duration across app background, app kills and system reboots
- Automatically updates session in WebView on session expiry

# 2.12.2
### Bug Fixes
- Hot fix for FragmentLifecycleTracker crash

# 2.12.1
### Bug Fixes
- Hot Fix for ConcurrentModificationException and NullPointerException

# 2.12.0
### New Features
- Implemented Warm Launch
- Added Cart Count and Cart Count Checkout Revenue fields to Timer
- Automatically taking Order Time to be Timer's end time

# 2.11.1
### Bug Fixes
- Fixed crash in NetworkTimelineTracker

# 2.11.0
### New Features
- Automatic Hot and Cold Launch Time Tracking
### Bug Fixes
- Fixed cases where Network State was not tracked with client side Network Errors
- Fixed crash in Network State implementation for Android Versions 11 and below

# 2.10.0
### Feature Improvements
- SDK can now be configured with only the Site ID, with all stat tracking enabled by default
### Minor Bug Fixes
- Fixed bug related to debug logging

# 2.9.0
### New Features
- Network state capture
- WebView tracking
- Memory Warning
### Feature Improvements
- Optimized CPU and Memory Tracking
- Improved offline caching mechanism with the inclusion of Memory limit and Expiration.
- Added support for capturing Network Errors

# 2.8.1
### New Features
- App Launch Time tracking
### Bug Fixes
- Fixed edge case where Screen Tracking performance time reported incorrectly for Composable.
- Fixed page name reported for ANR Warnings.

# 2.8.0
### New Features
- Automated Screen View Tracking activities, fragments, and composables
- Application Not Responding tracking and reporting as 'ANRWarnings'
### Bug Fixes
- All crashes and ANRWarnings now correctly report the screen where the error occurs