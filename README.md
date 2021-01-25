# FFMPEG video operations

![Screenshot_1611307416](https://user-images.githubusercontent.com/16113993/105489978-1c28e480-5cda-11eb-9b68-e6f2f5399868.png)

FFMpeg compiled for Android.
Execute FFmpeg commands with ease in your Android app.

## About
This project is provide inbuild ffmpeg operation queries:
##### Video operation ffmpeg queries like
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
##### Other extra operation ffmpeg queries like
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
###### Support target sdk
- 28,29 and 30 (all)

### Dependency
- [MobileFFmpeg](https://github.com/tanersener/mobile-ffmpeg)

### Getting Started
Include the dependency
```gradle
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
            override fun process(logMessage: LogMessage) {
                logMessage.text
            }

            override fun success() {
            }

            override fun cancel() {
            }

            override fun failed() {
            }
        })
```

_NOTE: This will result in `failed` being called instead of `success`._

#### Inbuild query example
```java
val startTimeString = "00:01:00" (HH:MM:SS)
val endTimeString = "00:02:00" (HH:MM:SS)
val query:Array<String> = FFmpegQueryExtension.cutVideo(inputPath, startTimeString, endTimeString, outputPath)
CallBackOfQuery.callQuery(this, query, object : FFmpegCallBack {
            override fun process(logMessage: LogMessage) {
                logMessage.text
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
