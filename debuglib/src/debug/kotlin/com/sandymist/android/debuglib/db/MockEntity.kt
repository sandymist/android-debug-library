package com.sandymist.android.debuglib.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sandymist.android.debuglib.model.MockItem

const val MOCK_TABLE_NAME = "mocks"

@Entity(tableName = MOCK_TABLE_NAME)
data class MockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val method: String,
    val path: String,
    val body: String,
    val code: Int,
    val createdAt: Long,
) {
    fun toMockItem(): MockItem {
        return MockItem(
            method = method,
            path = path,
            body = body,
            code = code,
            createdAt = createdAt,
        )
    }

    companion object {
        fun fromMockItem(mockItem: MockItem): MockEntity {
            return MockEntity(
                method = mockItem.method,
                path = mockItem.path,
                body = mockItem.body,
                code = mockItem.code,
                createdAt = mockItem.createdAt,
            )
        }
    }
}
