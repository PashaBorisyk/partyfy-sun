package dao.sql

import slick.jdbc.PostgresProfile.api._

package object implicits {

   implicit class RegexLikeOps(left: Rep[String]) {

      def regexLike(right: Rep[String]) = {
         val expression = SimpleExpression.binary[String, String, Boolean] { (left, right, queryBuilder) =>
            queryBuilder.expr(left)
            queryBuilder.sqlBuilder += " ~* "
            queryBuilder.expr(right)
         }
         expression(left, right)
      }

   }



}
