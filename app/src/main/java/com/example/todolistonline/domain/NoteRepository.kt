package com.example.todolistonline.domain

interface NoteRepository {

    suspend fun addNewNote(note: NoteEntity)
    suspend fun deleteNote(note: NoteEntity)
    suspend fun changeNote(note: NoteEntity)
    suspend fun sortNotes(notes: List<NoteEntity>)

}