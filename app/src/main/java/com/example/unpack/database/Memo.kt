package com.example.unpack.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo(
    // 主キーには @PrimaryKey をつける
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    // カラムには @ColumnInfo をつける
    @ColumnInfo val text: String,
    // カラムには @ColumnInfo をつける
    @ColumnInfo val updateDate: String ,
)
