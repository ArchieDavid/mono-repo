package com.example.notetaking.service

import com.example.notetaking.shared.NoteItem
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get

interface NoteTakingService {
    suspend fun getAll(): List<NoteItem>
    fun getNote(id: Int): NoteItem
    fun delete(id: Int): Boolean
    fun create(todo: NoteItem): Boolean
    fun update(id: Int, note: NoteItem): Boolean
}

class NoteTakingServiceImpl : NoteTakingService {

    val client = HttpClient {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }
    val apiEndpoint = "http://localhost:8081/api/notes"

    override suspend fun getAll(): List<NoteItem> {
        return client.get(apiEndpoint)
    }

    override fun getNote(id: Int): NoteItem {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun create(todo: NoteItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(id: Int, note: NoteItem): Boolean {
        TODO("Not yet implemented")
    }

}