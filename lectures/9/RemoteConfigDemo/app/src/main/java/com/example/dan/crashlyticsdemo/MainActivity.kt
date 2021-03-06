package com.example.dan.crashlyticsdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  private lateinit var remoteConfig: FirebaseRemoteConfig

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseCrashlytics.getInstance().log("app started")

    remoteConfig = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
      minimumFetchIntervalInSeconds = 60
    }
    remoteConfig.setConfigSettingsAsync(configSettings)

    remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

    setContentView(R.layout.activity_main)
    crashButton.setOnClickListener {
      if (remoteConfig.getString(HIDE_CRASH_LOGIC) ==
        remoteConfig.getString(HIDE_CRASH_LOGIC_VALUE)
      ) {
        throw RuntimeException("Oppps!")
      } else {
        Snackbar.make(
          parent_content,
          remoteConfig.getString(HIDE_CRASH_NO_LOGIC),
          Snackbar.LENGTH_LONG
        ).show()
      }
    }
    fetchValues()
  }

  private fun fetchValues() {
    textView.text = remoteConfig.getString(HELLO_MESSAGE)

    remoteConfig.fetchAndActivate()
      .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          val updated = task.result
          Log.d(TAG, "Config params updated: $updated")
          Toast.makeText(
            this, "Fetch and activate succeeded",
            Toast.LENGTH_SHORT
          ).show()
        } else {
          Toast.makeText(
            this, "Fetch failed",
            Toast.LENGTH_SHORT
          ).show()
        }
        displayWelcomeMessage()
      }
  }

  private fun displayWelcomeMessage() {
    textView.text = remoteConfig.getString(HELLO_MESSAGE)
  }

  companion object {
    private const val TAG = "Main"
    private const val HIDE_CRASH_LOGIC = "hide_crash_logic"
    private const val HIDE_CRASH_LOGIC_VALUE = "hide_crash_value"
    private const val HIDE_CRASH_NO_LOGIC = "hide_crash_nologic"
    private const val HELLO_MESSAGE = "hello_message"
  }
}
