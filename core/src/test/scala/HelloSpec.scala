import org.scalatest.{DiagrammedAssertions, FunSuite}

class HelloCoreSpec extends FunSuite with DiagrammedAssertions {
  test("Hello should start with H") {
    assert("Hello".startsWith("Hel"))
  }
}