package com.daniilk.stepikdevmobapp

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.realm.RealmList
import io.realm.RealmObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putString("param", "value")
            val fragment = MainFragment()
            fragment.arguments = bundle
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_place, fragment)
                .commitAllowingStateLoss()
            // commit - может работать в отдельном потоке и не быть выполнен что приведет к ошибке
            // commitAllowingStateLoss - допускающая потерю состояния

        }
    }

    // Отображение статьи
    fun showArticle(url: String) {
        val bundle = Bundle()
        bundle.putString("url", url)
        val fragment = WebFragment()
        fragment.arguments = bundle

        val frame2 = findViewById<View>(R.id.fragment_place2)
        if (frame2 != null) {
            frame2.visibility = View.VISIBLE
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_place2, fragment)
                .addToBackStack("main")
                .commitAllowingStateLoss()
        } else
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_place, fragment)
                .addToBackStack("main")
                .commitAllowingStateLoss()
    }

    fun playMusic(guid: String) {
        val intent = Intent(this, PlayService::class.java)
        intent.putExtra("mp3", guid)
        startService(intent)
    }


    companion object {
        internal const val TAG = "MainActivity"
    }
}

data class FeedApi(
    val items: ArrayList<FeedItemApi>
)

data class FeedItemApi(
    val title: String,
    val link: String,
    val thumbnail: String,
    val description: String,
    val guid: String
)

open class Feed(
    var items: RealmList<FeedItem> = RealmList<FeedItem>()
) : RealmObject()

open class FeedItem(
    var title: String = "",
    var link: String = "",
    var thumbnail: String = "",
    var description: String = "",
    var guid: String = ""
) : RealmObject()

class RecAdapter(private val items: RealmList<FeedItem>) :
    RecyclerView.Adapter<RecAdapter.RecHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)

        return RecHolder(view)
    }

    override fun onBindViewHolder(holder: RecHolder, position: Int) {
        val item = items[position]!!
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class RecHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: FeedItem) {
            val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
            val txtDescription = itemView.findViewById<TextView>(R.id.txtDescription)
            val imgThumb = itemView.findViewById<ImageView>(R.id.imgThumb)
            txtTitle.text = item.title
            txtDescription.text = Html.fromHtml(item.description)

            val thumbURI: String = item.thumbnail
            try {
                Picasso.with(imgThumb.context).load(thumbURI).into(imgThumb)
            } catch (e: Exception) { // При пустой картинке или ошибке
                Log.e("ImgLoadingError", e.message ?: "null")
                val defaultThumbURI =
                    "https://img3.akspic.ru/previews/8/7/7/6/6/166778/166778-spongebob-360x640.jpg"
                Picasso.with(imgThumb.context).load(defaultThumbURI).into(imgThumb)
            }

            itemView.setOnClickListener {
                (itemView.context as MainActivity).showArticle(item.link)
                (itemView.context as MainActivity).playMusic(item.guid)
            }
        }
    }
}