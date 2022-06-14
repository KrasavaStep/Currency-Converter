package com.example.currencyconverter.data.cryptocurrency_api.models.crypto_data_models

data class CryptoDataLinks(
    val homepage: List<String>,
    val blockchain_site: List<String>,
    val official_forum_url: List<String>,
    val chat_url: List<String>,
    val announcement_url: List<String>,
    val twitter_screen_name: String,
    val facebook_username: String,
    val bitcointalk_thread_identifier: Long,
    val telegram_channel_identifier: String,
    val subreddit_url: String,
    val repos_url: HashMap<String, List<String>>
)