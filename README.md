# FFMPEG video operations

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.4.21-blue.svg)](https://kotlinlang.org)  [![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat)](https://www.android.com/) [![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)


FFMpeg compiled for Android.
Execute FFmpeg commands with ease in your Android app.

Getting Started
------------------------
This project is provide inbuild ffmpeg operation queries:

<table>
  <tr>
    <td><img src="https://user-images.githubusercontent.com/16113993/105948498-0aa45b80-6091-11eb-9713-0088376742de.png" width=270 height=480></td>
    <td><img src="https://user-images.githubusercontent.com/16113993/105948415-e9dc0600-6090-11eb-9db9-a3778eaaa438.png" width=270 height=480></td>
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
#### Other extra operation ffmpeg queries like
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
- Uses native CPU capabilities on ARM architectures
- FFprobe is bundled in this library
- Enabled network capabilities
- Multithreading
- Supports zlib and MediaCodec system libraries
- Camera access on supported devices
- Builds shared native libraries (.so)
- Creates Android archive with .aar extension
- Supports API Level 16+
### Support target sdk
- 28,29 and 30 (all)

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
		implementation ''
	}
	```

This is all you have to do to load the FFmpeg library.

### Run FFmpeg command
In this sample code we will run the ffmpeg -version command in background call.
```java
  val query:Array<String> = "-i, input,....,...., outout"
        CallBackOfQuery.callQuery(this, query, object : FFmpegCallBack {
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



#### Inbuild query example
```java
val startTimeString = "00:01:00" (HH:MM:SS)
val endTimeString = "00:02:00" (HH:MM:SS)
val query:Array<String> = FFmpegQueryExtension.cutVideo(inputPath, startTimeString, endTimeString, outputPath)
CallBackOfQuery.callQuery(this, query, object : FFmpegCallBack {
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

## Special Thanks To
- [Simform Solutions](https://www.simform.com/)

## Created By
- [Ashwin Vavaliya](https://github.com/Nirashu)

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