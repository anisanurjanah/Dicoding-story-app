package com.anisanurjanah.dicodingstoryapp.view.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.anisanurjanah.dicodingstoryapp.data.Result
import com.anisanurjanah.dicodingstoryapp.data.pref.UserPreference
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.data.repository.StoryRepository
import com.anisanurjanah.dicodingstoryapp.utils.DataDummy
import com.anisanurjanah.dicodingstoryapp.utils.MainDispatcherRule
import com.anisanurjanah.dicodingstoryapp.utils.StoryPagingSource
import com.anisanurjanah.dicodingstoryapp.utils.getOrAwaitValue
import com.anisanurjanah.dicodingstoryapp.utils.noopListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock private lateinit var repository: StoryRepository
    @Mock private lateinit var preference: UserPreference
    private lateinit var mainViewModel: MainViewModel

    private lateinit var logMock: MockedStatic<Log>

    @Before
    fun setUp() {
        logMock = mockStatic(Log::class.java)
        logMock.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }.thenReturn(true)

        mainViewModel = MainViewModel(repository, preference)
    }

    @After
    fun tearDown() {
        logMock.close()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyQuote = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryItem> = StoryPagingSource.snapshot(dummyQuote)

        val expectedStory = MutableLiveData<Result<PagingData<StoryItem>>>()
        expectedStory.value = Result.Success(data)

        Mockito.`when`(repository.getAllStories(mainViewModel.viewModelScope)).thenReturn(expectedStory)

        val actualStory = mainViewModel.stories.getOrAwaitValue()
        val response = (actualStory as Result.Success).data

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(response)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyQuote.size, differ.snapshot().size)
        Assert.assertEquals(dummyQuote[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryItem> = PagingData.from(emptyList())

        val expectedStory = MutableLiveData<Result<PagingData<StoryItem>>>()
        expectedStory.value = Result.Success(data)

        Mockito.`when`(repository.getAllStories(mainViewModel.viewModelScope)).thenReturn(expectedStory)

        val actualStory = mainViewModel.stories.getOrAwaitValue()
        val response = (actualStory as Result.Success).data

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(response)

        Assert.assertEquals(0, differ.snapshot().size)
    }

}