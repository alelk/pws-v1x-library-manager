package com.alelk.pws.library_manager
package model

case class Version(major: Int, minor: Int) {
  require(major >= 0, "Major version must be greater or equal to 0")
  require(minor >= 0, "Minor version must be greater or equal to 0")

  override def toString: String = s"$major.$minor"
}

object Version {
  def apply(version: String): Version = {
    val parts = version.split('.')
    if (parts.length != 2) throw new IllegalArgumentException(s"Invalid version format: $version")
    Version(parts(0).toInt, parts(1).toInt)
  }
}
