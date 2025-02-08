package com.sandymist.android.debuglib.db

import timber.log.Timber

fun getRowLimitTrigger(
    tableName: String,
    selectField: String,
    orderByField: String,
    maxEntries: Int,
): String {
    val triggerName = "${tableName}_row_limit_trigger"

    Timber.d("Row limit trigger $triggerName on $tableName, max: $maxEntries")
    return """
        CREATE TRIGGER IF NOT EXISTS $triggerName
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
