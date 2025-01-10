# Interview Questions
Part 1: Foreground Services
--
1. Explain the difference between a started service and a bound service in Android.
   Answer: 
   - A started service is initiated using startService() and runs indefinitely, performing a single operation. 
     It must be stopped explicitly using stopService() or stopSelf(). 
   - A bound service - allows components to bind to it using bindService(), providing an interface for clients to interact with it. 
    The service is only active as long as there are bound clients.
   
2. What are the advantages of using WorkManager over JobScheduler and AlarmManager?
    Answer: WorkManager provides several advantages, including:
    - Compatibility with API levels as low as 14.
    - Ability to handle constraints like network availability, battery status, and charging status.
    - Persistence of work across app restarts and system reboots.
    - Automatic handling of task retries upon failure.
    - Chaining of dependent tasks for complex workflows.
    - 
3. Explain the purpose of ForegroundServiceType introduced in Android 12.
    Answer: 
   - ForegroundServiceType was introduced in Android 12 to categorize foreground services for better power management and resource allocation. 
   - It helps the system optimize battery usage by understanding the type of work the foreground service is performing, such as location updates, media playback, or data sync.

4. Explain the importance of the onDestroy() method in a Service.
    Answer: 
    The onDestroy() method is called when the service is no longer used and is being destroyed. 
    It's essential for releasing resources, stopping threads, or performing any cleanup tasks to ensure proper termination of the service.

5. How can you handle configuration changes, such as screen rotations, when using a bound service in Android?
    Answer: 
    - Bound services are not affected by configuration changes, such as screen rotations, because they are typically bound to the lifecycle of the binding component (e.g., an Activity). 
    To handle configuration changes, you might use other mechanisms like retained fragments or ViewModel.
    - To implement a long-running background task that survives configuration changes, you can use a combination of a foreground service, retained fragments, or ViewModel to retain the task’s state during configuration changes.

Part 2: Android Quiz
--
1. How do i ensure that a music player continues playing music even when I move away from the app?
    Answer:
    By use of Foreground Services. You will need to declare the foreground service and request permission on android manifest.
    Foreground services allow you to run long running tasks in the foreground which are noticeable to the user.
    Thee service will continue to run even when user navigate away from the app to another app.

2. How to create app Flavors?
    Answer:
   - App Flavors can be created using the Product Flavor and flavor dimensions.In your gradle file, 
     You can productFlavors with associated attributes such flavor-name, versionNameSuffix, manifestPlaceholder such as appLabel to define the display name of the application, etc.
   - With ProductFlavor, you can create different version of the app from the same codebase, You customize the app to use different themes, show and hide functionality etc
 
3. How to enhance Security of Data in transit and data stored in the app? how to secure you app?
   Answer:
   1. Secure Communication:
      - Use HTTPS for Network Communication - this ensure the network communications are encrypted using HTTPs. 
         A malicious wifi can easily alter the contents of HTTP traffic to make your app behave in an unexpected manner, or worse still, inject ads or exploits into it.
      - SSL Pinning to establish a secure connection and protect against MITM attacks.
   2. Secure Authentication and authorization:
      - Implement secure authentications methods such as 2FA, JWT for session management
      - 
   3. App Integrity Checks:
      - Google play integrity api comes in handy when we want to determine the authenticity of the application. It checks on thing like:
           ` - Device Integrity - is App running on an Android device powered by google play service, exclude rooted devices and simulators.
                    - Rooted devices can alter your code at runtime and change the behaviour of it.
              - Play account details - To check if the app is installed from google play store`
   4. Encrypt data:
      - Encrypt data on Transit using Algorithms such as AES,RSA, ECDH for key exchanges. Manage cryptographic keys using Android keystore
      - Data at rest using encrypted shared pref for key and value data, sqlcipher for room database etc.
   5. Data Validations - validate input to prevent Injection Attacks
   6. Disable Logs on production build - handle exception properly without exposing info on logs
   7. Secure the code through R8/Proguard/Dexguard to avoid reverse engineering.
      - isMinifyEnabled = true 
           ` - enables shrinking which removes unused code and resource, 
            - obfuscation(shorten the names of classes,member which results to reduced dex file size,For example, if R8 detects that the else {} branch for a given if/else statement is never taken, R8 removes the code for the else {} branch
            - code optimization to improve the performance and reduce the size of your app's DEX files`
      - isShrinkResources - enable resource shrinking
   8. Regularly update libraries and dependencies
   9. Perform security testing . static using tools like sonarqube
   10. Protect your Service and content provider with Permission - exported = false

4. How can I periodically sync data in my app?
   - By using WorkManager's PeriodicWorkRequest where it will fetch data from remote server periodically using the defined repeat interval.

