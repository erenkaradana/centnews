package com.tudio.appcentnews.database

import androidx.room.TypeConverter
import com.tudio.appcentnews.models.Source

// Room veritabanında source nesnesini String'e ve String'i source nesnesine dönüştüren dönüştürücü sınıf.
// (Source nesnesinin name özelliğine string olarak erişebilmek için gerekli.)
class Converters {
    @TypeConverter
    fun fromSource(source: Source): String? {
        return source?.name
    }
    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}