<h1 align="center">Input/Output (IO) Library for Android</h1>

<p align="center">
    <a href="https://jitpack.io/#okbash/io"><img src="https://img.shields.io/jitpack/v/github/okbash/io?style=for-the-badge" alt="Release"></a>
    <a href="https://travis-ci.com/okbash/io"><img src="https://img.shields.io/travis/com/okbash/io/master?style=for-the-badge" alt="Build Status"></a>
    <a href="https://github.com/okbash/io/blob/master/LICENSE.txt"><img src="https://img.shields.io/github/license/okbash/io.svg?style=for-the-badge" alt="License"></a>
<!--     <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/okbash/io?logo=GitHub&style=for-the-badge"> -->
    <img alt="GitHub repo size" src="https://img.shields.io/github/repo-size/okbash/io?logo=GitHub&style=for-the-badge">
    <a href="https://github.com/okbash/io/issues"><img alt="GitHub open issues" src="https://img.shields.io/github/issues/okbash/io?style=for-the-badge"></a>
</p>


### Getting Started

Add it in your root build.gradle at the end of repositories

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency

```gradle
implementation "com.github.okbash:io:$io_version"
```

### Usage

**FileIO**

```kotlin
val io = FileIO()
```

Usage of **FileIO**


```kotlin
io.write("hello.mp3", fileSize, stream, object : WriteListener {
    override fun onProgress(progress: Int) {
        debug("Progress:", progress)
    }

    override fun onComplete(uri: Uri?) {
        RingtoneManager.setActualDefaultRingtoneUri(this@MainActivity, RingtoneManager.TYPE_RINGTONE, uri)
        debug("Completed:", uri)
    }

    override fun onError(errorCode: Int) {
        debug("Error:", errorCode)
    }
})
```

