package com.tealium.example

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.tealium.example.helper.DataLayer
import com.tealium.example.helper.TealiumHelper


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val intent = intent
        intent.data?.toString()?.apply {
            TealiumHelper.trackEvent("track_deeplink",
                mapOf(
                    DataLayer.DEEPLINK_URL to this
                )
            )
        }



        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            TealiumHelper.trackEvent(
                "contact",
                mapOf(
                    DataLayer.EVENT_TOKEN to DataLayer.EventTokens.CONTACT,
                    DataLayer.EVENT_PARAM_1 to "value_1",
                    DataLayer.EVENT_PARAM_2 to "value_2",
                    DataLayer.PARTNER_PARAM_1 to "value_1",
                    DataLayer.PARTNER_PARAM_2 to "value_2",
                    DataLayer.CALLBACK_ID to "callbackId"
                )
            )
            Snackbar.make(view, "Contact Event Sent (t54mvx)", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        TealiumHelper.trackScreen(this, "MainActivity")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}