# FFMPEG video operations

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.7.0-blue.svg)](https://kotlinlang.org) 
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat)](https://www.android.com/) 
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19) [![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-SSffmpegVideoOperation-green.svg?style=flat )]( https://android-arsenal.com/details/1/8250 )


FFmpeg compiled for Android.
Execute FFmpeg commands with ease in your Android app.

Getting Started
------------------------
This project is provide in-build FFmpeg operation queries:

<table>
  <tr>
    <td><img src="https://user-images.githubusercontent.com/16113993/111145681-86f5ee00-85ae-11eb-9057-c54955819459.png" width=270 height=480></td>
    <td><img src="https://user-images.githubusercontent.com/16113993/111145695-8a897500-85ae-11eb-9c92-625865c0bfd4.png" width=270 height=480></td>
    <td><img src="https://user-images.githubusercontent.com/16113993/111145578-6cbc1000-85ae-11eb-90a6-3550842db092.gif" width=270 height=480></td>
  </tr>
</table>

#### Video operation ffmpeg queries like
- Cut video using time
- Convert image to video
- Add water mark on video
- Add text on video
- Combine image image and video
- Combine images
- Combine videos
- Compress a video
- Extract frames from video
- Fast/Slow motion video
- Reverse video
- video fade in / fade out
- Compress video to GIF
- Rotate and Flip video (Mirroring)
- Remove audio from video
- Update aspect ratio of video
#### Other extra operation FFmpeg queries like
- Merge GIFs
- Merge Audios
- Update audio volume
- Fast/Slow audio
- Crop audio using time
- Compress Audio

### Architectures
FFmpeg Android runs on the following architectures:
- arm-v7a, arm-v7a-neon, arm64-v8a, x86 and x86_64

### Features
- Enabled network capabilities
- Multi-threading
- Supports zlib and Media-codec system libraries
- Camera access on supported devices
- Supports API Level 24+

### Support target sdk
- 30

### Dependency
- [MobileFFmpeg](https://github.com/tanersener/mobile-ffmpeg)

**Note:** This library includes the mobile-ffmpeg.aar file (full version) in the `SSffmpegVideoOperation/libs/` directory. The full version includes all FFmpeg features and codecs needed for comprehensive video operations.

**For JitPack users:** The mobile-ffmpeg dependency is automatically included when you use the JitPack dependency. No additional setup required!

### Integration Guide

This library uses the mobile-ffmpeg AAR dependency which is included in the library's libs folder. Follow these simple steps to integrate the library into your project.

#### Step 1: Download the Library
* Download or clone this repository
* Copy the `SSffmpegVideoOperation` module folder to your project

#### Step 2: Integration Methods

**Option A: Using JitPack (Recommended - Simple One-Line Integration)**

This is the easiest way to integrate the library. JitPack will build the library along with the mobile-ffmpeg dependency automatically.

* Add JitPack repository to your root build.gradle:

	```gradle
	allprojects {
	    repositories {
		google()
		mavenCentral()
		maven { url 'https://jitpack.io' }
	    }
	}
	```

* Add the dependency in your app's build.gradle file:

	```gradle
	dependencies {
		implementation 'com.github.SimformSolutionsPvtLtd:SSffmpegVideoOperation:1.0.8'
	}
	```

**That's it!** JitPack will automatically handle the mobile-ffmpeg.aar dependency that's included in this repository. No additional configuration needed.

**Option B: Local Integration (For Customization)**

If you want to modify the library or integrate it locally, follow these steps:

1. **Add the library module to your `settings.gradle`:**
   ```gradle
   include ':app', ':SSffmpegVideoOperation'
   // If the SSffmpegVideoOperation folder is in a different location:
   // project(':SSffmpegVideoOperation').projectDir = new File('path/to/SSffmpegVideoOperation')
   ```

2. **Configure repositories in your app's `build.gradle`:**
   ```gradle
   android {
       // ... your existing configuration
   }

   repositories {
       google()
       mavenCentral()
       flatDir {
           dirs '../SSffmpegVideoOperation/libs'
       }
   }

   dependencies {
       implementation project(':SSffmpegVideoOperation')
       // ... your other dependencies
   }
   ```

3. **The SSffmpegVideoOperation module is already configured with:**
   - `libs/mobile-ffmpeg.aar` - The full version of mobile-ffmpeg
   - Proper repository configuration in its `build.gradle`:
     ```gradle
     repositories {
         flatDir {
             dirs 'libs'
         }
     }
     
     dependencies {
         implementation(name: 'mobile-ffmpeg', ext: 'aar')
         // ... other dependencies
     }
     ```

**Important Notes:**
- The `mobile-ffmpeg.aar` file is already included in `SSffmpegVideoOperation/libs/`
- No additional setup is required for the AAR dependency
- The library uses `flatDir` repository to resolve the local AAR file
- Make sure to sync your project after adding the module

## How JitPack Integration Works

When you use the JitPack dependency (`implementation 'com.github.SimformSolutionsPvtLtd:SSffmpegVideoOperation:1.0.8'`):

1. **JitPack automatically builds** your library from the GitHub repository
2. **Includes mobile-ffmpeg.aar** - The AAR file in `SSffmpegVideoOperation/libs/` is packaged with the library
3. **Resolves dependencies** - All transitive dependencies are handled automatically
4. **No local setup needed** - Users don't need to manually handle AAR files or repository configurations

This approach gives you the best of both worlds:
- **Simple integration** for users via JitPack
- **Full control** over the mobile-ffmpeg dependency
- **No external dependencies** on repositories that might change or become unavailable

#### Step 3: Add Required Permissions
Add these permissions to your AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

#### Step 4: ProGuard Configuration (if using ProGuard/R8)
Add these rules to your proguard-rules.pro:
```
-keep class com.arthenica.mobileffmpeg.** { *; }
-keep class com.simform.videooperations.** { *; }
```

This setup ensures proper AAR dependency resolution and avoids the "Direct local .aar file dependencies are not supported" error when building AAR libraries.

### Run FFmpeg command
In this sample code we will run the FFmpeg -version command in background call.
```java
  val query:Array<String> = "-i, input,....,...., outout"
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun statisticsProcess(statistics: Statistics) {
                Log.i("FFMPEG LOG : ", statistics.videoFrameNumber)
            }

            override fun process(logMessage: LogMessage) {
                Log.i("FFMPEG LOG : ", logMessage.text)
            }

            override fun success() {
            }

            override fun cancel() {
            }

            override fun failed() {
            }
        })
```



#### In-build query example
```java
val startTimeString = "00:01:00" (HH:MM:SS)
val endTimeString = "00:02:00" (HH:MM:SS)
val query:Array<String> = FFmpegQueryExtension().cutVideo(inputPath, startTimeString, endTimeString, outputPath)
CallBackOfQuery().callQuery(query, object : FFmpegCallBack {
            override fun statisticsProcess(statistics: Statistics) {
                Log.i("FFMPEG LOG : ", statistics.videoFrameNumber)
            }

            override fun process(logMessage: LogMessage) {
                Log.i("FFMPEG LOG : ", logMessage.text)
            }

            override fun success() {
                //Output = outputPath
            }

            override fun cancel() {
            }

            override fun failed() {
            }
        })
```
same for other queries.
And you can apply your query also

## Troubleshooting

### Common Issues and Solutions

#### 1. "Could not find mobile-ffmpeg.aar" Error
**Solution:** 
- Ensure the `SSffmpegVideoOperation/libs/mobile-ffmpeg.aar` file exists
- Check that your app's build.gradle has the correct flatDir repository configuration:
  ```gradle
  repositories {
      flatDir {
          dirs '../SSffmpegVideoOperation/libs'
      }
  }
  ```

#### 2. "INSTALL_PARSE_FAILED_NO_CERTIFICATES" Error
**Solution:** Ensure your release builds are properly signed. Add signing configuration to your app's build.gradle:
```gradle
android {
    signingConfigs {
        debug {
            storeFile file("${System.getProperty('user.home')}/.android/debug.keystore")
            storePassword "android"
            keyAlias "androiddebugkey"
            keyPassword "android"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.debug  // Use debug signing for testing
            // For production, create and use a proper release keystore
        }
    }
}
```

#### 3. Build Sync Issues
**Solution:** 
- Make sure the SSffmpegVideoOperation module is properly included in settings.gradle
- Verify the module path is correct
- Clean and rebuild the project

#### 4. FFmpeg Commands Not Working
**Solution:** 
- Check if you have the required permissions
- Ensure input and output file paths are correct and accessible
- Verify the FFmpeg command syntax

#### 5. Memory Issues with Large Videos
**Solution:**
- Process videos in smaller chunks
- Use appropriate compression settings
- Consider using background processing for large files

#### 6. "flatDir should be avoided" Warning
**Solution:** This warning can be ignored. While flatDir is not the recommended approach for published libraries, it's acceptable for local AAR dependencies and works reliably for this use case.

## Medium Blog
For more info go to __[Multimedia Operations for Android using FFmpeg](https://medium.com/simform-engineering/multimedia-operations-for-android-using-ffmpeg-78f1fb480a83)__

## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/SimformSolutionsPvtLtd/ffmpeg_video_operation/stargazers)__ for this repository. :star:

## Awesome Mobile Libraries
- Check out our other available [awesome mobile libraries](https://github.com/SimformSolutionsPvtLtd/Awesome-Mobile-Libraries)

## License

```
Copyright 2021 Simform Solutions

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
