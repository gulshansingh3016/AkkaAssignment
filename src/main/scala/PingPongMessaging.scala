import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

// First Way

object Pingprotocol{
  case object Ping
  case object Pong
  case class childPong(name: String)
}
class Ping extends Actor with ActorLogging{
  import Pingprotocol._
  override def receive: Receive = {
    case childPong(name) =>
      println("Creating Pong Child inside Ping Actor")
      val childRef = context.actorOf(Props[Pong], name)
       childRef ! Ping
    case Pong => log.info("Pong")
  }
}

class Pong extends Actor with ActorLogging {
  import Pingprotocol._
  override def receive: Receive = {
    case Ping => log.info("Ping")
      sender ! Pong
  }
}

object runPingPong extends App {
  import Pingprotocol._
  val system = ActorSystem("PingPong")
  val ping = system.actorOf(Props[Ping], "ping")
  ping ! childPong("Child")

}







import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

// Second Way

object PingPongCommunication extends App {

  object Ping {
    case class CreateChild(name: String)
  }
  class Ping extends Actor with ActorLogging {
    import Ping._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child")
        // create a new actor right HERE
        val childRef = context.actorOf(Props[Pong], name)
        childRef ! "Ping"

      case "Pong" => log.info("Ping")
    }
  }

  class Pong extends Actor with ActorLogging{
    override def receive: Receive = {
      case "Ping" => log.info("Pong")
          sender ! "Pong"
     // case message => println(s"${self.path} I got: $message")
    }
  }

  import Ping._
  val system = ActorSystem("ParentChildDemo")
  val ping = system.actorOf(Props[Ping], "parent")
  ping ! CreateChild("child")
}