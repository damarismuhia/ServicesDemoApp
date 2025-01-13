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
    
12. How to reduce build time of an android application?

    Answer:
    - Increase Heap Size
    - Use the latest versions of the tools and plugins you use in your project
    - Ensure that dynamic dependency is not used - as it will make gradle to go online and check for the latest version everytime it builds the app.
    - Use Daemon — org.gradle.daemon=true - Daemon keeps the instance of the gradle up and running in the background even after your build finishes. 
      This will remove the time required to initialize the gradle and decrease your build timing significantly.
    - Enable parallel execution - If you’re working on a multi-module project, then forcing Gradle to execute tasks in parallel is also an easy performance gain. 
      This works with the caveat that your tasks in different modules should be independent, and not access shared state, which you shouldn’t be doing anyway.

13. How to plug memory Leak in Android? https://proandroiddev.com/everything-you-need-to-know-about-memory-leaks-in-android-d7a59faaf46a
    Answer: A memory leak occurs when an app fails to release memory that it no longer needs or 
            when it unintentionally retains references to objects, preventing the Garbage Collector from reclaiming its memory. 
    
    Causes of Memory leak:
    1. Keeping references to Activities and Fragments - Activities and Fragments can be prone to memory leaks when references to 
       them are held longer than necessary by Long-lived object such as (background tasks, singleton and static variables)
       For instance, if a background task maintains a reference to an Activity that has been closed, 
       the Activity’s resources may not be properly released.
    
       - Fix: Use WeakReference to hold a reference to the Activity to allow it to be garbage collected when need.
    
    2. Mishandling Context - Storing a reference to an Activity's context in a long-lived object can prevent the 
       Activity from being garbage collected.
    
        - Fix: Use application context with WeakReference instead of Activity context
                ` Example: class SingleTan() {
                    private var contextRef: WeakReference<Context>? = null
                    fun init(con: Context) {
                    contextRef = WeakReference(con.applicationContext)
                  }
                }`
                        
    3. Mishandling of AsyncTask and Thread - Not canceling or cleaning up threads and AsyncTasks when associated Activity or Fragment is destroyed
       - Fix: Always cancel them in onDestroy() or onStop()
    4. Unregistered Listeners and Callbacks: - failing to unregister them when they are no longer needed can lead to memory leaks 
       - unregister receiver - If you don’t unregister the broadcast receiver, then it still holds a reference to the activity, 
         even if you close the activity.
         `NB: If the broadcast Receiver is registered in onCreate(), then when the app goes into the background and resumed again, 
         the receiver will not be registered again. So it is always good to register the broadcastReceiver in onStart() or onResume() of the activity and unregister in onStop().`
    5. Release unused resources - When your Android app uses a resource, be sure to release the resource(on onDestroyView) when it is no longer needed. 
      If you don't, the resource continues to take up memory even after your application finishes with them.

14. How to support different screen sizes?

    Answer:
    - Create a flexible layout — The best way to create a responsive layout for different screen sizes is to use ConstraintLayout as the base layout in your UI.
    - Use vector graphics: Use vector graphics for scalability
    - Avoid hard-coded layout sizes - Use wrap_content or match_parent.

15. Why is it recommended to use only the default constructor to create a Fragment?
    Answer:
    - Whenever the Android Framework decides to recreate our Fragment for example in case of orientation changes. Android calls the no-argument constructor of our Fragment.
    - when the system restores a fragment it will automatically restore your bundle. This way you are guaranteed to restore the state of the fragment correctly to the same state the fragment was initialised with.

16. How to optimize android app for performance?
    1. Leverage Kotlin's features such as inline class this allow you to create a type without runtime overhead and data class which automatically generates boilerplate code.
    2. Use memory efficiently(Cautious of memory leak) - Inefficient memory usage can lead to performance issues and app crashes.
        - use the onTrimMemory to release resources when the app is in the background/device is on low memoer
        - use the android:largeheap  to request larger heap size for ur app
        - use the SparseArray instead of hashmap to reduce memory usage
    3. Implement ViewBinding - this reduces the overhead of findViewById()
    4. Optimize Layouts with ConstraintLayout - which offers a flatter view hierarchy, 
       use merge tag to help eliminate redundant view groups in your view hierarchy when including one layout within another,Using the ViewStub class to defer the loading of complex views until they are needed.
    5. Utilize Coroutines - Lightweight and efficient
    6. Embrace Null Safety - Null safety prevent runtime crashes
    7. Use scalable image types


