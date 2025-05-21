package com.huyhieu.library.ui.model

class ItemState<out T>(
    val width: Int = 0,
    val height: Int = 0,

    val x: Int = 0,
    val y: Int = 0,

    val rotate: Float = 0F,
    val scale: Float = 0F,

    val isChecked: Boolean = false,
    val isSelected: Boolean = false,
    val isAvailable: Boolean = false,

    val name: String = "",
    val url: String = "",
    val value: String = "",

    val data: T,
)