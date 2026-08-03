package abhishek.gupta.weatherapp.data.remote.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.storage.Storage


object SupabaseClientProvider {
    val client =
        createSupabaseClient(
            supabaseUrl = "https://yyesferwrtxwcvkzwpjl.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl5ZXNmZXJ3cnR4d2N2a3p3cGpsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODU0MTEwNDMsImV4cCI6MjEwMDk4NzA0M30.X4eirE51aPUK8Fv0zLhFRGcgYFN9aWaQjoC9zzpWeHg"
        ) {
            install(GoTrue)
            install(Storage)
        }

}