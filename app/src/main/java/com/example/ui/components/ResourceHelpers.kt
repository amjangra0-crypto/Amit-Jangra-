package com.example.ui.components

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.R

object ResourceHelpers {
    @DrawableRes
    fun getDrawableId(context: Context, name: String): Int {
        val resName = when {
            name.contains("shonen", ignoreCase = true) || name.contains("hero", ignoreCase = true) && !name.contains("heroine", ignoreCase = true) -> "char_shonen_hero"
            name.contains("lady", ignoreCase = true) || name.contains("mentor", ignoreCase = true) -> "char_lady_mentor"
            name.contains("chibi", ignoreCase = true) || name.contains("mascot", ignoreCase = true) -> "char_chibi_mascot"
            name.contains("cyber", ignoreCase = true) -> "scene_cyber_city"
            name.contains("temple", ignoreCase = true) || name.contains("cherry", ignoreCase = true) -> "scene_cherry_temple"
            name.contains("studio", ignoreCase = true) -> "hero_anime_studio"
            name.contains("heroine", ignoreCase = true) -> "char_anime_heroine"
            else -> name
        }

        val id = context.resources.getIdentifier(resName, "drawable", context.packageName)
        return if (id != 0) id else R.drawable.char_anime_heroine
    }
}
