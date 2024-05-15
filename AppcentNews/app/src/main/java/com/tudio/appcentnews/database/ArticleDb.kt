package com.tudio.appcentnews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tudio.appcentnews.models.Article

// Room veritabanı için veri tabanı sınıfını tanımlar.
@Database( entities = [Article::class], version = 1)

@TypeConverters(Converters::class)


abstract class ArticleDb: RoomDatabase() {

    // Veritabanından alınacak veriler için Data Access Object'i (DAO) döndürür.
    abstract fun getArticleDao(): DataAccess

    companion object{
        @Volatile
        private var instance: ArticleDb? = null
        private val LOCK = Any()

        // Hazır veritabanını döndürür.
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also {
                // instance hala null ise, veritabanını oluştur ve instance'a atar.
                instance = it
            }
        }
        // Veritabanı oluşturur.
        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDb::class.java,
            "article_db.db"
        ).build()

    }
}