package edu.utap.billbriefer

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.billbriefer.ui.main.BillSearchFragment
import edu.utap.billbriefer.ui.main.MainViewModel
import edu.utap.billbriefer.ui.main.Constants
import edu.utap.billbriefer.ui.main.MainFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    companion object {
        val constants = Constants()
        val TAG = "BillBriefer::MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private var back_b: ImageButton? = null
    private var title_tv: TextView? = null
    lateinit var beforeBackPress: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        beforeBackPress = {}
        title_tv = toolbar.findViewById(R.id.title)
        back_b = toolbar!!.findViewById<ImageButton>(R.id.back_b)
        back_b!!.isClickable = true
        back_b!!.setOnClickListener() {
            onBackPressed()
        }
        viewModel.apikey = getString(R.string.apikey)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

    override fun onBackPressed() {
        beforeBackPress()
        super.onBackPressed()
    }

    fun set_toolbar(title: String, back_is_clickable: Boolean) {
        title_tv!!.text = title
        back_b!!.isClickable = back_is_clickable
        if(back_is_clickable) {
            back_b!!.setImageResource(R.drawable.ic_baseline_arrow_left_24)
        } else {
            back_b!!.setImageResource(R.color.green)
        }
    }

    fun check_network(): Boolean {
        val conn_manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network_info = conn_manager.getNetworkInfo(conn_manager.activeNetwork)

        if((network_info == null) || (network_info.state != NetworkInfo.State.CONNECTED)) {
            Toast.makeText(applicationContext, "Connect to network.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}