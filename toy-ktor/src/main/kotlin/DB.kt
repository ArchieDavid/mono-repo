import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database

object Hotels : IntIdTable() {
    val name = varchar("name", 20).uniqueIndex()
    val age = integer("age").default(0)
}

object DB {
    val host = System.getenv("DB_HOST")
    val port = System.getenv("DB_PORT")
    val dbName = System.getenv("DB_NAME")
    val dbUser = System.getenv("DB_USER")
    val dbPassword = System.getenv("DB_PASSWORD")

    fun connect() = Database.connect(
        "jdbc:postgresql://$host:$port/$dbName", driver = "org.postgresql.Driver",
        user = dbUser, password = dbPassword
    )

}
