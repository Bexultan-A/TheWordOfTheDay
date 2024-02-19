package com.example.thewordoftheday

data class WordModel(
    val word: String,
    val translation: String,
) {
    constructor() : this("","")
}
