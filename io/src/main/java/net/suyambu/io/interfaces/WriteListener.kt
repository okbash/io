package net.suyambu.io.interfaces

import android.net.Uri

interface WriteListener {
    fun onProgress(progress: Int)
    fun onComplete(uri: Uri?)
    fun onError(errorCode: Int)
}