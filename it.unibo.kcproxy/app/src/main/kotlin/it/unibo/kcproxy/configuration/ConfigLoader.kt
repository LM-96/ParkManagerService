package it.unibo.kcproxy.configuration

import com.diogonunes.jcolor.AnsiFormat
import com.diogonunes.jcolor.Attribute
import com.google.gson.Gson
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.toList

object ConfigLoader {

    @JvmStatic private val configs = mutableMapOf<String, ConfigEntry>()
    @JvmStatic private val CONFIG_FILE = "configs/config.json"
    @JvmStatic private val cyan = AnsiFormat(Attribute.CYAN_TEXT())
    @JvmStatic private val red = AnsiFormat(Attribute.RED_TEXT())

    @JvmStatic fun load() : ConfigLoader {
        configs.clear()

        var configFile = Paths.get(CONFIG_FILE)
        if(!Files.exists(configFile)) {
            println(red.format("ConfigLoader | Unable to find configuration file at ${configFile.toAbsolutePath()}"))
            System.exit(-1)
        }

        try {
            val gson = Gson()
            Files.lines(configFile)
                .map { gson.fromJson<ConfigEntry>(it, ConfigEntry::class.java) }
                .forEach {
                    configs.put(it.resource, it)
                    println(cyan.format("ConfigLoader | Loaded configuration entry [$it]"))
                }
        } catch (e : Exception) {
            println(red.format("ConfigLoader | Error while reading configuration file at ${configFile.toAbsolutePath()}"))
            e.printStackTrace()
            System.exit(-1)
        }

        return this
    }

    @JvmStatic fun getEntry(resource : String) : ConfigEntry? {
        return configs.get(resource)
    }

    @JvmStatic fun getEntries() : Collection<ConfigEntry> {
        return Collections.unmodifiableCollection(configs.values)
    }

    @JvmStatic fun getIterator() : Iterator<ConfigEntry> {
        return configs.values.iterator()
    }

}