package com.sandymist.android.debuglib.db

import timber.log.Timber

fun getRowLimitTrigger(
    tableName: String,
    selectField: String,
    orderByField: String,
    maxEntries: Int,
): String {
    Timber.d("Row limit trigger on $tableName, max: $maxEntries")
    return """
        CREATE TRIGGER IF NOT EXISTS limit_rows
        AFTER INSERT ON $tableName
        WHEN (SELECT COUNT(*) FROM $tableName) > $maxEntries
        BEGIN
            DELETE FROM $tableName 
            WHERE $selectField IN (
                SELECT $selectField 
                FROM $tableName 
                ORDER BY $orderByField ASC 
                LIMIT (SELECT COUNT(*) - $maxEntries FROM $tableName)
            );
        END;
    """.trimIndent()
}
