package example

import core.Weather

object Hello extends App {
  println(s"Hello! The weather in New York is ${Weather.likeNow()}.")
}