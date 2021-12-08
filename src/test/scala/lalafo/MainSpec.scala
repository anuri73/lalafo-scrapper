package lalafo

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MainSpec extends AnyFlatSpec with Matchers {
  "The Main object" should "has category id for scrapping" in {
    Main.categryId shouldEqual 2046
  }
}
