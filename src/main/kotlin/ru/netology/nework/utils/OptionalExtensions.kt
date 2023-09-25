package ru.netology.nework.utils

import java.util.Optional

fun <T> Optional<T>.getOrNull(): T? = if (isPresent) get() else null
