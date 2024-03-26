package com.example.budgetquagga

class Category(name: String, color: String, id: Int) {

    val ID = id
    val name : String = name
    val color : String = color

    override fun toString(): String {
        return name
    }
}