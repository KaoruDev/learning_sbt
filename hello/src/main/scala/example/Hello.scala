package example

import com.example.hellotwo.{Dep, Deuce}

import scala.concurrent._
import duration._
import core.Weather

object Hello extends App {
  val w = Await.result(Weather.weather, 10.seconds)
  println(s"Hello! The weather in New York is $w.")
  Weather.http.close()
  println(s"Dep in Hello: ${Dep().bar()}")
  println(s"Deuce in Hello: ${Deuce().deuce()}")

  Weather.car()
}