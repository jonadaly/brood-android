///*
// * Copyright (C) 2017 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.jonadaly.brood.api
//
//import android.arch.lifecycle.LiveData
//import android.arch.lifecycle.MediatorLiveData
//import android.support.annotation.MainThread
//import android.support.annotation.WorkerThread
//
//abstract class NetworkBoundResource<ResultType, RequestType>
//@MainThread constructor() {
//
//    private val result = MediatorLiveData<Resource<ResultType>>()
//
//    init {
//        result.value = Resource.loading(null)
//        @Suppress("LeakingThis")
//        val dbSource = loadFromDb()
//        result.addSource(dbSource) { data ->
//            result.removeSource(dbSource)
//            if (shouldFetch(data)) {
//                fetchFromNetwork(dbSource)
//            } else {
//                result.addSource(dbSource) { newData ->
//                    setValue(Resource.success(newData))
//                }
//            }
//        }
//    }
//
//    @MainThread
//    private fun setValue(newValue: Resource<ResultType>) {
//        if (result.value != newValue) {
//            result.value = newValue
//        }
//    }
//
//    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
//        val apiResponse = createCall()
//        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
//        result.addSource(dbSource) { newData ->
//            setValue(Resource.loading(newData))
//        }
//        result.addSource(apiResponse) { response ->
//            result.removeSource(apiResponse)
//            result.removeSource(dbSource)
//            when (response.status) {
//                is ApiSuccessResponse -> {
//                    appExecutors.diskIO().execute {
//                        saveCallResult(processResponse(response))
//                        appExecutors.mainThread().execute {
//                            // we specially request a new live data,
//                            // otherwise we will get immediately last cached value,
//                            // which may not be updated with latest results received from network.
//                            result.addSource(loadFromDb()) { newData ->
//                                setValue(Resource.success(newData))
//                            }
//                        }
//                    }
//                }
//                is ApiEmptyResponse -> {
//                    appExecutors.mainThread().execute {
//                        // reload from disk whatever we had
//                        result.addSource(loadFromDb()) { newData ->
//                            setValue(Resource.success(newData))
//                        }
//                    }
//                }
//                is ApiErrorResponse -> {
//                    onFetchFailed()
//                    result.addSource(dbSource) { newData ->
//                        setValue(Resource.error(response.errorMessage, newData))
//                    }
//                }
//            }
//        }
//    }
//
//    protected open fun onFetchFailed() {}
//
//    fun getAsLiveData() = result as LiveData<Resource<ResultType>>
//
//    @WorkerThread
//    protected abstract fun saveCallResult(item: RequestType)
//
//    @MainThread
//    protected abstract fun shouldFetch(data: ResultType?): Boolean
//
//    @MainThread
//    protected abstract fun loadFromDb(): LiveData<ResultType>
//
//    @MainThread
//    protected abstract fun createCall(): LiveData<Response<RequestType>>
//}