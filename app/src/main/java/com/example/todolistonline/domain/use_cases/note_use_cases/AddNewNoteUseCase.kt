package com.example.todolistonline.domain.use_cases.note_use_cases

import com.example.todolistonline.domain.NoteEntity
import com.example.todolistonline.domain.NoteRepository
import javax.inject.Inject

class AddNewNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: NoteEntity) = repository.addNewNote(note)
}