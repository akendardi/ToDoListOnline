package com.example.todolistonline.domain.use_cases.note_use_cases

import com.example.todolistonline.domain.NoteEntity
import com.example.todolistonline.domain.NoteRepository
import javax.inject.Inject

class SortNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(notes: List<NoteEntity>) = repository.sortNotes(notes)
}