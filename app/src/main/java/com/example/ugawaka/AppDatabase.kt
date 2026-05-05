package com.example.ugawaka

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderDao {
    @Query("SELECT * FROM providers")
    fun getAllProviders(): Flow<List<Provider>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<Provider>): List<Long>

    @Query("SELECT COUNT(*) FROM providers")
    suspend fun getCount(): Int
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE providerId = :providerId")
    fun getReviewsForProvider(providerId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>): List<Long>
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE email = :email LIMIT 1")
    suspend fun getAccountByEmail(email: String): DemoAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: DemoAccount): Long
    
    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<DemoAccount>): List<Long>
}

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    fun getBookingsForUser(userId: String): Flow<List<Booking>>
}

@Database(entities = [Provider::class, Review::class, Booking::class, DemoAccount::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun providerDao(): ProviderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun accountDao(): AccountDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ugawaka_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
