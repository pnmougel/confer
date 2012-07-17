package controllers.admin

case class ConferenceMatch(shortName : String, longName : String, possibleMatch : List[(Long, String)], score : String)