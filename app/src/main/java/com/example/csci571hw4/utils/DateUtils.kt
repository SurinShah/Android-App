package com.example.csci571hw4.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getCurrentDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    return LocalDate.now().format(formatter)
}
