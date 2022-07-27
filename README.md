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

### Gradle Dependency
* Add it in your root build.gradle at the end of repositories:

	```
	allprojects {
	    repositories {
		...
		maven { url 'https://jitpack.io' }
	    }
	}
	```

* Add the dependency in your app's build.gradle file

	```
	dependencies {
		implementation 'com.github.SimformSolutionsPvtLtd:SSffmpegVideoOperation:1.0.8'
	}
	```

This is all you have to do to load the FFmpeg library.

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
