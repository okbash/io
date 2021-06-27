package net.suyambu.io

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import net.suyambu.io.interfaces.WriteListener
import java.io.*

class FileIO(private val context: Context) {
    companion object {
        const val ERROR_NO_STREAM = 1
        const val ERROR_UNABLE_WRITE = 2
    }

    private fun getMimeType(url: String): String? {
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        if (ext != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        }
        return null
    }

    fun write(fileName: String, fileSize: Int, inputStream: InputStream? = null, listener: WriteListener? = null) {
        val contentResolver = context.contentResolver
        var outputStream: OutputStream? = null
        var fileSizeWritten: Long = 0
        if (inputStream == null) {
            listener?.onError(ERROR_NO_STREAM)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(fileName))
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
            }

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            try {
                var pfd: ParcelFileDescriptor?=null
                try {
                    pfd =
                        uri?.let { contentResolver.openFileDescriptor(it, "w")}!!
                    val buf = ByteArray(4 * 1024)
                    outputStream = FileOutputStream(pfd.fileDescriptor)
                    var len: Int

                    listener?.onProgress(0)

                    while (true) {
                        val read = inputStream.read(buf)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(buf, 0, read)
                        fileSizeWritten += read.toLong()
                        listener?.onProgress((fileSizeWritten / fileSize * 100).toInt())
                    }

                    outputStream.flush()
                } catch (e: Exception) {
                    listener?.onError(ERROR_UNABLE_WRITE)
                } finally {
                    inputStream.close()
                    outputStream?.close()
                    pfd?.close()
                }
                values.clear()
                values.put(MediaStore.Video.Media.IS_PENDING, 0)
                if (uri != null) {
                    context.contentResolver.update(uri, values, null, null)
                    outputStream = context.contentResolver.openOutputStream(uri)
                }
                if (outputStream == null) {
                    listener?.onError(ERROR_UNABLE_WRITE)
                }

            } catch (e: IOException) {
                if (uri != null) {
                    context.contentResolver.delete(uri, null, null)
                }
                listener?.onError(ERROR_UNABLE_WRITE)
            } finally {
                if (outputStream != null) {
                    outputStream.close()
                    listener?.onComplete(uri)
                }
            }

        } else {

            /* Legacy write */

            val directoryPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            val file = File(directoryPath)
            if (!file.exists()) {
                file.mkdirs()
            }
            val filePath = File(directoryPath, fileName)

            try {
                val fileReader = ByteArray(4 *1024)
                outputStream = FileOutputStream(filePath)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeWritten += read.toLong()
                    listener?.onProgress((fileSizeWritten / fileSize * 100).toInt())
                }
                outputStream.flush()
                listener?.onComplete(Uri.fromFile(filePath))
            } catch (e: IOException) {
                listener?.onError(ERROR_UNABLE_WRITE)
            } finally {
                inputStream.close()
                outputStream?.close()
            }

        }
    }
}