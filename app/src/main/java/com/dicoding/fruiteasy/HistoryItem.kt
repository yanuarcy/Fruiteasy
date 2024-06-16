package com.dicoding.fruiteasy

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryItem(
    val tglScan: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val idBuah: String,
    val descBuah: String,
    val vitaminC: String,
    val vitaminA: String,
    val zatBesi: String,
    val proteinLemak: String,
    val ketahananTubuh: String,
    val penglihatan: String,
    val anemia: String,
    val giziBuruk: String,
    val kandunganProtein: String,
    val kandunganLemak: String,
    val kandunganKarbohidrat: String,
    val kandunganEnergi: String,
    val kandunganZatBesi: String,
    val kandunganVitaminA: String,
    val kandunganVitaminC: String,
    val kandunganFolat: String,
    val kandunganKalsium: String,
    val kandunganSeng: String,
    val beratBuahPerGizi: String,
    val nilaiGiziKalori: String,
    val funfact: String,
    val faq: String
) : Parcelable
