package com.anisanurjanah.dicodingstoryapp.utils

import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryItem(
                "id $i",
                "www.photo.com/$i",
                "1-1-$i",
                "name $i",
                "description $i",
                i + 1.toDouble(),
                i + 2.toDouble()
            )
            items.add(story)
        }
        return items
    }
}