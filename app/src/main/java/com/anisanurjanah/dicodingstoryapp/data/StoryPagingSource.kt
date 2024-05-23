package com.anisanurjanah.dicodingstoryapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.data.remote.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, StoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getStories(position, params.loadSize)
            val responseData = response.listStory?.filterNotNull() ?: emptyList()

            Log.d("StoryPagingSource", "Response: $response")
            Log.d("StoryPagingSource", "Response Data: $responseData")
            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}