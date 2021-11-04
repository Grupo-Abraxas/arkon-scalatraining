package com.arkondata.training.repo

trait InegiRepo {

  def search(): String
}

object InegiRepo {

  def fromTransactor =
  new InegiRepo {
    def search(): String = {
      "Hello!"
    }
  }


}