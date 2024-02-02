package com.cwtdemo.cameralauncher

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cwtdemo.cameralauncher.ui.theme.CameraLauncherTheme
import java.io.File
import java.util.Objects

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraLauncherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestScreen()
                }
            }
        }
    }
}

val TAG = "CameraLauncher"

@Composable
fun TestScreen() {
    val context = LocalContext.current
    val file = File.createTempFile(
        "some_photo", /* prefix */
        ".jpg", /* suffix */
        context.externalCacheDir      /* directory */
    )

    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider",
        file
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            Log.d(TAG, "cameraLauncher result is success: $success")
            Toast.makeText(context, "CameraLauncher result is success: $success", Toast.LENGTH_SHORT).show()
            if (success) {
                // use uri to do something with camera result
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "NEED PERMISSION", Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Button(
            onClick = {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "calling camera launcher")
                    cameraLauncher.launch(uri)
                } else {
                    Log.d(TAG, "calling permission launcher")
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
        ) {
            Text(text = "TAKE PHOTO")
        }
    }
}