package uk.co.barbuzz.devicestats

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

object Utils {
    const val MIME_TYPE_EMAIL = "message/rfc822"

    fun newEmailIntent(context: Context?,
                       address: String, subject: String?, body: String?,
                       useEmailMime: Boolean): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        if (useEmailMime) {
            intent.type = MIME_TYPE_EMAIL
        }
        return intent
    }

    fun getAppVersion(ctx: Context): String {
        var versionName: String? = null
        var versionCode = -1
        try {
            val pInfo = ctx.packageManager.getPackageInfo(
                    ctx.packageName, 0)
            versionName = pInfo.versionName
            versionCode = pInfo.versionCode
        } catch (ex: PackageManager.NameNotFoundException) {
            versionName = null
        }
        return ctx.getString(R.string.about_version, versionName, versionCode)
    }

    @Throws(Exception::class)
    fun dumpDataToSD(filename: String?, results: String?) {
        val log = File(Environment.getExternalStorageDirectory(), filename)
        if (log.exists()) {
            log.delete()
        }
        val out = BufferedWriter(FileWriter(
                log.absolutePath, log.exists()))
        out.write(results)
        out.close()
    }
}