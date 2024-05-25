package com.anisanurjanah.dicodingstoryapp.data.remote.local

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.data.remote.retrofit.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, StoryItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                Log.d(TAG, "LoadType.REFRESH: remoteKeys = $remoteKeys")
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                Log.d(TAG, "LoadType.PREPEND: remoteKeys = $remoteKeys")
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                Log.d(TAG, "LoadType.APPEND: remoteKeys = $remoteKeys")
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            Log.d(TAG, "Fetching stories from network for page = $page")
            val response = apiService.getStories(page, state.config.pageSize)
            val rawResult = response.listStory ?: throw IllegalStateException("List story is null")
            val responseData = rawResult.filterNotNull()
            val endOfPaginationReached = responseData.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d(TAG, "Refreshing database")
                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
                    storyDatabase.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                storyDatabase.remoteKeysDao().insertAll(keys)
                storyDatabase.storyDao().insertStory(responseData)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            Log.e(TAG, "Error fetching stories: ${exception.message}")
            return MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val TAG = "StoryRemoteMediator"
        const val INITIAL_PAGE_INDEX = 1
    }

}