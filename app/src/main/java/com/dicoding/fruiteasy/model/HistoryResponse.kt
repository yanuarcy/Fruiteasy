package com.dicoding.fruiteasy.model

data class HistoryResponse(
    val HistoryID: String,
    val userLocalid: String,
    val fruitName: String,
    val createdAt: String,
    val fruitData: FruitData
)

data class FruitData(
    val id_buah: String,
    val nama_buah: String,
    val nama_latin: String,
    val desc_buah: String,
    val vitamin_c: String,
    val vitamin_a: String,
    val zat_besi: String,
    val protein_lemak: String,
    val ketahanan_tubuh: String,
    val penglihatan: String,
    val anemia: String,
    val gizi_buruk: String,
    val kandungan_protein: String,
    val kandungan_lemak: String,
    val kandungan_karbohidrat: String,
    val kandungan_energi: String,
    val kandungan_zat_besi: String,
    val kandungan_vitamin_a: String,
    val kandungan_vitamin_c: String,
    val kandungan_folat: String,
    val kandungan_kalsium: String,
    val kandungan_seng: String,
    val Berat_Buah_PerGizi: String,
    val nilai_gizi_Kalori: String,
    val image: String,
    val funfact: String,
    val icon: String,
    val faq: String
)
