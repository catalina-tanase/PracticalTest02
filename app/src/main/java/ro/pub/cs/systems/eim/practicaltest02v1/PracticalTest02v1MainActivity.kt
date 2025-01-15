package ro.pub.cs.systems.eim.practicaltest02v1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.IOException

class PracticalTest02v1MainActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var resultText: TextView

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
    }
    // Funcție care face cererea HTTP folosind Apache HttpClient
    private fun searchAutocomplete(prefix: String) {
        val url = "https://www.google.com/complete/search?client=chrome&q=$prefix"
        val client = HttpClients.createDefault()
        val request = HttpGet(url)

        // Fă cererea în thread-ul de fundal pentru a nu bloca UI-ul
        Thread {
            try {
                val response = client.execute(request)
                val entity = response.entity
                val responseBody = EntityUtils.toString(entity)

                // Afișează răspunsul complet în LogCat
                Log.d("GoogleAutocomplete", "Răspuns complet: $responseBody")

                // Poți vizualiza răspunsul în TextView pentru debugging
                runOnUiThread {
                    resultText.text = "Răspunsul complet a fost înregistrat în LogCat"
                }
            } catch (e: IOException) {
                // În caz de eroare
                Log.e("GoogleAutocomplete", "Eroare la cererea HTTP: ${e.message}")
                runOnUiThread {
                    resultText.text = "Eroare la obținerea răspunsului."
                }
            }
        }.start()
    }
}