package api.sanity

import http.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import java.util.UUID

class SanityForebyggngsplan(val apiVersion: String, dataset: String) {
    private val dataset = Dataset.toDataset(dataset)
    suspend fun eksisterer(aktivitetsmalId: UUID): Boolean {
        val query = "*[_type == \"Aktivitet\" && _id == \"${aktivitetsmalId}\"]"
        val baseUrl =
            "https://${Miljø.sanityProjectId}.api.sanity.io/$apiVersion/data/query/${dataset.name.lowercase()}?query="
        val response = HttpClient.client.get(baseUrl + query) {
            accept(ContentType.Application.Json)
        }
        return response.body<List<Any>>().isNotEmpty()
    }

    enum class Dataset {
        Production, Development;
        companion object {
            fun toDataset(input: String): Dataset {
                return when (input.lowercase()) {
                    "production" -> Production
                    else -> Development
                }
            }
        }

    }
}


