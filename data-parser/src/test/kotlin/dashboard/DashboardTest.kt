package dashboard

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class DashboardTest {

    private val gson = Gson()

    @Test
    fun json() {
        val regionB = getBerlinRegion()
        val regionR = getRuhrgebietRegion()
        val dashboard = Dashboard(listOf(regionB, regionR), "TestDate")
        val jsonString = gson.toJson(dashboard)
        println(jsonString)
        val dashboard2 = gson.fromJson(jsonString, Dashboard::class.java)
        assertEquals(dashboard, dashboard2)
    }

    private fun getBerlinRegion(): Region {
        return Region("Berlin", listOf(1300, 400), listOf(270, 21), listOf(74, 3), listOf(4800, 729))
    }

    private fun getRuhrgebietRegion(): Region {
        return Region("Ruhrgebiet", listOf(140021, 14320), listOf(5720), listOf(111), listOf(1240921, 1495))
    }
}