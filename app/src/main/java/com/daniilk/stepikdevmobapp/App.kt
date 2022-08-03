package com.daniilk.stepikdevmobapp

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Realm инициализация, чтение в UI потоке, миграция и конфигурация
        Realm.init(this)
        val realmBuilder = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded() // мигрирование изменний БД
        val realmConfiguration = realmBuilder.build()
        Realm.setDefaultConfiguration(realmConfiguration)

    }
}