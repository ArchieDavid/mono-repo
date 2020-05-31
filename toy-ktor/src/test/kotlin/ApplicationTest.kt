import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before

import org.junit.Test

class ApplicationTest {
    @Test
    fun validValue() {
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Get, "/hotelcali1234")
            assertEquals(
                """
                {
                   "Hotel name:" : "hotelcali1234"
                }
                """.asJson(), call.response.content?.asJson()
            )
        }
    }

    @Test
    fun emptyPath() {
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Get, "")
            assertEquals(HttpStatusCode.OK, call.response.status())
        }
    }

}

fun String.asJson() = ObjectMapper().readTree(this)