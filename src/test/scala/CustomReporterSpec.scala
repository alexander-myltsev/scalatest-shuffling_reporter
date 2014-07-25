import org.scalatest.{MustMatchers, WordSpec}

class CustomReporter1Spec extends WordSpec 
                             with MustMatchers {
  "CustomReporter 1" must {
    "sum correctly" in {
      2 + 2 mustBe 4
    }
    
    "do very long job in the middle of suite" in {
      Thread.sleep(150)
      3 * 3 mustBe 9
    }
    
    "handle nested test" when {
      "parent test" which {
        "child test 1" in {
          Thread.sleep(250)
          3 * 4 mustBe 12
        }

        "child test 2" in {
          Thread.sleep(200)
          3 * 4 mustBe 12
        }
      }
      
      "nested test" in {
        Thread.sleep(100)
        3 * 4 mustBe 12
      }
    }
    
    "get string length correctly" in {
      "abc".length mustBe 3
    }
    
    "handle failed test" in {
      2 + 2 mustBe 5
    }
    
    "handle exception" in {
      throw new Exception("exception message")
    }
  }
}

class CustomReporter2Spec extends WordSpec
                             with MustMatchers {
  "CustomReporter 2" must {
    "sum correctly" in {
      2 + 2 mustBe 4
    }

    "get string length correctly" in {
      "abc".length mustBe 3
    }
  }
}
