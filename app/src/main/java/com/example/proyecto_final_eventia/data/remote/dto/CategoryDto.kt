package com.example.proyecto_final_eventia.data.remote.dto

import com.example.proyecto_final_eventia.domain.model.Category

data class CategoryDto(
    val id: Int,
    val name: String
)

fun CategoryDto.toDomain() = Category(id = id, name = name)
