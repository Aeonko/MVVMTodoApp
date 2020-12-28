package com.nemanjamiseljic.mvvmtodoapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val TAG = "PreferencesManager"
enum class SortOrder{
    BY_NAME,
    BY_DATE
}

data class  FilterPreferences(val sortOrder: SortOrder,val hideCompleted: Boolean)
/**
 * Saves currently read data from DataStore inside this class and transfers it back to UI to be read
 * Letter used as means to transfer data to database and what is requested for query**/



class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data
            .catch {exception->
                /**Catching exception when something went's wrong**/
                if(exception is IOException){
                    Log.e(TAG, "Error reading preferences: ",exception )
                    emit(emptyPreferences()) /**Emits default functions**/
                }else{
                    throw  exception /**If it it some other exception then IO it throws exception and app crashes**/
                }
            }
            .map { preferences->

                /**Gets values from DataStore and gets name of that value (SortOrder enum as string)**/
                /**Enum is saved in data Store as String that is why we are reading it as String again**/
                val sortOrder = SortOrder.valueOf(
                        preferences[PreferencesKeys.SORT_ORDER]?: SortOrder.BY_DATE.name
                )


                /**Gets hide completed from Data Store**/
                val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED]?: false

                FilterPreferences(sortOrder,hideCompleted)
            }

    suspend fun updateSortOrder(sortOrder:SortOrder){
        /**Writes data to DataStore**/
        dataStore.edit { preferences->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean){
        /**Writes data to DataStore**/
        dataStore.edit { preferences->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }
    private object PreferencesKeys{
        /**Data Store keys used to write or read values**/
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}