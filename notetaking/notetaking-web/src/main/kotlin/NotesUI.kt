package com.example.notetaking.web

import com.example.notetaking.service.NoteTakingService
import com.example.notetaking.shared.Importance
import com.example.notetaking.shared.NoteItem
import com.example.notetaking.shared.User
import com.example.notetaking.web.viewmodels.NoteVM
import io.ktor.application.call
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.time.LocalDate

val note = NoteItem(
    1,
    "Add database processing",
    "Add backend support to this code",
    "AD",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)

var notes = listOf(note, note)

fun Routing.notes(noteTakingService: NoteTakingService) {

    get("/notes") {
        notes = noteTakingService.getAll()

        val noteVM = NoteVM(notes, User("Michael Jordan"))

        call.respond(
            MustacheContent("notes.hbs", mapOf("notes" to noteVM))
        )
    }
}


