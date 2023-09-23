import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import commands.*
import models.*
import java.io.*
import java.net.ServerSocket
import java.net.SocketException
import java.sql.*
import java.util.logging.Logger

class Server(private val port: Int) {
    private val logger: Logger = Logger.getLogger(Server::class.java.name)
    private val xmlMapper = XmlMapper()

    fun start() {
        logger.info("Запуск сервера на порту $port...")
        val serverSocket = ServerSocket(port)
        val sshHost = "helios.cs.ifmo.ru"
        val sshPort = 2222
        val sshPassword = "KKpk$1840"
        val sshUser = "s354511"
        val dbHost = "localhost"
        val dbPort = 5432
        val dbName = "studs"
        val dbUser = "s354511"
        val dbPassword = "VxWWFhamaK8hHcft"
        val jsch = JSch()
        val session: Session = jsch.getSession(sshUser, sshHost, sshPort)
        session.setPassword(sshPassword)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect()
        val assignedPort = session.setPortForwardingL(0, dbHost, dbPort)
        val dbUrl = "jdbc:postgresql://localhost:$assignedPort/$dbName"

        var flatsSet: HashSet<Flat>
        try {
            val connection: Connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
            logger.info("Успешное подключение к базе данных")
            val show = Show()
            val flatsSet1 = show.getHashSet(connection)
            logger.info("${flatsSet1.size} элементов добавлено в HashSet flatsSet.")

        } catch (e: SQLException) {
            logger.warning("Ошибка подключения к базе данных: ${e.message}")
        }
        while (true) {
            val clientSocket = serverSocket.accept()
            logger.info("Новое подключение: ${clientSocket.inetAddress.hostAddress}")

            Thread {
                val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                val writer = PrintWriter(clientSocket.getOutputStream(), true)

                var authenticated = false
                while (!authenticated){
                    val request = reader.readLine() ?: break
                    val xmlMapper2 = XmlMapper()
                    val requestObj = xmlMapper2.readValue<Command>(request)
                    val connection: Connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
                    logger.info("Получен запрос от клиента: $request")

                    when (requestObj){
                        is Auto -> {
                            val requestUser = reader.readLine()
                            println("1")
                            val xmlMapper3 = XmlMapper()
                            val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                            println("2")
                            val response: String
                            if (authenticateUser(requestUser1.username, requestUser1.password)){
                                response = "true"
                                authenticated = true
                            }
                            else{
                                response = "false"
                            }
                            val message = Message(response)
                            val xmlResponse = message.toXml()
                            parseMessage(xmlResponse)
                            writer.println(xmlResponse)
                        }
                        is Register -> {
                            println()
                            val requestUser = reader.readLine()
                            val xmlMapper3 = XmlMapper()
                            val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                            registerUser(connection, requestUser1)
                            val response: String
                            if (authenticateUser(requestUser1.username, requestUser1.password)){
                                response = "true"
                                authenticated = true
                            }
                            else {
                                response = "false"
                            }
                            val message = Message(response)
                            val xmlResponse = message.toXml()
                            parseMessage(xmlResponse)
                            writer.println(xmlResponse)
                        }
                    }
                }

                try{
                    while (authenticated) {
                        var response = ""
                        val request = reader.readLine() ?: break
                        logger.info("Получен запрос от клиента: $request")
                        val xmlMapper2 = XmlMapper()
                        val requestObj = xmlMapper2.readValue<Command>(request)
                        val connection: Connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
                        val show = Show()
                        val flats = show.getHashSet(connection)

                        when (requestObj) {
                            is Show -> {
                                response = requestObj.showString(flats)
                            }
                            is Help -> {
                                response = requestObj.toString()
                            }
                            is AverageOfTimeToMetroOnFoot -> {
                                response = requestObj.averageMetro(flats)
                            }
                            is Add -> {
                                val requestFlat = reader.readLine()
                                val xmlMapper3 = XmlMapper()
                                val requestFlat1 = xmlMapper3.readValue<Flat>(requestFlat)
                                println( requestFlat1.toString())
                                val requestUser = reader.readLine()
                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                                val add = Add()
                                add.execute(connection, requestFlat1, requestUser1.username)
                                flatsSet = show.getHashSet(connection)
                                response = "Объект добавлен"

                            }
                            is RemoveById -> {
                                val requestId = reader.readLine()
                                val xmlMapper3 = XmlMapper()
                                val id = xmlMapper3.readValue(requestId, String::class.java).toLong()
                                val requestUser = reader.readLine()
                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                                if (doesIdExist(connection, id)){
                                    val rm = RemoveById()
                                    if (isAdmin(connection,requestUser1.username)){
                                        rm.removeById(connection,id)
                                        flatsSet = show.getHashSet(connection)
                                        response = "Объект удален"
                                    }
                                    else {
                                        rm.removeByIdAndUsername(connection,id, requestUser1.username)
                                        flatsSet = show.getHashSet(connection)
                                        response = "Объект удален"
                                    }
                                }
                                else {
                                    response = "Id не существует"
                                }
                            }

                            is Clear -> {
                                val requestUser = reader.readLine()
                                val xmlMapper3 = XmlMapper()
                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                                val clear = Clear()
                                if (isAdmin(connection,requestUser1.username)){
                                    clear.execute(connection)
                                    flatsSet = show.getHashSet(connection)
                                    response = "Коллекция очищена"
                                }
                                else {
                                    clear.deleteFlatsByUser(connection, requestUser1.username)
                                    flatsSet = show.getHashSet(connection)
                                    response = "Коллекция пользователя ${requestUser1.username} очищена"
                                }
                            }
                            is RemoveLower -> {
                                val requestId = reader.readLine()
                                val xmlMapper3 = XmlMapper()
                                val id = xmlMapper3.readValue(requestId, String::class.java).toLong()
                                val requestUser = reader.readLine()
                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                                val removeLower = RemoveLower()
                                if (isAdmin(connection,requestUser1.username)){
                                    removeLower.removeLower(connection, id)
                                    flatsSet = show.getHashSet(connection)
                                    response = "Элементы с id меньше чем $id удалены"
                                }
                                else {
                                    removeLower.removeLowerUser(connection, id, requestUser1.username)
                                    flatsSet = show.getHashSet(connection)
                                    response = "Элементы с id меньше чем $id удалены пользователя ${requestUser1.username}"
                                }

                            }
                            is RemoveGreater -> {
                                val requestId = reader.readLine()
                                val xmlMapper3 = XmlMapper()
                                val id = xmlMapper3.readValue(requestId, String::class.java).toLong()
                                val requestUser = reader.readLine()
                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                                val removeGreater = RemoveGreater()
                                if (isAdmin(connection,requestUser1.username)){
                                    removeGreater.removeGreater(connection, id)
                                    flatsSet = show.getHashSet(connection)
                                    response = "Элементы с id больше чем $id удалены"
                                }
                                else {
                                    removeGreater.removeGreaterUser(connection, id, requestUser1.username)
                                    flatsSet = show.getHashSet(connection)
                                    response = "Элементы с id больше чем $id удалены пользователя ${requestUser1.username}"
                                }
                            }
                            is UpdateById -> {
                                val requestId = reader.readLine()
                                val xmlMapper3 = XmlMapper()
                                val id = xmlMapper3.readValue(requestId, String::class.java).toLong()
                                val requestFlat = reader.readLine()
                                val requestFlat1 = xmlMapper3.readValue<Flat>(requestFlat)
                                val requestUser = reader.readLine()
                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                                if (doesIdExist(connection, id)){
                                    val up = UpdateById()
                                    if (isAdmin(connection,requestUser1.username)){
                                        up.update(connection, requestFlat1, id)
                                        flatsSet = show.getHashSet(connection)
                                        response = show.showString(flatsSet)
                                    }
                                    else {
                                        up.updateUser(connection,requestFlat1, id, requestUser1.username)
                                        flatsSet = show.getHashSet(connection)
                                        response = show.showString(flatsSet)
                                    }
                                }
                                else {
                                    response = "Id не существует"
                                }


                            }

//                            is AddIfMax -> {
//                                val requestFlat = reader.readLine()
//                                val xmlMapper3 = XmlMapper()
//                                val requestFlat1 = xmlMapper3.readValue<Flat>(requestFlat)
//                                val requestUser = reader.readLine()
//                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
//                                val maxId = flats.maxByOrNull { it.id }?.id ?: 0
//                                if (requestFlat1.id > maxId) {
//                                    val addM  = AddIfMax()
//                                    addM.execute(connection, requestFlat1, requestUser1.username)
//                                    response = "Элемент добавлен"
//                                } else {
//                                    response = "Id меньше максимального"
//                                }
//                            }
                            is PrintUniqueTimeToMetroOnFoot -> {
                                response = requestObj.uniqueTime(flats)
                            }
                            is RemoveAllByNumberOfRooms -> {
                                val requestNOR = reader.readLine()
                                val xmlMapper3 = XmlMapper()
                                val NOR = xmlMapper3.readValue(requestNOR, String::class.java).toInt()
                                val removeAllByNumberOfRooms = RemoveAllByNumberOfRooms()
                                val requestUser = reader.readLine()
                                val requestUser1 = xmlMapper3.readValue<User>(requestUser)
                                if (isAdmin(connection,requestUser1.username)){
                                    removeAllByNumberOfRooms.removeByRooms(connection, NOR)
                                    response = "Объекты с $NOR комнатами удалены"
                                    flatsSet = show.getHashSet(connection)
                                }
                                else {
                                    removeAllByNumberOfRooms.removeByRoomsAndUsername(connection, NOR, requestUser1.username)
                                    response = "Объекты с $NOR комнатами удалены пользователя ${requestUser1.username}"
                                    flatsSet = show.getHashSet(connection)
                                }

                            }


                            else -> {
                                logger.info("Обработка объекта неизвестного типа: $requestObj")
                                response = "Обработка объекта неизвестного типа"
                            }
                        }
                        val message = Message(response)
                        val xmlResponse = message.toXml()
                        val respon = parseMessage(xmlResponse)
                        writer.println(xmlResponse)
                        logger.info("Отправлен ответ клиенту: $respon")


                    }
                } catch (e: SocketException){
                    e.printStackTrace()
                }
                finally {
                    reader.close()
                    writer.close()
                    clientSocket.close()
                }
            }.start()
        }
    }
    private fun parseMessage(xmlString: String): Message {
        return xmlMapper.readValue(xmlString, Message::class.java)
    }
    private fun doesIdExist(connection: Connection, id: Long): Boolean {
        val sql = "SELECT COUNT(*) FROM flats WHERE id = ?"
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setLong(1, id)
            val resultSet: ResultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val count = resultSet.getInt(1)
                resultSet.close()
                preparedStatement.close()
                return count > 0
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
    }
}


fun main() {
    val serverPort = 8080

    val server = Server(serverPort)
    server.start()
}

