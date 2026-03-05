package db

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.jdbc.statements.toExecutable
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

/**
 * Fungerer kun med PostgreSQL.
 */
fun <T : Table> T.upsert(
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(InsertStatement<Number>) -> Unit,
) = InsertOrUpdate<Number>(this, keys = keys).apply {
    body(this)
}.toExecutable().execute(TransactionManager.current())

class InsertOrUpdate<Key : Any>(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>,
) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(
        transaction: Transaction,
        prepared: Boolean,
    ): String {
        val tm = TransactionManager.current()
        val updateSetter =
            (table.columns - keys.toSet()).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction, prepared)} $onConflict"
    }
}
