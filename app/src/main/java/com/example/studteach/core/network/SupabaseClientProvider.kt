package com.example.studteach.core.network

import com.example.studteach.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClientProvider {

    private var _client: SupabaseClient? = null

    val client: SupabaseClient
        get() {
            if (_client == null) {
                val url = BuildConfig.SUPABASE_URL
                val key = BuildConfig.SUPABASE_ANON_KEY

                if (url.isBlank() || key.isBlank()) {
                    throw IllegalStateException(
                        "Supabase credentials not found. Check your .env file."
                    )
                }

                _client = createSupabaseClient(
                    supabaseUrl = url,
                    supabaseKey = key
                ) {
                    install(Auth)
                    install(Postgrest)
                    install(Realtime)
                }
            }
            return _client!!
        }
}
