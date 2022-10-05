package com.example.wsamad8.data.models

data class DataHistory(
    val date : String,
    val probability_infection : Int
)
data class HistoryList(
    val dataHistory: List<DataHistory>
)
