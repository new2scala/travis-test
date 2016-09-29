package controllers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.net.URLDecoder
import java.sql.Timestamp
import java.util.{Base64, Calendar, TimeZone}
import javax.inject.{Inject, Singleton}

import akka.stream.scaladsl.Source
import akka.util.{ByteString, Timeout}

import models._
import org.apache.pdfbox.util.PDFMergerUtility
import play.api.db.slick.DatabaseConfigProvider
import play.api.http.{HttpChunk, HttpEntity}
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{AnyContent, _}

import slick.driver.JdbcProfile

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.actor.ActorSystem

import org.joda.time.DateTime

import scala.util.{Failure, Success}

@Singleton
class Application @Inject()(
                            actorSys:ActorSystem,
                            //pdfDownloader:PDFDownloader,
                            protected val dbConfigProvider: DatabaseConfigProvider,
                            val messagesApi: MessagesApi,
                            implicit val webJarAssets: WebJarAssets
                           )
                           extends Controller with I18nSupport {

  //val helloActor = actorSys.actorOf(TestActor.props, "hello-actor")

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  implicit val messages = messagesApi.preferred(Seq(Lang("zhs")))

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import akka.pattern.ask
  implicit val timeout:Timeout = 5.seconds

  def sayHi = Action.async {implicit rs =>
    Future.successful(Ok("Hellow!"))
  }

  def testCharts = Action.async {implicit rs =>
    Future.successful(Ok(views.html.chartsTest()))
  }

  val testData =
    """
      |  {
      |    "chart": {
      |      "caption": "Sales - 2012 v 2013",
      |      "numberprefix": "$",
      |      "plotgradientcolor": "",
      |      "bgcolor": "FFFFFF",
      |      "showalternatehgridcolor": "0",
      |      "divlinecolor": "CCCCCC",
      |      "showvalues": "0",
      |      "showcanvasborder": "0",
      |      "canvasborderalpha": "0",
      |      "canvasbordercolor": "CCCCCC",
      |      "canvasborderthickness": "1",
      |      "yaxismaxvalue": "30000",
      |      "captionpadding": "30",
      |      "linethickness": "3",
      |      "yaxisvaluespadding": "15",
      |      "legendshadow": "0",
      |      "legendborderalpha": "0",
      |      "palettecolors": "#f8bd19,#008ee4,#33bdda,#e44a00,#6baa01,#583e78",
      |      "showborder": "0"
      |    },
      |    "categories": [
      |      {
      |        "category": [
      |          {
      |            "label": "Jan"
      |          },
      |          {
      |            "label": "Feb"
      |          },
      |          {
      |            "label": "Mar"
      |          },
      |          {
      |            "label": "Apr"
      |          },
      |          {
      |            "label": "May"
      |          },
      |          {
      |            "label": "Jun"
      |          },
      |          {
      |            "label": "Jul"
      |          },
      |          {
      |            "label": "Aug"
      |          },
      |          {
      |            "label": "Sep"
      |          },
      |          {
      |            "label": "Oct"
      |          },
      |          {
      |            "label": "Nov"
      |          },
      |          {
      |            "label": "Dec"
      |          }
      |        ]
      |      }
      |    ],
      |    "dataset": [
      |      {
      |        "seriesname": "2013",
      |        "data": [
      |          {
      |            "value": "22400"
      |          },
      |          {
      |            "value": "24800"
      |          },
      |          {
      |            "value": "21800"
      |          },
      |          {
      |            "value": "21800"
      |          },
      |          {
      |            "value": "24600"
      |          },
      |          {
      |            "value": "27600"
      |          },
      |          {
      |            "value": "26800"
      |          },
      |          {
      |            "value": "27700"
      |          },
      |          {
      |            "value": "23700"
      |          },
      |          {
      |            "value": "25900"
      |          },
      |          {
      |            "value": "26800"
      |          },
      |          {
      |            "value": "24800"
      |          }
      |        ]
      |      },
      |      {
      |        "seriesname": "2012",
      |        "data": [
      |          {
      |            "value": "10000"
      |          },
      |          {
      |            "value": "11500"
      |          },
      |          {
      |            "value": "12500"
      |          },
      |          {
      |            "value": "15000"
      |          },
      |          {
      |            "value": "16000"
      |          },
      |          {
      |            "value": "17600"
      |          },
      |          {
      |            "value": "18800"
      |          },
      |          {
      |            "value": "19700"
      |          },
      |          {
      |            "value": "21700"
      |          },
      |          {
      |            "value": "21900"
      |          },
      |          {
      |            "value": "22900"
      |          },
      |          {
      |            "value": "20800"
      |          }
      |        ]
      |      }
      |    ]
      |  }
      |
    """.stripMargin
  def getData = {

  }



  val ContentType_AppJson = "application/json"

  private def profile(msg:String, prevTs:Option[Long] = None):Long = {
    val t = new DateTime().getMillis
    if (prevTs.nonEmpty) {
      val diff = t - prevTs.get
      println(s"[PROFILING] $msg: $diff ms")
    }
    else {
      println(s"[PROFILING] $msg")
    }
    t
  }


  val encoding = "UTF-8"
}