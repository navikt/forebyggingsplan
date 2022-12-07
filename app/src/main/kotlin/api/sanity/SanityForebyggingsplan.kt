package api.sanity

import Clusters.DEV_GCP
import Clusters.PROD_GCP
import Miljø
import http.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

class SanityForebyggingsplan(apiVersion: String) {
    private val dataset = when (Miljø.cluster) {
        PROD_GCP.clusterId -> Dataset.Production
        DEV_GCP.clusterId -> Dataset.Production
        else -> Dataset.Development
    }
    private val baseUrl = "${Miljø.sanityHost}/v$apiVersion/data/query/${this.dataset.name.lowercase()}?query="

    suspend fun hentAktivitetsinfo(aktivitetsmalId: UUID): SanityResult? {
        val query = "*[_type == \"Aktivitet\" && _id == \"${aktivitetsmalId}\"]"
        val response = HttpClient.client.get(baseUrl + query.encodeURLParameter()) {
            accept(ContentType.Application.Json)
        }
        return response.body<SanityResponse>().result.firstOrNull()
    }

    enum class Dataset {
        Production, Development;
    }
}

@Serializable
class SanityResponse (val result: List<SanityResult>)

@Serializable
class SanityResult( @SerialName("_id")val malId: String, @SerialName("_rev") val versjon: String)


