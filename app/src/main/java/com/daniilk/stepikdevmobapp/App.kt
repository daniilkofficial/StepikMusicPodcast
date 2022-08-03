package com.daniilk.stepikdevmobapp

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val realmBuilder = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
        val realmConfiguration = realmBuilder.build()
        Realm.setDefaultConfiguration(realmConfiguration)

//        getSharedPreferences("name", 0)
//            .edit()
//            .putString("zzz", "xx")
//            .apply()
//
//        getSharedPreferences("name", 0)
//            .getString("zzz", "")

    }
}