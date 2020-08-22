package com.example.notetaking.dataaccess

import com.example.notetaking.repository.NotesRepository
import com.example.notetaking.shared.Importance
import com.example.notetaking.shared.NoteItem
import java.time.LocalDate

interface NoteTakingDataAccessService {
    fun getAll(): List<NoteItem>
    fun getNote(id: Int): NoteItem
    fun delete(id: Int): Boolean
    fun create(note: NoteItem): Boolean
    fun update(id: Int, note: NoteItem): Boolean
}


val note1 = NoteItem(
    1,
    "Add RestAPI Data Access",
    "Add database support",
    "Arc",
    LocalDate.of(2018, 12, 18),
    Importance.LOW
)

val note2 = NoteItem(
    2,
    "Add RestAPI Service",
    "Add a service to get the data",
    "Someone",
    LocalDate.of(2018, 12, 18),
    Importance.HIGH
)


var notes = listOf(note1, note2) // todo - don't mutate

class NoteTakingDataAccessServiceImpl(val notesRepository: NotesRepository) : NoteTakingDataAccessService {
    override fun getAll(): List<NoteItem> = notes

    override fun getNote(id: Int): NoteItem = notes.first { it.id == id }
    override fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun create(note: NoteItem): Boolean {
        notes += note
        return true // todo - fix this
    }

    override fun update(id: Int, note: NoteItem): Boolean {
        TODO("Not yet implemented")
    }
}