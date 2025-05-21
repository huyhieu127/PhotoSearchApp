package com.huyhieu.domain.model

data class PhotoModel(
    val alt: String = "",
    val avgColor: String = "",
    val height: Int = 0,
    val id: Int = 0,
    val liked: Boolean = false,
    val photographer: String = "",
    val photographerId: String = "",
    val photographerUrl: String = "",
    val src: SrcModel = SrcModel(),
    val url: String = "",
    val width: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        return (other is PhotoModel) && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}