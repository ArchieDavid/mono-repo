package hotels


import DB
import Hotels
import asJson
import io.ktor.application.Application
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.Assert.assertEquals
import mainModule
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test

class HotelRouterTest {
    @Test
    fun `Create hotel`() {
        withTestApplication(Application::mainModule) {
            val call = createHotel("hotelcalifornia", 300)
            assertEquals(HttpStatusCode.Created, call.response.status())
        }
    }


    @Test
    fun `All cats`() {
        withTestApplication(Application::mainModule) {
            val beforeCreate = handleRequest(HttpMethod.Get, "/hotels")
            assertEquals("[]".asJson(), beforeCreate.response.content?.asJson())

            createHotel("hotel101", 1)
            val afterCreate = handleRequest(HttpMethod.Get, "/hotels")
            assertEquals("""[{"id":1,"name":"hotel101","age":1}]"""".asJson(), afterCreate.response.content?.asJson())
        }
    }

    @Test
    fun `Hotel by Id`() {
        withTestApplication(Application::mainModule) {
            val createCall = createHotel("hotel104", 1)
            val id = createCall.response.content
            val afterCreate = handleRequest(HttpMethod.Get, "/hotels/$id")
            assertEquals("""{"id":1,"name":"hotel104","age":1}"""".asJson(), afterCreate.response.content?.asJson())
        }
    }


    @Test
    fun `Delete by Id`() {
        withTestApplication(Application::mainModule) {
            val createCall = createHotel("hotel104", 1)
            val id = createCall.response.content
            val afterCreate = handleRequest(HttpMethod.Get, "/hotels/$id")
            assertEquals("""{"id":1,"name":"hotel104","age":1}"""".asJson(), afterCreate.response.content?.asJson())

            handleRequest(HttpMethod.Delete, "/hotels/$id")

            val afterDelete = handleRequest(HttpMethod.Get, "/hotels/$id")
            assertEquals(HttpStatusCode.NotFound, afterDelete.response.status())
        }
    }

    @Test
    fun `Update by Id`() {
        withTestApplication(Application::mainModule) {
            val createCall = createHotel("hotel777", 1)
            val id = createCall.response.content
            val afterCreate = handleRequest(HttpMethod.Get, "/hotels/$id")
            assertEquals("""{"id":1,"name":"hotel777","age":1}"""".asJson(), afterCreate.response.content?.asJson())

            handleRequest(HttpMethod.Put, "/hotels/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("name" to "newname", "age" to 45.toString()).formUrlEncode())
            }

            val afterUpdate = handleRequest(HttpMethod.Get, "/hotels/$id")
            assertEquals("""{"id":1,"name":"newname","age":45}"""".asJson(), afterUpdate.response.content?.asJson())
        }
    }


    @Before
    fun cleanup() {
        DB.connect()
        transaction {
            SchemaUtils.drop(Hotels)
        }
    }

    private fun TestApplicationEngine.createHotel(name: String, age: Int): TestApplicationCall {
        return handleRequest(HttpMethod.Post, "/hotels") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("name" to name, "age" to age.toString()).formUrlEncode())
        }
    }
}