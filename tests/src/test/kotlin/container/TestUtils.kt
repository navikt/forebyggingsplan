import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.serialization.responseObject
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> Request.tilSingelRespons() =
    this.responseObject(loader = T::class.serializer(), json = Json.Default)

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> Request.tilListeRespons() =
    this.responseObject(loader = ListSerializer(T::class.serializer()), json = Json.Default)

internal val <T> ResponseResultOf<T>.data
    get() = this.third.get()