package com.example.ui.components

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.R

object ResourceHelpers {
    @DrawableRes
    fun getDrawableId(context: Context, name: String): Int {
        // Priority mapping to newly generated vibrant and colorful pictures
        val candidateNames = when {
            name.contains("vibrant_hero", ignoreCase = true) || name.contains("hero_anime_studio", ignoreCase = true) || name.contains("hero_studio", ignoreCase = true) -> {
                listOf("img_vibrant_anime_hero_1790581925628", "img_vibrant_studio_1790581403275", "hero_anime_studio")
            }
            name.contains("vibrant_fantasy", ignoreCase = true) || name.contains("temple", ignoreCase = true) || name.contains("cherry", ignoreCase = true) || name.contains("scene_fantasy", ignoreCase = true) -> {
                listOf("img_vibrant_fantasy_scene_1790581940931", "scene_cherry_temple", "scene_cyber_city")
            }
            name.contains("vibrant_char", ignoreCase = true) || name.contains("vibrant_avatar", ignoreCase = true) -> {
                listOf("img_vibrant_character_art_1790581954600", "char_anime_heroine", "char_shonen_hero")
            }
            name.contains("studio", ignoreCase = true) -> {
                listOf("img_vibrant_anime_hero_1790581925628", "img_vibrant_studio_1790581403275", "hero_anime_studio")
            }
            name.contains("shonen", ignoreCase = true) || (name.contains("hero", ignoreCase = true) && !name.contains("heroine", ignoreCase = true)) -> {
                listOf("char_shonen_hero")
            }
            name.contains("lady", ignoreCase = true) || name.contains("mentor", ignoreCase = true) -> {
                listOf("char_lady_mentor")
            }
            name.contains("chibi", ignoreCase = true) || name.contains("mascot", ignoreCase = true) -> {
                listOf("char_chibi_mascot")
            }
            name.contains("cyber", ignoreCase = true) -> {
                listOf("scene_cyber_city", "img_vibrant_fantasy_scene_1790581940931")
            }
            name.contains("heroine", ignoreCase = true) -> {
                listOf("img_vibrant_character_art_1790581954600", "char_anime_heroine")
            }
            else -> listOf(name)
        }

        for (cand in candidateNames) {
            val resId = context.resources.getIdentifier(cand, "drawable", context.packageName)
            if (resId != 0) return resId
        }

        return R.drawable.char_anime_heroine
    }
}
