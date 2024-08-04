package pt.isel.ls.sports.data.comp.transactions

import java.sql.Connection

class DbTransaction(private val connection: Connection) : Transaction {

    /**
     * Execute a task using the transaction's connection
     * @param function the task that will be executed
     */
    fun withConnection(function: (c: Connection) -> Unit) = function(connection)

    override fun setAutocommit(value: Boolean) { connection.autoCommit = value }
    override fun commit() { connection.commit() }
    override fun rollback() { connection.rollback() }
    override fun closeConnection() { connection.close() }
}
