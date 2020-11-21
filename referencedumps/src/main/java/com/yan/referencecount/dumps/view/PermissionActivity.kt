package com.yan.referencecount.dumps.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.yan.referencecount.dumps.ReferenceMgr

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/21
 */
class PermissionActivity : FragmentActivity() {
    private val permission = arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, 2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        logicPermission()

    }

    private fun logicPermission() {
        if (WindowPop.floatingPermissionOk(this)) {
            ReferenceMgr.attachDumpView(application)
        } else if (shouldShowRequestPermissionRationale(permission[0])) {
            requestPermissions(permission, 2)
        } else {
            // 浮窗权限
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            logicPermission()
        }
    }

}