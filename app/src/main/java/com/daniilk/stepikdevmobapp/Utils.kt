package com.daniilk.stepikdevmobapp

import io.reactivex.Observable
import java.net.HttpURLConnection
import java.net.URL

fun createRequest(url: String): Observable<String> = Observable.create<String> {
    val urlConnection = URL(url).openConnection() as HttpURLConnection
    try {
        urlConnection.connect() // само обращение в сеть

        if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) // проверка результата, что он 200
            it.onError(RuntimeException(urlConnection.responseMessage))
        else {
            val str = urlConnection.inputStream.bufferedReader()
                .readText() // читаем urlConnection как текст
            it.onNext(str)
        }
    } finally {
        urlConnection.disconnect()
    }
}