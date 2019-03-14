package services.database

package object implicits {

   implicit class SeqImplicit[+T1](val seq:Seq[(T1,Option[_])]){

      def extractOptions = seq.map{ entry =>
         entry._1->entry._2.getOrElse(None)
      }.asInstanceOf[Seq[(T1, Product with Serializable)]]

   }

}
