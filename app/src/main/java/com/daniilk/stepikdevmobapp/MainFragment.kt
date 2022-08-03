package com.daniilk.stepikdevmobapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList

class MainFragment : Fragment(), LifecycleObserver {
    private lateinit var recyclerView: RecyclerView
    private var request: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val o =
            createRequest("https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.twit.tv%2Fbrickhouse.xml")
                .map { Gson().fromJson(it, FeedApi::class.java) }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        request = o.subscribe({
            val feed = Feed(
                it.items.mapTo(
                    RealmList<FeedItem>()
                ) { feed ->
                    FeedItem(
                        feed.title,
                        feed.link,
                        feed.thumbnail,
                        feed.description,
                        feed.guid
                    )
                }
            )
            Realm.getDefaultInstance().executeTransaction { realm ->
                val oldList = realm.where(Feed::class.java).findAll()
                if (oldList.size > 0)
                    for (item in oldList)
                        item.deleteFromRealm()

                realm.copyToRealm(feed)
            }
            showRecyclerView()

        }, {
            showRecyclerView()
            Log.e(MainActivity.TAG, "onCreate: $it")
        })

    }

    private fun showRecyclerView() {
        Realm.getDefaultInstance().executeTransaction { realm ->
            if (!isVisible)
                return@executeTransaction
            val feed = realm.where(Feed::class.java).findAll()
            if (feed.size > 0) {
                recyclerView.adapter = RecAdapter(feed[0]!!.items)
                recyclerView.layoutManager = LinearLayoutManager(activity)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        request?.dispose()
    }
}