5. How to handle exception in app?
   - try and catch blocks
   - Avoid Silent Failures - on the catch block do something such as logging to be able to debug
   - Logging Exceptions (Crash Reporting) - Use Firebase Crashlytics to send crash reports eg: FirebaseCrashlytics.getInstance().recordException(e)
   - Uses of Sealed Classes for Error Handling
   - Graceful Error Handling with Coroutines using runningCatch{}
   - Null Safety and the Elvis Operator

6. Ways to prevent ANR?  ref: https://developer.android.com/topic/performance/anrs/diagnose-and-fix-anrs
    Answer:
    ANR occur in Android when an app’s main thread is blocked for too long, typically more than 5 seconds, causing the app to become unresponsive.
    - Keep the main thread unblocked at all times - Don't perform blocking or long operations on the app's main thread.
    - Use Asynchronous processing to handle tasks without blocking the main thread
    - Minimize Lock Contention - this occurs when multiple thread compete for the same resources,causing delays. reduce the use of synchronized blocks
    - Optimize database queries and execute them in background
    - Optimize UI Rendering, example avoid heavy operations in onDraw,
    - Handle broadcasts or running services efficiently, eg registerReceiver() to run broadcast receivers in a non-main thread,

    Ways to Fix ANR
    - Analyze the ANR trace that android generates when ANR occurs, this file contains all the threads in the app. - use workmanager, coroutines to handle background tasks
    - Identify the blocking operations such as Disk I/O,network(https requests) operations,database queries, Complex computations and move them off the main thread
    - Use Diagnostic tools such as Android vitals to alerts you via Play console when app is exhibiting excessive ANRs,
      Strict mode to find ways you might be causing ANRs during integrations, Android Profiler

7. How to implement feature flags in my app?
    Answer: 
    - A feature flag is a simple boolean variable with which we can remotely enable or disable some functionality in the app without having to rebuild and re-release the app.
    - This can be implemented by using Firebase remote config or other android sdk libraries such as PostHog,FlagSmith
    - Clean up old flags: Once a feature is fully rolled out, remove the corresponding flags from your code and Firebase.

8. How to reducing App size?
    Answer:
    - Use tools like Proguard or R8 for Code Shrinking and Obfuscation - to reduce the app size by removing unused code and obfuscating the remaining code.
         setting shrinkResources to true or Use tools line Lint(to detect redundant code) remove unused resources such as drawables and layout files
    - Using AAPT2(Android Asset Packaging Tool 2) - to reduce the size of resources by eliminating redundancy. set it on gradle.properties as: android.enableAapt2=true
    - Use Android App Bundle(AAB) Instead of APK - only the necessary resources are delivered to the user’s device
    - Use Vector drawable instead of PNGs, compress the PNG and JPEG images and reuse resources
    - Optimize Kotlin code - Avoid using reflections as can add a lot of overhead, use inline functions
    - Use dynamic App Delivery
    - Split APKs by ABI - create separate apk for different CPU architectures to reduce the size of each apk

9. What is the role of broadcast receiver? : Ref: https://medium.com/@khush.panchal123/understanding-broadcast-receivers-in-android-044fbfaa1330
    Answer: https://www.linkedin.com/pulse/what-broadcast-receivers-android-full-guide-tebgc/
    - To allow us to receive broadcast messages from other apps or the system itself. 
      Apps can respond to system-wide events like changes in battery level, network connectivity, and incoming SMS messages by using Broadcast Receivers.
    - The broadcast message is nothing but an Intent. 
      The action string of this Intent identifies the event that occurred (e.g, android.intent.action.AIRPLANE_MODE indicates that Airplane mode is toggled)
    - RECEIVER_EXPORTED flag needs to be add for custom broadcast, it indicates that other apps can send the broadcast to our app
10. What is A/B testing?
    Answer:
    - allows developers to test different variations of their app's UI and features to see which ones perform better with users.
    - As users interact with the app, we collect data on their behavior. This might include metrics like click-through rate, conversion rate or time spent on the page.
    - 
11. How to prevent reverse engineering your code?
    Answer:
    - Disable app backups in the Android settings to prevent attackers from extracting your app’s data when performing device backups
    - Use Proguard/DexGuard/R8 to obfuscate the code
    - Play Integrity API
    - Encryption

Part 3: Kotlin Quiz
--
1. Activity Lifecycle and how it differs from fragment?
2. Component of RoomDB?
3. How Kotlin handle null or null safety in kotlin?
4. Kotlin advanced concept, seal class, scope func?
5. Advantages of Kotlin over Java?
6. LiveData and MVVM?










31: An Android application keeps crashing. How do you resolve the issue?
When an application crashes often, these are the best ways to fix it -

It could be a memory space issue. Make sure there’s enough memory space.
Clear the app data by clearing the cache memory using “settings” under Application Manager.
Not all apps run the same on assorted machines, so you may have to tinker with memory management.
It may be a matter of compatibility; a problem that can be headed off by testing the app on as many of your devices as possible beforehand.