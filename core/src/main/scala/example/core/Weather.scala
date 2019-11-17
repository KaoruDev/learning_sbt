package example.core

import java.util.concurrent.{Executor, TimeUnit}

import com.google.common.util.concurrent.CheckedFuture

class WeatherCheckedFuture extends CheckedFuture[String, Exception] {
  override def checkedGet(): String = "checked-bar"

  override def checkedGet(timeout: Long, unit: TimeUnit): String = "checked-bar"

  override def cancel(mayInterruptIfRunning: Boolean): Boolean = true

  override def addListener(listener: Runnable, executor: Executor): Unit = ()

  override def isCancelled: Boolean = false

  override def isDone: Boolean = true

  override def get(): String = "bar"

  override def get(timeout: Long, unit: TimeUnit): String = "bar"
}

object Weather {
  def likeNow(): String = {
    new WeatherCheckedFuture()
    "Stuff is what the stuff is brother"
  }
}
