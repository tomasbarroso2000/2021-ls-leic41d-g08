package pt.isel.ls.sports.services.comp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.ls.sports.data.Data
import pt.isel.ls.sports.data.comp.DataException
import pt.isel.ls.sports.data.comp.transactions.Transaction
import java.sql.SQLException
import java.util.regex.Pattern

private const val UNIQUE_VIOLATION = "23505"
private const val CHECK_VIOLATION = "23514"

val logger: Logger = LoggerFactory.getLogger("pt.isel.ls.sports.services.comp.ServicesUtils")

/**
 * Executes a service with the necessary catch block
 * @param function the function to be executed
 * @return the value returned by function
 */
fun <T> doService(function: () -> T): T {
    try {
        return function()
    } catch (error: Exception) {
        if (error is AppException) throw error
        else throw handleDataError(error)
    }
}

/**
 * Executes a function within a [Transaction]
 * @param transaction the [Transaction] to be used
 * @param function the code to be executed
 * @return the value returned by function
 */
fun <T> executeTransaction(transaction: Transaction, function: (t: Transaction) -> T): T {
    transaction.setAutocommit(false)
    try {
        val res = function(transaction)
        transaction.commit()
        return res
    } catch (error: Exception) {
        transaction.rollback()
        throw error
    } finally {
        transaction.closeConnection()
    }
}

/**
 * Represents an app exception
 * @property message the exception message
 * @property status the status of the exception
 */
data class AppException(
    override val message: String? = null,
    val status: AppExceptionStatus = AppExceptionStatus.INTERNAL
) : Exception() {
    init {
        logger.warn(message)
    }
}

/**
 * Represents an app exception status
 * used to later convert the exception to
 * the correct HTTP response
 */
enum class AppExceptionStatus {
    UNAUTHORIZED,
    BAD_REQUEST,
    NOT_FOUND,
    INTERNAL
}

/**
 * Sends the token to getUserNumberByToken in the data module
 * @param token the token linked to the user
 * @param data the data module
 * @return [Int] the number of the user
 */
fun getUserNumber(transaction: Transaction, token: String?, data: Data): Int {
    if (token == null || token == "") throw AppException("No token provided", AppExceptionStatus.UNAUTHORIZED)
    return data.usersData.getUserNumberByToken(transaction, token)
        ?: throw AppException("Invalid token", AppExceptionStatus.UNAUTHORIZED)
}

/**
 * Transforms a data exception into an [AppException]
 * for it to be interpreted by the WebApi module
 * @param error the [Exception] that was thrown
 * @return the new [AppException]
 */
fun handleDataError(error: Exception): Exception {
    return when (error) {
        is SQLException -> {
            handleSQLException(error)
        }
        is DataException -> {
            AppException(error.message, AppExceptionStatus.BAD_REQUEST)
        }
        else -> {
            logger.warn(error.message)
            error
        }
    }
}

/**
 * Transforms a SQL exception into an [AppException]
 * for it to be interpreted by the WebApi module
 * @param error the [SQLException] that was thrown
 * @return the new [AppException]
 */
fun handleSQLException(error: SQLException): Exception {
    return when {
        isUniqueViolation(error) -> {
            AppException(buildUniqueViolationMessage(error.message), AppExceptionStatus.BAD_REQUEST)
        }
        isCheckViolation(error) -> {
            AppException(buildCheckViolationMessage(error.message), AppExceptionStatus.BAD_REQUEST)
        }
        else -> {
            AppException("Something went wrong", AppExceptionStatus.INTERNAL)
        }
    }
}

/**
 * Checks if [SQLException] represents
 * a unique violation in the database
 * @param error the [SQLException] that was thrown
 * @return true if it is a unique violation
 */
fun isUniqueViolation(error: SQLException) =
    error.sqlState == UNIQUE_VIOLATION

/**
 * Checks if [SQLException] represents
 * a check violation in the database
 * @param error the [SQLException] that was thrown
 * @return true if it is a check violation
 */
fun isCheckViolation(error: SQLException) =
    error.sqlState == CHECK_VIOLATION

/**
 * Builds an error message for
 * unique violation errors
 * @param message the original [SQLException] message
 * @return the new error message
 */
fun buildUniqueViolationMessage(message: String?): String? {
    val detail = message?.split("Detail: ")?.get(1) ?: return message
    val column = detail.substringAfter('(').substringBefore(')')
    val value = detail.substringAfterLast('(').substringBefore(')')
    return "The $column $value is already in use"
}

/**
 * Builds an error message for
 * check violation errors
 * @param message the original [SQLException] message
 * @return the new error message
 */
fun buildCheckViolationMessage(message: String?): String? {
    val column = message?.substringAfter('_')?.substringBeforeLast('_') ?: return message
    return "Invalid $column"
}

/**
 * Check if the email address is valid
 */
fun isEmailValid(email: String): Boolean {
    return Pattern.compile("^(.+)@(\\S+)$")
        .matcher(email)
        .matches()
}

/**
 * Checks if the limit and skip values are valid and
 * throws an exception they are not
 * @param limit the given limit
 * @param skip the given skip
 */
fun checkLimitAndSkip(limit: Int, skip: Int) {
    if (limit <= 0)
        throw AppException("Invalid limit", AppExceptionStatus.BAD_REQUEST)
    if (skip < 0)
        throw AppException("Invalid skip", AppExceptionStatus.BAD_REQUEST)
}
