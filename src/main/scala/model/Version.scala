package com.alelk.pws.library_manager
package model

case class Version(major: Int, minor: Int) {
  override def toString: String = s"$major.$minor"
}

object Version {
  def apply(version: String): Version = {
    val parts = version.split('.')
    if (parts.length != 2) throw new IllegalArgumentException(s"Invalid version format: $version")
    Version(parts(0).toInt, parts(1).toInt)
  }
}
