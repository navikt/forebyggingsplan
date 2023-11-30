internal object Systemmiljø : Miljø {
    override val sanityHost: String by lazy { System.getenv("SANITY_HOST") }
    override val tokenxClientId: String by lazy { System.getenv("TOKEN_X_CLIENT_ID") }
    override val tokenxIssuer: String by lazy { System.getenv("TOKEN_X_ISSUER") }
    override val tokenxJwkPath: String by lazy { System.getenv("TOKEN_X_JWKS_URI") }
    override val tokenxPrivateJwk: String by lazy { System.getenv("TOKEN_X_PRIVATE_JWK") }
    override val tokenXTokenEndpoint: String by lazy { System.getenv("TOKEN_X_TOKEN_ENDPOINT") }
    override val altinnRettigheterProxyUrl: String by lazy { System.getenv("ALTINN_RETTIGHETER_PROXY_URL") }
    override val altinnRettigheterProxyClientId: String by lazy { System.getenv("ALTINN_RETTIGHETER_PROXY_CLIENT_ID") }
    override val dbHost: String by lazy { System.getenv("DB_HOST") }
    override val dbDatabaseName: String by lazy { System.getenv("DB_DATABASE") }
    override val dbPort: String by lazy { System.getenv("DB_PORT") }
    override val dbUser: String by lazy { System.getenv("DB_USERNAME") }
    override val dbPassword: String by lazy { System.getenv("DB_PASSWORD") }

    override val cluster: String by lazy { System.getenv("NAIS_CLUSTER_NAME") }
}

enum class Clusters(val clusterId: String) {
    PROD_GCP("prod-gcp"), DEV_GCP("dev-gcp"), LOKAL("local")
}
