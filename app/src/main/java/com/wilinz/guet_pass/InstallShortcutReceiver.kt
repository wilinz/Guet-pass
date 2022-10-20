package com.wilinz.guet_pass

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class InstallShortcutReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "InstallShortcutReceiver"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d(TAG, p1?.action.toString())
    }

}