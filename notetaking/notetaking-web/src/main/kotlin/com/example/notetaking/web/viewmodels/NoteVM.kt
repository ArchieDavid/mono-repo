package com.example.notetaking.web.viewmodels

import com.example.notetaking.shared.NoteItem
import com.example.notetaking.shared.User

data class NoteVM(private val notes: List<NoteItem>, private val user: User) {
    val userName = user.name
    val noteItems = notes
}


