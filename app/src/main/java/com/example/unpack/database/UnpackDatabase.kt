package com.example.unpack.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// entities には、関連付けるテーブルのエンティティを渡す
@Database(entities = [Memo::class], version = 1, exportSchema = false)
abstract class UnpackDatabase: RoomDatabase() {

    abstract fun memoDao(): MemoDao

    companion object {
        // シングルトンで使えるようにする
        @Volatile
        private var instance: UnpackDatabase? = null

        // データベースインスタンスは、マルチスレッドで参照される可能性があるため synchronized でアクセス
        fun getInstance(context: Context): UnpackDatabase =
            instance ?: synchronized(this) {
                // データベースインスタンスは、databaseBuilderを使って作成
                Room.databaseBuilder(context, UnpackDatabase::class.java, "thchbooster.db").build()
            }
    }
}