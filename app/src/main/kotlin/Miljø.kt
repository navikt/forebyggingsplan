internal object Miljø {
    val tokenxClientId: String by lazy { System.getenv("TOKEN_X_CLIENT_ID") }
    val tokenxIssuer: String by lazy { System.getenv("TOKEN_X_ISSUER") }
    val tokenxJwkPath: String by lazy { System.getenv("TOKEN_X_JWKS_URI") }
    val altinnRettigheterProxyUrl: String by lazy { System.getenv("ALTINN_RETTIGHETER_PROXY_URL")}
    val altinnRettighetServiceEdition: String by lazy { System.getenv().getOrDefault("ALTINN_RETTIGHET_SERVICE_EDITION", "2")}
    val dbHost: String by lazy { System.getenv("DB_HOST")}
    val dbDatabaseName: String by lazy { System.getenv("DB_DATABASE")}
    val dbPort: String by lazy { System.getenv("DB_PORT")}
    val dbUser: String by lazy { System.getenv("DB_USERNAME")}
    val dbPassword: String by lazy { System.getenv("DB_PASSWORD")}
}
