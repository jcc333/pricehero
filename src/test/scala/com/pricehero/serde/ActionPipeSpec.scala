package com.pricehero.serde

import com.twitter.util.{Await, Future}
import org.scalatest.FlatSpec

class ActionPipeSpec extends FlatSpec {
  it should "bubble up exceptions in case of failures in deserialization" in {
    //given
    val readFail = new ActionPipe[Int, String, Option[Int], String] {
      override def translate(a: Int): Future[String] = Future(a.toString)

      override def deserialize(in: Option[Int]): Future[Int] = Future(in.get)

      override def serialize(t: String): Future[String] = Future(t)
    }

    //when
    val futureFailure = readFail.run(None)

    //then
    assertThrows[NoSuchElementException](Await.result(futureFailure))
  }

  it should "bubble up exceptions in case of failures in the method" in {
    //given
    val translateFail = new ActionPipe[Int, String, Option[Int], Option[String]] {
      override def translate(a: Int): Future[String] = Future(throw new RuntimeException("Bleep blorp exception"))

      override def deserialize(in: Option[Int]): Future[Int] = Future(in.get)

      override def serialize(t: String): Future[Option[String]] = Future(Some(t))
    }

    //when
    val futureFailure = translateFail.run(Some(1))

    //then
    assertThrows[RuntimeException](Await.result(futureFailure))
  }

  it should "bubble up exceptions in case of failures in serialization" in {
    //given
    val writeFail = new ActionPipe[Int, String, Option[Int], Option[String]] {
      override def translate(a: Int): Future[String] = Future(a.toString)

      override def deserialize(in: Option[Int]): Future[Int] = Future(in.get)

      override def serialize(t: String): Future[Option[String]] = Future(throw new RuntimeException("Bleep blorp exception"))
    }

    //when
    val futureFailure = writeFail.run(Some(1))

    //then
    assertThrows[RuntimeException](Await.result(futureFailure))
  }

  it should "pipe through actual successes" in {
    //given
    val succeeds = new ActionPipe[Int, String, Option[Int], Option[String]] {
      override def translate(a: Int): Future[String] = Future(a.toString)

      override def deserialize(in: Option[Int]): Future[Int] = Future(in.get)

      override def serialize(t: String): Future[Option[String]] = Future(Some(t))
    }

    //when
    val futureSuccess = succeeds.run(Some(1))

    //then
    assert(Await.result(futureSuccess) == Some("1"))
  }

}
