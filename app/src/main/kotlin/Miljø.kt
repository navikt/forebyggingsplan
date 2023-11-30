interface DbMiljø {
    val dbHost: String
    val dbDatabaseName: String
    val dbPort: String
    val dbUser: String
    val dbPassword: String
}

interface Miljø : DbMiljø {
    val tokenxClientId: String
    val tokenxIssuer: String
    val tokenxJwkPath: String
    val tokenxPrivateJwk: String
    val tokenXTokenEndpoint: String
    val altinnRettigheterProxyUrl: String

    val altinnRettigheterProxyClientId: String
    val cluster: String
}