package pt.isel.ls.sports.data.comp.users

import pt.isel.ls.sports.data.comp.toSession
import pt.isel.ls.sports.data.comp.toUser
import pt.isel.ls.sports.data.comp.transactions.DbTransaction
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Session
import pt.isel.ls.sports.domain.User
import pt.isel.ls.sports.domain.UserOutput
import java.sql.Statement
import java.util.UUID

class DbUsersData : UsersData {

    override fun addUser(transaction: Transaction, name: String, email: String, password: String): UserOutput? {
        var userOutput: UserOutput? = null
        (transaction as DbTransaction).withConnection {
            val pstmtUser =
                it.prepareStatement(
                    "insert into users (name, email, password, token) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
            val token = UUID.randomUUID().toString()
            pstmtUser.setString(1, name)
            pstmtUser.setString(2, email)
            pstmtUser.setInt(3, password.hashCode())
            pstmtUser.setString(4, token)
            pstmtUser.executeUpdate()

            val generatedKeys = pstmtUser.generatedKeys
            if (generatedKeys.next()) {
                val userNumber = generatedKeys.getInt("number")
                userOutput = UserOutput(token, userNumber)
            }
        }
        return userOutput
    }

    override fun getUserByNumber(transaction: Transaction, userNumber: Int): User? {
        var user: User? = null
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement("select * from users where number = ?")
            select.setInt(1, userNumber)
            val rs = select.executeQuery()
            if (rs.next()) {
                user = rs.toUser()
            }
        }
        return user
    }

    override fun getUserNumberByToken(transaction: Transaction, token: String?): Int? {
        var userNumber: Int? = null
        (transaction as DbTransaction).withConnection {
            val pstmt = it.prepareStatement("select number from users where token = ?")
            pstmt.setString(1, token)
            val rs = pstmt.executeQuery()
            if (rs.next())
                userNumber = rs.getInt("number")
        }
        return userNumber
    }

    override fun getUsers(transaction: Transaction, limit: Int, skip: Int): ListOfData<User> {
        val users = mutableListOf<User>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement("select * from users order by number asc offset ? limit ?")
            select.setInt(1, skip)
            select.setInt(2, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit)
                    users.add(rs.toUser())
                else
                    hasMore = true
            }
        }
        return ListOfData(users, hasMore)
    }

    override fun getUserRankings(transaction: Transaction, sportNumber: Int, routeNumber: Int, limit: Int, skip: Int): ListOfData<User> {
        val creators = mutableListOf<User>()
        var hasMore = false
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select number, name, email from (" +
                    "select distinct on (number) user_number as number, duration " +
                    "from activities " +
                    "where sport_number = ? and route_number = ? " +
                    ") activities natural join users u " +
                    "order by duration desc " +
                    "offset ? limit ?"
            )
            select.setInt(1, sportNumber)
            select.setInt(2, routeNumber)
            select.setInt(3, skip)
            select.setInt(4, limit + 1)
            val rs = select.executeQuery()
            var found = 0
            while (rs.next()) {
                found++
                if (found <= limit)
                    creators.add(rs.toUser())
                else hasMore = true
            }
        }
        return ListOfData(creators, hasMore)
    }

    override fun getUserToken(transaction: Transaction, email: String, password: String): Session? {
        var session: Session? = null
        (transaction as DbTransaction).withConnection {
            val select = it.prepareStatement(
                "select number, name, token from users where email = ? and password = ?"
            )
            select.setString(1, email)
            select.setInt(2, password.hashCode())
            val rs = select.executeQuery()
            if (rs.next()) {
                session = rs.toSession()
            }
        }
        return session
    }
}
