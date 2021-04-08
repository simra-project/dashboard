package dashboard

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()
//private val gson = GsonBuilder().setPrettyPrinting().create();
private val gson = Gson()

data class Dashboard(val regions: List<Region>) {

    fun saveDashboardJson(dashboardFile: File) {
        dashboardFile.writeText(gson.toJson(this))
        logger.info("Saved dashboard json to ${dashboardFile.absolutePath}")
    }

}
