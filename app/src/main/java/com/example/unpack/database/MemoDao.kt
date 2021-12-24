package com.example.unpack.database

import androidx.room.*

// DAOには @Dao をつける
@Dao
interface MemoDao {
    // @Queryを使うと実行SQLを定義することができる
    @Query("SELECT * from memo")
    fun getAllMemos(): List<Memo>

    // SQLに引数を渡す場合は 「:<引数名>」 で関連付ける
    @Query("SELECT * from memo where id = :id LIMIT 1")
    fun findById(id: String): Memo

    // SQLに引数を渡す場合は 「:<引数名>」 で関連付ける
    @Query("DELETE from memo where id = :id")
    fun deleteById(id: Int): Int

    // @Insertなど用意されたアノテーションをつけると引数にEntityを渡すだけで処理ができる
    @Insert
    fun insert(memo: Memo)

    @Update
    fun update(memo: Memo)

    @Delete
    fun delete(memo: Memo)
}