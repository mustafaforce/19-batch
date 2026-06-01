package com.example.studteach

import android.app.Application
import com.example.studteach.core.network.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient

class StudTeachApp : Application() {

    lateinit var supabase: SupabaseClient
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        supabase = SupabaseClientProvider.client
    }

    companion object {
        lateinit var instance: StudTeachApp
            private set
    }
}
