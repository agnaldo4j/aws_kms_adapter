package br.codesimples.kms

import org.specs2.mutable.Specification

class KmsAdapterSpec extends Specification {

  isolated
  sequential
  "The kms adapter showd" should {
    args(sequential=true, isolated=true)
    "crypt data" ! workerForTest().cryptData()
    "decrypt data" ! workerForTest().decryptData()
  }

  private def toCryptDataPacket(): DataPacket = {
    DataPacket(
      List[Data](
        Data("name", "Agnaldo de Oliveira"),
        Data("email", "teste@teste.com"),
        Data("phone", "111111111111"),
        Data("valor", "10.20")
      )
    )
  }

  private def toDecryptDataPacket(): DataPacket = {
    DataPacket(
      List[Data](
        Data(
          "name",
          "AYADeGeV3SfcDPxp5AJmFbKH4CcAXwABABVhd3MtY3J5cHRvLXB1YmxpYy1rZXkAREFsSEhHWm1ncDJyYUZldXNrM3owZU9WQzVZN3FrY1phMytLSmdwV3lsVXJZTGZhN1FOV2RWcEw3dzUxTzFLbWFidz09AAEAB2F3cy1rbXMAS2Fybjphd3M6a21zOnVzLWVhc3QtMTo2MDU0NzYyNDIyNzQ6a2V5L2FjMTUxNDFiLTYwOWUtNDg4My04ZWJmLTg3YWQ4ZDA3OGUyZgCnAQEBAHg1v2v0v+nCMq8WgYxlOgjvYcA+nsBq/MqBSGsV0ok0+gAAAH4wfAYJKoZIhvcNAQcGoG8wbQIBADBoBgkqhkiG9w0BBwEwHgYJYIZIAWUDBAEuMBEEDF3R9vtHhF9HmeDZ3wIBEIA7TLiLbTWd1bJ4NX4JpBKmp6wBz5C8lQD5nQb6G4BLks8GHbVR0dsrDd5ho8Rp+3C3UdPNlkgKYr8tev8CAAAAAAwAABAAqtkXVbBET8jYFmKrWpsw3wsHCOKXpLpdnVObt/////8AAAABiQEeTiq5vGNYaoOBAAAAE3EOiYZwdoTGSM2xx54xuam+/jn7s4wKYNPwOaELOwUHws/QAGYwZAIwQpyb+kI+l1bDPQ8ethFgw4uCAw5mMxUhygNEGsFwcmmx4d2N2SSPD523+comNDeuAjAbFCFx6X+UtmETn7YbehTsrFcH6LZuWj1HaNDIM9jbX4lXdftz1vkZKz3g1eQ8HG0="
        )
      )
    )
  }

  case class workerForTest() {
    def cryptData() = {
      val adapter = KmsAdapter.newWithEnvironmentVariables()
      val result = adapter.crypt( toCryptDataPacket() )
      val listOfValues = result.values
      listOfValues.size must be equalTo(4)
      val value = listOfValues(0)
      value.attribute must be equalTo("name")
      value.value.length must be greaterThan(20)
    }

    def decryptData() = {
      val adapter = KmsAdapter.newWithEnvironmentVariables()
      val result = adapter.decrypt( toDecryptDataPacket() )
      val listOfValues = result.values
      listOfValues.size must be equalTo(1)
      val value = listOfValues(0)
      value.attribute must be equalTo("name")
      value.value must be equalTo("Agnaldo de Oliveira")
    }
  }
}