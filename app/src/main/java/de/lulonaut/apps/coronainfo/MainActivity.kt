package de.lulonaut.apps.coronainfo

import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    private fun applyText(responseDE: JSONObject, responseBE: JSONObject) {
        //apply new cases last 24 hr and incidence for Germany
        val newCasesView: TextView = findViewById(R.id.newCases)
        val incidenceDE: TextView = findViewById(R.id.incidenceDE)
        if (!responseDE.has("weekIncidence") && !responseDE.has("delta") && !responseDE.getJSONObject(
                "delta"
            ).has("cases")
        ) {
            showError("Fehlerhafte Daten bekommen")
            return
        } else {
            newCasesView.text = responseDE.getJSONObject("delta").get("cases").toString()
            incidenceDE.text = responseDE.getDouble("weekIncidence").toString()
                .removeRange(4, responseDE.getDouble("weekIncidence").toString().length)
        }
        //apply incidence for Berlin
        val incidenceBE: TextView = findViewById(R.id.incidenceBE)
        if (responseBE.has("data") && responseBE.getJSONObject("data")
                .has("BE") && responseBE.getJSONObject("data").getJSONObject("BE")
                .has("weekIncidence")
        ) {
            incidenceBE.text =
                responseBE.getJSONObject("data").getJSONObject("BE").getDouble("weekIncidence")
                    .toString()
                    .removeRange(
                        4,
                        responseBE.getJSONObject("data").getJSONObject("BE")
                            .getDouble("weekIncidence").toString().length
                    )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        val urlDE = "https://api.corona-zahlen.org/germany"
        val urlBE = "https://api.corona-zahlen.org/states/BE"
        thread {
            Looper.prepare()
            try {
                val responseBE = JSONObject(URL(urlBE).readText())
                val responseDE = JSONObject(URL(urlDE).readText())
                runOnUiThread {
                    applyText(responseDE, responseBE)
                }
            } catch (e: Exception) {
                showError("Fehler beim einholen der Daten\n (kein Internet?)")
            }
        }

    }
}