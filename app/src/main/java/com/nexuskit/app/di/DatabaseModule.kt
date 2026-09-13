package com.nexuskit.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.data.database.NexusKitDatabase
import com.nexuskit.app.data.database.dao.NoteDao
import com.nexuskit.app.data.database.dao.ToolHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `notes` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `title` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                `is_pinned` INTEGER NOT NULL DEFAULT 0,
                `color_tag` INTEGER NOT NULL DEFAULT 0,
                `created_at` INTEGER NOT NULL,
                `updated_at` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_is_pinned` ON `notes` (`is_pinned`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_updated_at` ON `notes` (`updated_at`)")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NexusKitDatabase {
        return Room.databaseBuilder(
            context,
            NexusKitDatabase::class.java,
            AppConstants.APP_DATABASE_NAME
        )
        .addMigrations(MIGRATION_1_2)
        .build()
    }

    @Provides
    @Singleton
    fun provideToolHistoryDao(db: NexusKitDatabase): ToolHistoryDao =
        db.toolHistoryDao()

    @Provides
    @Singleton
    fun provideNoteDao(db: NexusKitDatabase): NoteDao =
        db.noteDao()
}
