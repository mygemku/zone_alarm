package com.zonealarm.app.di

import android.content.Context
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.zonealarm.app.data.datastore.SettingsRepository
import com.zonealarm.app.data.datastore.SettingsStore
import com.zonealarm.app.data.geocoding.GeocodingRepository
import com.zonealarm.app.data.geocoding.NominatimApi
import com.zonealarm.app.data.geocoding.NominatimGeocodingRepository
import com.zonealarm.app.data.local.AlarmZoneDao
import com.zonealarm.app.data.local.ZoneAlarmDatabase
import com.zonealarm.app.data.location.CurrentLocationProvider
import com.zonealarm.app.data.location.FusedLocationProvider
import com.zonealarm.app.data.repo.ZoneRepositoryImpl
import com.zonealarm.app.domain.repo.ZoneRepository
import com.zonealarm.app.geofence.GeofenceController
import com.zonealarm.app.geofence.GeofenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZoneAlarmDatabase =
        androidx.room.Room.databaseBuilder(
            context,
            ZoneAlarmDatabase::class.java,
            "zonealarm.db",
        ).build()

    @Provides
    fun provideAlarmZoneDao(db: ZoneAlarmDatabase): AlarmZoneDao = db.alarmZoneDao()

    @Provides
    @Singleton
    fun provideZoneRepository(impl: ZoneRepositoryImpl): ZoneRepository = impl

    @Provides
    @Singleton
    fun provideGeofencingClient(@ApplicationContext context: Context): GeofencingClient =
        LocationServices.getGeofencingClient(context)

    @Provides
    @Singleton
    fun provideGeofenceController(manager: GeofenceManager): GeofenceController = manager

    @Provides
    @Singleton
    fun provideLocationProvider(impl: FusedLocationProvider): CurrentLocationProvider = impl

    @Provides
    @Singleton
    fun provideSettingsStore(impl: SettingsRepository): SettingsStore = impl

    @Provides
    @Singleton
    fun provideNominatimApi(): NominatimApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", "ZoneAlarm/1.0 (Android)")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingRepository(impl: NominatimGeocodingRepository): GeocodingRepository = impl
}
