package org.fb.gym.meet.data

data class Gymnast(
   val id: String,
   val name: String,
   val category: Category
)

enum class Category {
   C1, C2, C3, C4, C5, C6, C7
}