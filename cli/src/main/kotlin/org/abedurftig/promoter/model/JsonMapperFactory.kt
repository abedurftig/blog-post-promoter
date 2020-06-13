package org.abedurftig.promoter.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonMapperFactory {

    private var gson = getMapper()

    fun getGson() = gson

    private fun getMapper(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter().nullSafe())
            .create()
    }
}

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(out: JsonWriter, value: LocalDateTime) {
        out.value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value))
    }

    override fun read(input: JsonReader): LocalDateTime = LocalDateTime.parse(input.nextString())
}
