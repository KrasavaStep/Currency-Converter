package com.example.currencyconverter.data.cryptocurrency_api.models.crypto_data_models

data class CryptoDataResponse(
    val id: String,
    val symbol: String,
    val name: String,
    val asset_platform_id: String,
    val platforms: HashMap<String, String>,
    val block_time_in_minutes: Long,
    val hashing_algorithm: String?,
    val categories: List<String?>,
    val public_notice: String?,
    val additional_notices: List<String?>,
    val description: HashMap<String, String>,
    val links: CryptoDataLinks, //
    val image: HashMap<String, String>,//
    val country_origin: String,
    val genesis_date: String?,
    val contract_address: String,
    val sentiment_votes_up_percentage: Float,
    val sentiment_votes_down_percentage: Float,
    val market_cap_rank: Float,
    val coingecko_rank: Float,
    val coingecko_score: Float,
    val developer_score: Float,
    val community_score: Float,
    val liquidity_score: Float,
    val public_interest_score: Float,
    val public_interest_stats: HashMap<String, String?>,
    val status_updates: List<String>,
    val last_updated: String
)