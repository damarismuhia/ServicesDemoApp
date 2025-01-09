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
2. How to create app Flavors?
3. How to enhance Security of Data in transit and data stored in the app? how to secure you app?
4. How can I periodically sync data in my app?
5. How to handle exception in app?
6. Ways to prevent ANR?
7. How to implement feature flags in my app?
8. How to reducing App size?
9. What is the role of broadcast receiver?

Part 3: Kotlin Quiz
--
1. Activity Lifecycle and how it differs from fragment?
2. Component of RoomDB?
3. How Kotlin handle null or null safety in kotlin?
4. Kotlin advanced concept, seal class, scope func?
5. Advantages of Kotlin over Java?
6. LiveData and MVVM?







28: Give some examples of Android exceptions.
Exceptions include:

Inflate Exception
Surface.OutOfResourceException
SurfaceHolder.BadSurfaceTypeException
WindowManager.BadTokenException





31: An Android application keeps crashing. How do you resolve the issue?
When an application crashes often, these are the best ways to fix it -

It could be a memory space issue. Make sure there’s enough memory space.
Clear the app data by clearing the cache memory using “settings” under Application Manager.
Not all apps run the same on assorted machines, so you may have to tinker with memory management.
It may be a matter of compatibility; a problem that can be headed off by testing the app on as many of your devices as possible beforehand.