package com.example

import com.example.notetaking.dataaccess.NoteTakingDataAccessService
import com.example.notetaking.shared.NoteItem
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Routing.noteTakingApi(noteTakingDataAccessService: NoteTakingDataAccessService) {
    route("/api") {

        accept(NoteTakingContentV1) {
            get("/notes") {
                call.respond(noteTakingDataAccessService.getAll())
            }
        }

        get("/notes") {
            call.respond(noteTakingDataAccessService.getAll())
        }

        get("notes/{id}") {
            val id = call.parameters["id"]!!
            try {
                val note = noteTakingDataAccessService.getNote(id.toInt())//notes.first { it.id == id.toInt() }
                call.respond(note)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("notes") {
            val note = call.receive<NoteItem>()
            noteTakingDataAccessService.create(note)
            call.respond(HttpStatusCode.Created)
        }

        put("notes/{id}") {
            val id =  call.parameters["id"] ?: throw IllegalArgumentException("Missing id")

            val note = call.receive<NoteItem>()

            noteTakingDataAccessService.update(id.toInt(), note)

            call.respond(HttpStatusCode.NoContent)
        }

        delete("notes/{id}") {
            val id =  call.parameters["id"] ?: throw IllegalArgumentException("Missing id")
            noteTakingDataAccessService.delete(id.toInt())
            call.respond(HttpStatusCode.NoContent)
        }
    }
}