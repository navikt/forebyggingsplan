internal object Miljø {
    val sanityHost: String by lazy { System.getenv("SANITY_HOST") }
    val tokenxClientId: String by lazy { System.getenv("TOKEN_X_CLIENT_ID") }
    val tokenxIssuer: String by lazy { System.getenv("TOKEN_X_ISSUER") }
    val tokenxJwkPath: String by lazy { System.getenv("TOKEN_X_JWKS_URI") }
    val tokenxPrivateJwk: String by lazy { System.getenv("TOKEN_X_PRIVATE_JWK") }
    val tokenXTokenEndpoint: String by lazy { System.getenv("TOKEN_X_TOKEN_ENDPOINT") }
    val altinnRettigheterProxyUrl: String by lazy { System.getenv("ALTINN_RETTIGHETER_PROXY_URL") }
    val altinnRettighetServiceCode: String by lazy {
        System.getenv().getOrDefault("ALTINN_RETTIGHET_SERVICE_CODE", "3403")
    }
    val altinnRettighetServiceEdition: String by lazy {
        System.getenv().getOrDefault("ALTINN_RETTIGHET_SERVICE_EDITION", "2")
    }
    val altinnRettigheterProxyClientId: String by lazy { System.getenv("ALTINN_RETTIGHETER_PROXY_CLIENT_ID") }
    val dbHost: String by lazy { System.getenv("DB_HOST") }
    val dbDatabaseName: String by lazy { System.getenv("DB_DATABASE") }
    val dbPort: String by lazy { System.getenv("DB_PORT") }
    val dbUser: String by lazy { System.getenv("DB_USERNAME") }
    val dbPassword: String by lazy { System.getenv("DB_PASSWORD") }

    val cluster: String by lazy { System.getenv("NAIS_CLUSTER_NAME") }
}

enum class Clusters(val clusterId: String) {
    PROD_GCP("prod-gcp"), DEV_GCP("dev-gcp"), LOKAL("local")
}
