package ro.pub.cs.systems.eim.practicaltest02v1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.IOException

class PracticalTest02v1MainActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var resultText: TextView

    // Receiver pentru a gestiona broadcast-ul ce actualizează UI-ul
    private val suggestionsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.broadcast.UPDATE_UI") {
                val suggestions = intent.getStringExtra("updatedSuggestions")
                Log.d("BroadcastReceiver", "Sugestii primite: $suggestions")
                resultText.text = suggestions // Actualizează UI-ul cu sugestiile
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v1_main)

        // Inițializare componente UI
        searchInput = findViewById(R.id.searchInput)
        searchButton = findViewById(R.id.searchButton)
        resultText = findViewById(R.id.resultText)

        // Configurare acțiune pe buton
        searchButton.setOnClickListener {
            val prefix = searchInput.text.toString()
            if (prefix.isNotEmpty()) {
                // Apelează funcția care va face cererea HTTP
                searchAutocomplete(prefix)
            } else {
                resultText.text = "Te rugăm să introduci un prefix!"
            }
        }

        // Înregistrăm receiver-ul pentru a asculta broadcast-ul
        val filter = IntentFilter("com.example.broadcast.UPDATE_UI")
        LocalBroadcastManager.getInstance(this).registerReceiver(suggestionsReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        // Asigurăm că dezregistrăm receiver-ul pentru a evita scurgerile de memorie
        LocalBroadcastManager.getInstance(this).unregisterReceiver(suggestionsReceiver)
    }

    // Funcție pentru a face cererea HTTP
    private fun searchAutocomplete(prefix: String) {
        val client: CloseableHttpClient = HttpClients.createDefault()

        // Construim URL-ul pentru cererea GET
        val url = "https://www.google.com/complete/search?client=chrome&q=$prefix"

        // Creăm cererea HTTP GET
        val httpGet = HttpGet(url)

        // Fă cererea într-un thread separat pentru a nu bloca UI-ul
        Thread {
            try {
                val response = client.execute(httpGet)
                val responseBody = EntityUtils.toString(response.entity)

                // Afișează răspunsul complet în LogCat
                Log.d("GoogleAutocomplete", "Răspuns complet: $responseBody")

                // Parsăm răspunsul JSON
                val suggestions = parseAutocompleteResponse(responseBody)

                // Afișăm sugestiile în LogCat
                Log.d("GoogleAutocomplete", "Sugestii: $suggestions")

                // Trimitem sugestiile prin broadcast către UI
                sendSuggestionsBroadcast(suggestions)

            } catch (e: IOException) {
                Log.e("GoogleAutocomplete", "Eroare la cererea HTTP: ${e.message}")
                runOnUiThread {
                    resultText.text = "Eroare la obținerea răspunsului. Verifică conexiunea la internet."
                }
            } catch (e: Exception) {
                Log.e("GoogleAutocomplete", "Eroare neașteptată: ${e.message}")
                runOnUiThread {
                    resultText.text = "A apărut o eroare neașteptată."
                }
            }
        }.start()
    }

    // Funcție pentru a parsa răspunsul JSON
    private fun parseAutocompleteResponse(response: String?): List<String> {
        return try {
            val gson = Gson()
            val responseList: List<Any> = gson.fromJson(response, object : TypeToken<List<Any>>() {}.type)

            // Accesăm al doilea element din listă care conține sugestiile autocomplete
            val suggestions = responseList.getOrNull(1) as? List<String> ?: emptyList()

            // Returnăm lista de sugestii
            suggestions
        } catch (e: Exception) {
            Log.e("GoogleAutocomplete", "Eroare la parsarea JSON: ${e.message}")
            emptyList() // Returnează o listă goală în caz de eroare
        }
    }

    // Funcție care trimite broadcast-ul cu sugestiile
    private fun sendSuggestionsBroadcast(suggestions: List<String>) {
        val suggestionsString = suggestions.joinToString(",\n") // Convertim lista într-un string, separat prin virgulă și newline
        Log.d("Broadcast", "Trimitem broadcast cu sugestii: $suggestionsString")  // Verificare log

        val intent = Intent("com.example.broadcast.UPDATE_UI")
        intent.putExtra("updatedSuggestions", suggestionsString) // Adăugăm sugestiile în extras
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
