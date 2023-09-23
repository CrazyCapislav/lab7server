package models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.sql.*

@JacksonXmlRootElement(localName = "user")
data class User(
    @JacksonXmlProperty(localName = "username")
    val username: String,
    @JacksonXmlProperty(localName = "password")
    val password: String)

fun registerUser(connection: Connection, user: User): RegistrationResult {
    val username = user.username
    val passwordHash = hashPassword(user.password)

    if (isUsernameTaken(connection, username)) {
        return RegistrationResult.UsernameTaken
    }

    val sql = "INSERT INTO users (username, password_hash, admin) VALUES (?, ?, ?)"
    try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, username)
        preparedStatement.setString(2, passwordHash)
        preparedStatement.setBoolean(3, false)
        preparedStatement.executeUpdate()
        preparedStatement.close()
        return RegistrationResult.Success
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    return RegistrationResult.Error
}
fun authenticateUser(username: String, passwordHash: String): Boolean {
    val dbName = "studs"
    val sshHost = "helios.cs.ifmo.ru"
    val sshPort = 2222
    val sshPassword = "KKpk$1840"
    val dbUser = "s354511"
    val sshUser = "s354511"
    val dbPassword = "VxWWFhamaK8hHcft"
    val dbHost = "localhost"
    val dbPort = 5432
    val jsch = JSch()
    val session: Session = jsch.getSession(sshUser, sshHost, sshPort)
    session.setPassword(sshPassword)
    session.setConfig("StrictHostKeyChecking", "no")
    session.connect()
    val assignedPort = session.setPortForwardingL(0, dbHost, dbPort)
    val dbUrl = "jdbc:postgresql://localhost:$assignedPort/$dbName"
    try {
        val connection: Connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)

        val sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?"
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, username)
        preparedStatement.setString(2, passwordHash)

        val resultSet: ResultSet = preparedStatement.executeQuery()

        if (resultSet.next()) {
            resultSet.close()
            preparedStatement.close()
            connection.close()
            return true
        }

        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }

    return false
}

fun isUsernameTaken(connection: Connection, username: String): Boolean {
    val sql = "SELECT * FROM users WHERE username = ?"
    try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, username)
        val resultSet: ResultSet = preparedStatement.executeQuery()

        val isTaken = resultSet.next()
        resultSet.close()
        preparedStatement.close()
        return isTaken
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    return false
}

sealed class RegistrationResult {
    data object Success : RegistrationResult()
    data object UsernameTaken : RegistrationResult()
    data object Error : RegistrationResult()
}

fun hashPassword(password: String): String {
    return password
}

fun isAdmin(connection: Connection, username: String): Boolean {
    val sql = "SELECT admin FROM users WHERE username = ?"
    try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, username)
        val resultSet: ResultSet = preparedStatement.executeQuery()

        if (resultSet.next()) {
            val isAdmin = resultSet.getBoolean("admin")
            resultSet.close()
            preparedStatement.close()
            return isAdmin
        }

        resultSet.close()
        preparedStatement.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }

    return false
}