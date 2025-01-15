package ro.pub.cs.systems.eim.practicaltest02v1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SuggestionsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.broadcast.SUGGESTIONS") {
            val suggestions = intent.getStringExtra("suggestions")
            suggestions?.let {
                // Trimitem sugestiile către UI prin broadcast
                val updateIntent = Intent("com.example.broadcast.UPDATE_UI")
                updateIntent.putExtra("updatedSuggestions", it)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(updateIntent)
            }
        }
    }
}