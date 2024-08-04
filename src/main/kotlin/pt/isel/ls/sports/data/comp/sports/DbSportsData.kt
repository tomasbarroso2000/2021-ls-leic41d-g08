package pt.isel.ls.sports.data.comp.sports

import pt.isel.ls.sports.data.comp.toSport
import pt.isel.ls.sports.data.comp.transactions.DbTransaction
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Sport
import pt.isel.ls.sports.domain.SportUpdateInput
import java.sql.Statement

class DbSportsData : SportsData {

    override fun addSport(transaction: Transaction, userNumber: Int, name: String, description: String?): DataOutput? {
        var sportOutput: DataOutput? = null
        (transaction as DbTransaction).withConnection {
            val pstmtSport =
                it.prepareStatement(
                    "insert into sports (name, description, user_number) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
            pstmtSport.setString(1, name)
            pstmtSport.setString(2, description)
            pstmtSport.setInt(3, userNumber)
            pstmtSport.executeUpdate()

            val generatedKeys = pstmtSport.generatedKeys
            if (generatedKeys.next()) {
                val sportNumber = generatedKeys.getInt("number")
                sportOutput = DataOutput(sportNumber)
            }
        }
        return sportOutput
    }

    override fun getSports(transaction: Transaction, limit: Int, skip: Int): ListOfData<Sport> {
        val sports = mutableListOf<Sport>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select sports.number, sports.name, sports.description, sports.user_number, users.name as user_name " +
                    "from sports join users on sports.user_number = users.number order by sports.number asc offset ? limit ?"
            )
            select.setInt(1, skip)
            select.setInt(2, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit) {
                    sports.add(rs.toSport())
                } else
                    hasMore = true
            }
        }
        return ListOfData(sports, hasMore)
    }

    override fun getSportByNumber(transaction: Transaction, sportNumber: Int): Sport? {
        var sport: Sport? = null
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select sports.number, sports.name, sports.description, sports.user_number, users.name as user_name " +
                    "from sports join users on sports.user_number = users.number where sports.number = ?"
            )
            select.setInt(1, sportNumber)
            val rs = select.executeQuery()
            if (rs.next()) {
                sport = rs.toSport()
            }
        }
        return sport
    }

    override fun updateSport(transaction: Transaction, sportNumber: Int, updates: SportUpdateInput): DataOutput {
        (transaction as DbTransaction).withConnection {
            val update = it.prepareStatement(buildQueryStringForUpdateSport(updates))
            var curr = 1
            if (updates.name != null) {
                update.setString(curr, updates.name)
                curr++
            }
            if (updates.description != null) {
                update.setString(curr, updates.description)
                curr++
            }
            update.setInt(curr, sportNumber)
            update.executeUpdate()
        }
        return DataOutput(sportNumber)
    }

    /**
     * Builds the query string depending on the nullability of the name and description
     * @param updates the updated attributes of the sport
     * @return [String] the query string
     */
    private fun buildQueryStringForUpdateSport(updates: SportUpdateInput): String {
        var queryString = "update sports set "
        updates.name?.let { queryString += "name = ?, " }
        updates.description?.let { queryString += "description = ?" }
        queryString = queryString.removeSuffix(", ")
        queryString += " where number = ?"
        return queryString
    }

    override fun searchSports(transaction: Transaction, searchQuery: String, limit: Int, skip: Int): ListOfData<Sport> {
        val sports = mutableListOf<Sport>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select =
                it.prepareStatement(
                    "select sports.number, sports.name, sports.description, sports.user_number, users.name as user_name " +
                        "from sports join users on sports.user_number = users.number where " +
                        "lower (sports.name) like lower ('%' || ? || '%') or " +
                        "lower (sports.description) like lower ('%' || ? || '%') " +
                        "order by sports.number asc " +
                        "offset ? limit ?"
                )
            select.setString(1, searchQuery)
            select.setString(2, searchQuery)
            select.setInt(3, skip)
            select.setInt(4, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit)
                    sports.add(rs.toSport())
                else
                    hasMore = true
            }
        }
        return ListOfData(sports, hasMore)
    }
}
