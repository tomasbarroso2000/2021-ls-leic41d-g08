package pt.isel.ls.sports.data.comp.transactions

class MemTransaction : Transaction {
    override fun setAutocommit(value: Boolean) { return }
    override fun commit() { return }
    override fun rollback() { return }
    override fun closeConnection() { return }
}
