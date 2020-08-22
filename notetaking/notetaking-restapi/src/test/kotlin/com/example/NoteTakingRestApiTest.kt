package com.example.com.example

import com.example.module
import com.example.notetaking.shared.Importance
import com.example.notetaking.shared.NoteItem
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import java.time.LocalDate

class NoteTakingRestApiTest : StringSpec() {
    init {
        "should be ok to return list of notes" {
            testApp {
                handleRequest(HttpMethod.Get, "/api/notes").apply {
                    response.status() shouldBe HttpStatusCode.OK
                }
            }
        }

        "should return a note based on id" {
            testApp {
                handleRequest(HttpMethod.Get, "/api/notes/1").apply {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldContain "Add RestAPI Data Access"
                }
            }
        }


        "should create a new note" {
            testApp {
                val mapper = jacksonObjectMapper()
                    .registerModule(JavaTimeModule())

                val newNoteItem = NoteItem(
                    3,
                    "",
                    "",
                    "",
                    LocalDate.of(2018, 12, 18),
                    Importance.MEDIUM
                )
                val json = mapper.writeValueAsString(newNoteItem)

                var call = handleRequest(HttpMethod.Post, "/api/notes") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(json)
                }

                with(call) {
                    response.status() shouldBe HttpStatusCode.Created
                }
            }
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({ module() }) { callback() }
    }
}