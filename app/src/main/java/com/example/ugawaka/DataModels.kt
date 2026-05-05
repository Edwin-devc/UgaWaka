package com.example.ugawaka

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Brush
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

// DATA MODELS (Encapsulation)
@Entity(tableName = "providers")
data class Provider(
    @PrimaryKey val id: String,
    val name: String,
    val categoryName: String,
    val rating: Double,
    val distance: String,
    val price: String,
    val bio: String,
    val location: String,
    val experience: String,
    val jobsCompleted: Int
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey val id: String,
    val providerId: String,
    val reviewerName: String,
    val rating: Int,
    val comment: String
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val id: String,
    val providerId: String,
    val userId: String,
    val serviceName: String,
    val status: String, // Pending, Confirmed, Completed
    val date: String
)

data class ServiceCategoryData(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Entity(tableName = "accounts")
data class DemoAccount(
    @PrimaryKey val id: String, 
    val name: String, 
    val email: String,
    val role: String // "Client" or "Provider"
)

data class ServicesUiState(
    val categories: List<ServiceCategoryData> = emptyList(),
    val providers: List<Provider> = emptyList(),
    val isLoading: Boolean = false,
    val bookingStatus: String? = null
)

// Sample data for initial population
val sampleCategories = listOf(
    ServiceCategoryData("Plumbing", Icons.Filled.Build, Color(0xFF2196F3)),
    ServiceCategoryData("Electrical", Icons.Filled.ElectricBolt, Color(0xFFFFC107)),
    ServiceCategoryData("Cleaning", Icons.Filled.CleaningServices, Color(0xFF4CAF50)),
    ServiceCategoryData("Mechanics", Icons.Filled.CarRepair, Color(0xFFF44336)),
    ServiceCategoryData("Catering", Icons.Filled.Restaurant, Color(0xFFFF9800)),
    ServiceCategoryData("Tutoring", Icons.Filled.School, Color(0xFF9C27B0)),
    ServiceCategoryData("Delivery", Icons.Filled.LocalShipping, Color(0xFF00BCD4)),
    ServiceCategoryData("Painting", Icons.Filled.Brush, Color(0xFFE91E63))
)

val sampleProviders = listOf(
    Provider("1", "Ssali Peter Joshua", "Plumbing", 4.9, "1.2 km", "UGX 50,000", "Professional plumber with over 5 years of experience in Kampala.", "Nakasero, Kampala", "5 yrs", 250),
    Provider("2", "Namuganza Isabella Regina", "Plumbing", 4.8, "2.1 km", "UGX 45,000", "Experienced electrician providing reliable electrical services.", "Kololo, Kampala", "4 yrs", 180),
    Provider("3", "Nabwire Vivian Rachael", "Cleaning", 4.7, "3.5 km", "UGX 35,000", "Professional cleaning services for homes and offices.", "Wandegeya, Kampala", "3 yrs", 120)
)

val sampleReviews = listOf(
    Review("1", "1", "KAYEERA NATHAN", 5, "Excellent work!"),
    Review("2", "1", "Rwakasiisi Edwin", 4, "Good service."),
    Review("3", "1", "KAYEERA NATHAN", 5, "Very skilled.")
)

val clientAccounts = listOf(
    DemoAccount("kayeera", "KAYEERA NATHAN", "kayeera.nathan@ugawaka.app", "Client"),
    DemoAccount("rwakasiisi", "Rwakasiisi Edwin", "rwakasiisi.edwin@ugawaka.app", "Client")
)

val providerAccounts = listOf(
    DemoAccount("ssali", "Ssali Peter Joshua", "ssali.peter@ugawaka.app", "Provider"),
    DemoAccount("namuganza", "Namuganza Isabella Regina", "namuganza.isabella@ugawaka.app", "Provider"),
    DemoAccount("nabwire", "Nabwire Vivian Rachael", "nabwire.vivian@ugawaka.app", "Provider")
)

// OOP PRINCIPLE: Abstraction (Interface)
interface IProviderRepository {
    fun getCategories(): List<ServiceCategoryData>
    fun getAllProviders(): Flow<List<Provider>>
    fun getReviewsForProvider(providerId: String): Flow<List<Review>>
    suspend fun initializeData()
    suspend fun getAccountByEmail(email: String): DemoAccount?
    suspend fun insertAccount(account: DemoAccount)
    suspend fun insertBooking(booking: Booking)
    suspend fun insertReview(review: Review)
    suspend fun insertProvider(provider: Provider)
}

// Implementation using Room
class ProviderRepository(private val db: AppDatabase) : IProviderRepository {
    override fun getCategories(): List<ServiceCategoryData> = sampleCategories

    override fun getAllProviders(): Flow<List<Provider>> = db.providerDao().getAllProviders()

    override fun getReviewsForProvider(providerId: String): Flow<List<Review>> =
        db.reviewDao().getReviewsForProvider(providerId)

    override suspend fun initializeData() {
        if (db.providerDao().getCount() == 0) {
            db.providerDao().insertProviders(sampleProviders)
            db.reviewDao().insertReviews(sampleReviews)
        }
        if (db.accountDao().getCount() == 0) {
            db.accountDao().insertAccounts(clientAccounts + providerAccounts)
        }
    }

    override suspend fun getAccountByEmail(email: String): DemoAccount? =
        db.accountDao().getAccountByEmail(email)

    override suspend fun insertAccount(account: DemoAccount) {
        db.accountDao().insertAccount(account)
    }

    override suspend fun insertBooking(booking: Booking) {
        db.bookingDao().insertBooking(booking)
    }

    override suspend fun insertReview(review: Review) {
        db.reviewDao().insertReviews(listOf(review))
    }

    override suspend fun insertProvider(provider: Provider) {
        db.providerDao().insertProviders(listOf(provider))
    }
}
