package pt.isel.ls.sports.data.comp.transactions

/**
 * Represents a transaction that can be requested
 * by the services module to execute sequential tasks
 * on the data without using multiple connections
 */
interface Transaction {

    /**
     * Change autocommit value
     * @param value new autocommit value
     */
    fun setAutocommit(value: Boolean)

    /**
     * Commit a transaction
     */
    fun commit()

    /**
     * Rollback a transaction
     */
    fun rollback()

    /**
     * Close a connection
     */
    fun closeConnection()
}