Part 3
-
1. What is an Activity?
    Answer:
    - An Activity represents a single screen with a user interface. It acts as the entry point for interacting with the app and
      serves as a container for views. Each activity is typically associated with a layout file that defines the UI components.
2. Scenario in which only onDestroy is called for an activity without onPause() and onStop()?
    Answer:
    If finish() is called in the OnCreate() method of an activity, the system will invoke onDestroy() method directly
3. Configuration changes on device rotation:
   - A ViewModel is LifeCycle-Aware. I.e a ViewModel will not be destroyed if its owner is destroyed for a configuration change (e.g. rotation) - caches state and persists it through configuration changes.
    The new instance of the owner will just re-connected to the existing ViewModel. So if you rotate an Activity three times, you have just created three different Activity instances, but you only have one ViewModel.
   - onRestoreInstanceState - Restores the fragment’s state after it has been recreated., Retrieve the saved data and restore the fragment’s UI elements or state.
   - onSaveInstanceState() - Saves the fragment’s state(scroll positions) before it might be destroyed due to configuration changes (e.g., screen rotation, device reboots).

4. Mention two ways to clear the back stack of Activities when a new Activity is called using intent.
    Answer:
    The first approach is to use a FLAG_ACTIVITY_CLEAR_TOP flag. 
    The second way is by using FLAG_ACTIVITY_CLEAR_TASK and FLAG_ACTIVITY_NEW_TASK in conjunction.

5. What is a Fragment, and how does it differ from an Activity?
    Answer:
    - Fragment is a UI entity attached to Activity.Activity can have multiple fragments attached to it. 
      Fragment must be attached to an activity and its lifecycle will depend on its host activity.

6. Difference between margin & padding?
   - Padding will be space added inside the container, for instance, if it is a button, padding will be added inside the button. Margin will be space added outside the container




Part 3: Android Tools and Technologies
--
1. What is ADB?
    Answer:
    - This is a command line tools that allow us to communicate with a device, it allow us to install and debug our app.
      - It consists of components such as 
        - client - which sends commands. The client runs on your development machine, 
        - daemon which run the command 
        - server which manages communication between the client and the daemon. The server runs as a background process on your development machine.

2. What is StrictMode?
   - It helps detects things you might be doing by accident and brings them to your attention so you can fix them.
   - It is commonly used to catch accidental disk or network access on the application’s main thread
     - Example: `StrictMode.setVmPolicy(VmPolicy.Builder()
                   .detectCleartextNetwork()
                   .detectActivityLeaks()
                   .detectLeakedRegistrationObjects()
                   .detectLeakedSqlLiteObjects()
                   .penaltyLog()
                   .penaltyDeath()
                   .build())`
  
3. What is Kotlin DSL for Gradle?
   - Kotlin DSL (Domain-Specific Language) is an alternative to the traditional Groovy for configuring Gradle build scripts
   - With Kotlin DSL, you can write your Gradle build configurations using the Kotlin programming language instead of Groovy.
   - It provides:
     • Type Safety: It provides compile-time type checking, reducing runtime errors in your build scripts.
     • Better IDE support with auto-completion, and improved navigation to source code
   
4. What is Gradle?
   - Gradle is a build tool that we use for Android development to automate the process of building and publishing apps.
   - Gradle builds run in three phases.
     - Initialization - to determine which projects and subprojects are included in the build, and sets up classpaths containing your build files and applied plugins. 
       This phase focuses on a settings file where you declare projects to build and the locations from which to fetch plugins and libraries.

5. How do you create a custom task in Gradle?
- 






 When should you use a Fragment, rather than an Activity
 What is the difference between Serializable and Parcelable?
 











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

DESIGN: https://medium.com/@prabhat.rai1707/android-system-design-interview-google-uber-29dedbbe9fda