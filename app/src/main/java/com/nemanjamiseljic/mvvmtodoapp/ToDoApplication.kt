package com.nemanjamiseljic.mvvmtodoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ToDoApplication: Application() { /**Class needed to activate Dagger hilt **/
}