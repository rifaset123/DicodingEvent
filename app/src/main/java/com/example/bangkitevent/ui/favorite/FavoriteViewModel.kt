package com.example.bangkitevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bangkitevent.data.remote.local.entity.EventEntity
import com.example.bangkitevent.data.remote.local.repository.EventRepo

class FavoriteViewModel(private val eventRepo: EventRepo) : ViewModel()  {
    val favoriteEvents = eventRepo.getFavoriteEvents()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _favoriteEventsEntity = MutableLiveData<List<EventEntity>>()
    val favoriteEventsEntity: LiveData<List<EventEntity>> = _favoriteEventsEntity

    init {
        observeFavoriteEvents()
    }

    private fun observeFavoriteEvents() {
        _isLoading.value = true
        eventRepo.getFavoriteEvents().observeForever { events ->
            _isLoading.value = false
            _favoriteEventsEntity.value = events
        }
    }
}