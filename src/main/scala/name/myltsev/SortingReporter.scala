package name.myltsev

import org.scalatest.Reporter
import org.scalatest.events._
import scala.collection.mutable
import scala.concurrent.duration._

class SortingReporter extends Reporter {
  private[this] case class ErrorData(message: String, throwable: Option[Throwable])

  private[this] class TestInfo(val testName: String) {
    private[this] val nanoStartTime: Long = System.nanoTime()

    private[this] var nanoEndTime: Long = 0
    
    private[this] var error: ErrorData = _
    
    def success(): Unit = 
      nanoEndTime = System.nanoTime()
    
    def fail(errorData: ErrorData): Unit = {
      nanoEndTime = System.nanoTime()
      error = errorData
    }
    
    def duration: Duration = (nanoEndTime - nanoStartTime).nanos
    
    def errorData: ErrorData = error 
    
    def succeeded: Boolean = error == null
  }

  private[this] class SuiteInfo() {
    var tests = Map.empty[String, TestInfo]
  }

  private[this] var suites = Map.empty[String, SuiteInfo]
  
  val RED = "\033[31m"
  val GREEN = "\033[32m"
  val BOLD = "\033[1m"
  val NORMAL = "\033[0m"

  def apply(event: Event): Unit = event match {
    case rs: RunStarting =>

    case ts: TestStarting =>
      suites(ts.suiteId).tests += ts.testName -> new TestInfo(ts.testName)

    case ts: TestSucceeded =>
      suites(ts.suiteId).tests(ts.testName).success()

    case ts: TestFailed =>
      suites(ts.suiteId).tests(ts.testName).fail(ErrorData(ts.message, ts.throwable))

    case ss: SuiteStarting =>
      suites += ss.suiteId -> new SuiteInfo()

    case sc: SuiteCompleted =>
      val tests = suites(sc.suiteId).tests.values.toList.sortBy(_.duration)

      greenMsg { s"$GREEN${sc.suiteName}" }
      tests.foreach(ti => {
        if (ti.succeeded) greenMsg {
          s"- ${ti.testName} [${ti.duration.toMillis} millis]"
        } else redMsg {
          val stackTrace = ti.errorData.throwable match {
            case None => "" 
            case Some(throwable) => buildStackTrace(throwable).mkString("\n")
          }          
          
          s"- ${ti.testName} *** FAILED *** [${ti.duration.toMillis} millis]\n" + 
          s"  ${ti.errorData.message}\n" +
          stackTrace
        }
      })

      suites -= sc.suiteId

    case _ =>
  }
  
  private[this] def greenMsg(str: Any): Unit = println(s"$GREEN$str")
  private[this] def redMsg(str: Any): Unit = println(s"$RED$str")

  private[this] def buildStackTrace(throwable: Throwable, limit: Int = 10): List[String] = {
    var out = new mutable.ListBuffer[String]
    if (limit > 0) {
      out ++= throwable.getStackTrace.map { elem => "    at %s".format(elem.toString) }
      if (out.length > limit) {
        out.trimEnd(out.length - limit)
        out += "    ..."
      }
    }
    if ((throwable.getCause ne null) && (throwable.getCause ne throwable)) {
      out += "Caused by %s".format(throwable.getCause.toString)
      out ++= buildStackTrace(throwable.getCause, limit)
    }
    out.toList
  }
}
