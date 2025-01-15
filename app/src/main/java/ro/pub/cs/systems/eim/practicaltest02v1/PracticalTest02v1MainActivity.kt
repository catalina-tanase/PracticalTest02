package ro.pub.cs.systems.eim.practicaltest02v1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
                // Aici ar urma să adaugi logica de căutare
                resultText.text = "Rezultate pentru: $prefix"
            } else {
                resultText.text = "Te rugăm să introduci un prefix!"
            }
        }
    }
}