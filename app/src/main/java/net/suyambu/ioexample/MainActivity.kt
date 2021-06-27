package net.suyambu.ioexample

import android.Manifest
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import net.suyambu.io.FileIO
import net.suyambu.io.interfaces.WriteListener
import sh.fearless.hiper.Hiper
import sh.fearless.util.Permission
import sh.fearless.util.debug
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var permission: Permission
    private lateinit var fileIO: FileIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permission = Permission(this)
        val hiper = Hiper.getInstance().async()
        fileIO = FileIO(this)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            debug("here")
//            if (Settings.System.canWrite(this)) {
//                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:${packageName}")).let {
//                        startActivityForResult(it, 21)
//                    }
//                }
//            }
//        }

        permission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, message = null, listener = object : Permission.Listener {
            override fun onGranted() {
                hiper.get("https://fsa.zobj.net/download/bvWtDTcRGJ_aMR3res5U2Uu7o3NXMNG0p8AaYI6kmcp8tjKvt8sGfnW5qcyaEg191-fUXiIXDF0kv4vKT66f1dehaaWj7ofgVfcyuzOPxX4POp2k00konx1mBVNE/?a=&c=72&f=astronaut.mp3&special=1624799782-NL4Lu68NINEUoxt1aAV34TXKJyTvDw1WtRLJKkmVq5Q%3D", isStream = true).resolve {
                    writeFile(it.stream, it.headers["content-length"]!!.toInt())
                }.reject {

                }.catch {

                }.execute()
            }
            override fun onRejected() {}
        })
    }

    fun writeFile(stream: InputStream?, fileSize: Int) {
        fileIO.write("hello.mp3", fileSize, stream, object : WriteListener {
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permission.execute(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}