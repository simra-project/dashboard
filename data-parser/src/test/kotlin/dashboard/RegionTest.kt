package dashboard

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class RegionTest {

    private val gson = Gson()

    @Test
    fun json() {
        val region = getBerlinRegion()
        val jsonString = gson.toJson(region)
        println(jsonString)
        val region2 = gson.fromJson(jsonString, Region::class.java)
        assertEquals(region, region2)
    }

    private fun getBerlinRegion(): Region {
        return Region("Berlin", listOf(1300, 400), listOf(270, 21), listOf(74, 3), listOf(4800, 729), "https://www.google.com")
    }
}