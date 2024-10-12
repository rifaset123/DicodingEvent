package com.example.bangkitevent.ui.finished

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bangkitevent.data.response.EventResponse
import com.example.bangkitevent.data.response.ListEventsItem
import com.example.bangkitevent.data.retrofit.ApiConfig
import com.example.bangkitevent.data.retrofit.ApiService
import com.example.bangkitevent.ui.upcoming.UpcomingViewModel
import com.example.bangkitevent.ui.upcoming.UpcomingViewModel.Companion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    companion object{
        private const val TAG = "MainViewModel"

    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _eventsName = MutableLiveData<List<EventResponse>>()
    val eventsName: LiveData<List<EventResponse>> = _eventsName

    private val _listEventsItem = MutableLiveData<List<ListEventsItem>?>()
    val listEventsItem: LiveData<List<ListEventsItem>?> = _listEventsItem

    // store default value before query inputed
    private val _storedDefault = MutableLiveData<List<ListEventsItem>?>()
    val storedDefault: LiveData<List<ListEventsItem>?> = _storedDefault

    init {
        showEvents()
    }

    private fun showEvents(newListEventsItem: List<ListEventsItem>? = null) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFinishedEvents()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        _listEventsItem.value = it.listEvents
                        if (_storedDefault.value == null) {
                            _storedDefault.value = it.listEvents
                        }
                        _listEventsItem.value = newListEventsItem ?: it.listEvents
                        Log.d("wtest", "onResponse: Success - ${newListEventsItem ?: it.listEvents}")
                    } ?: run {
                        Log.e("wtest", "onResponse: Failure - Response body is null")
                    }
                } else {
                    Log.e("wtest", "onResponse: Failure - ${response.code()} - ${response.message()}")
                    Toast.makeText(getApplication(), "Failed to Fetch API", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("wtest", "onFailure: ${t.message.toString()}")
                Toast.makeText(getApplication(), "Failed to load events: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        Log.d("QueryTest", "Fetching event details for ID: $query")

        // Call the API service to get event details
        val client = ApiConfig.getApiService().searchEvents(query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                _listEventsItem.value = emptyList() // Clear existing data
                Log.d("QueryTest", "API response received")

                // Check if the response is successful and body is not null
                if (response.isSuccessful) {
                    response.body()?.let { eventResponse ->
                        // store the value to LiveData _detailEvent
                        _listEventsItem.value = eventResponse.listEvents
                        showEvents(eventResponse.listEvents)
                        Log.d("QueryTest", "onResponse: Success - ${eventResponse.listEvents.size}")

                    } ?: run {
                        Log.e("QueryTest", "onResponse: Failure - Response body is null")
                    }
                } else {
                    // Handle error response
                    Log.e("QueryTest", "onResponse: Failure - ${response.code()} - ${response.message()}")
                    Toast.makeText(getApplication(), "Failed to Fetch API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("QueryTest", "onFailure: ${t.message}")
                Toast.makeText(getApplication(), "Failed to load events: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}