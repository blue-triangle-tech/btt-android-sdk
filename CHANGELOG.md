## 2.16.0
- [feature] Ability to remotely override Network Sample Rate
- [feature] Improved method for testing SDK integration using system properties via adb shell, for testing full Network Sample Rate within debug environment

## 2.15.0
- [feature] Added support for Custom Variables

## 2.14.0
- [feature] Adding support for collecting Cellular Network Type

## 2.13.1
- [feature] Adding support for collecting Android Device Model

## 2.13.0
- [changed] Added session expiry after 30 minutes of inactivity
- [changed] Session will now be maintained within 30 minutes duration across app background, app kills and system reboots
- [changed] Automatically updates session in WebView on session expiry

## 2.12.2
- [fixed] Hot fix for FragmentLifecycleTracker crash

## 2.12.1
- [fixed] Hot Fix for ConcurrentModificationException and NullPointerException

## 2.12.0
- [feature] Implemented Warm Launch
- [feature] Added Cart Count and Cart Count Checkout Revenue fields to Timer
- [feature] Automatically taking Order Time to be Timer's end time

## 2.11.1
- [fixed] Fixed crash in NetworkTimelineTracker

## 2.11.0
- [feature] Automatic Hot and Cold Launch Time Tracking
- [fixed] Fixed cases where Network State was not tracked with client side Network Errors
- [fixed] Fixed crash in Network State implementation for Android Versions 11 and below

## 2.10.0
- [changed] SDK can now be configured with only the Site ID, with all stat tracking enabled by default
- [fixed] Fixed bug related to debug logging

## 2.9.0
- [feature] Network state capture
- [feature] WebView tracking
- [feature] Memory Warning
- [changed] Optimized CPU and Memory Tracking
- [changed] Improved offline caching mechanism with the inclusion of Memory limit and Expiration.
- [feature] Added support for capturing Network Errors

## 2.8.1
- [feature] App Launch Time tracking
- [fixed] Fixed edge case where Screen Tracking performance time reported incorrectly for Composable.
- [fixed] Fixed page name reported for ANR Warnings.

## 2.8.0
- [feature] Automated Screen View Tracking activities, fragments, and composables
- [feature] Application Not Responding tracking and reporting as 'ANRWarnings'
- [feature] All crashes and ANRWarnings now correctly report the screen where the error occurs