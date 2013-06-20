import java.io.File
import scala.util.Random
import collection.JavaConversions._
import org.apache.ftpserver._
import org.apache.ftpserver.listener._
import org.apache.ftpserver.usermanager._
import org.apache.ftpserver.usermanager.impl.WritePermission
import org.apache.ftpserver.ftplet._

object Main {
  def main(args: Array[String]) {
    if (args.size != 4) {
      println(
        "Usage: sbt\n" +
        "> run <port: Int> <Username: String> <Password: String> <Home_Directory: String>\n"
      )
    } else {
      ServerBuilder(args(0).toInt, args(1), args(2), args(3)).getServerInstance.start
    }
  }
}

case class ServerBuilder(port: Int, name: String, pass: String, home: String) {
  private[this] lazy val passwordEncryptor = new PasswordEncryptor {
    override def encrypt(password: String): String = password
    override def matches(a: String, b: String) = (a == b)
  }

  private[this] lazy val uniqueId = {
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    (1 to 12) map { _ => alphabet(Random.nextInt(alphabet.size)).toString } reduce (_+_)
  }

  private[this] lazy val serverFactory = new FtpServerFactory
  private[this] lazy val listenerFactory = new ListenerFactory
  private[this] lazy val userManagerFactory = new PropertiesUserManagerFactory
  private[this] lazy val userFactory = new UserFactory
  private[this] lazy val propertyFile = new File("/tmp/.quickftp-" + uniqueId)

  lazy val getServerInstance: FtpServer = {
    // Setup listener
    listenerFactory.setPort(port)
    serverFactory.addListener("default", listenerFactory.createListener)

    // Setup user account
    userManagerFactory.setFile(propertyFile)
    userManagerFactory.setPasswordEncryptor(passwordEncryptor)
    userFactory.setName(name)
    userFactory.setPassword(pass)
    userFactory.setHomeDirectory(home)
    userFactory.setAuthorities(asJavaList(List[Authority](new WritePermission)))
    propertyFile.createNewFile
    val userManager = userManagerFactory.createUserManager
    userManager.save(userFactory.createUser)
    serverFactory.setUserManager(userManager)

    // Build Server
    serverFactory.createServer
  }
}
