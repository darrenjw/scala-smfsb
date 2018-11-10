/*
Types.scala

Types and type classes used throughout the package

 */

package smfsb

object Types {

  import breeze.linalg._

  // Hard-coded types:
  type Time = Double
  type Ts[S] = List[(Time, S)]
  type LogLik = Double
  // TODO: Make HazardVec a type class?
  type HazardVec = DenseVector[Double]


  // TODO: Clean up by switching to Simulacrum

  // Now type classes:

  // Serialisable to a CSV row (and numeric vector)...
  trait CsvRow[T] {
    def toCsv(value: T): String
    def toDvd(value: T): DenseVector[Double]
    }
  implicit class CsvRowSyntax[T](value: T) {
    def toCsv(implicit inst: CsvRow[T]): String = inst.toCsv(value)
    def toDvd(implicit inst: CsvRow[T]): DenseVector[Double] = inst.toDvd(value)
  }

  // State type class, with implementations for Ints and Doubles
  trait State[S] extends CsvRow[S] {
  }
  type IntState = DenseVector[Int]
  implicit val dviState = new State[IntState] {
    def toCsv(s: IntState): String = (s.toArray map (_.toString)).mkString(",")
    def toDvd(s: IntState): DenseVector[Double] = s.map(_*1.0)
  }
  type DoubleState = DenseVector[Double]
  implicit val dvdState = new State[DoubleState] {
    def toCsv(s: DoubleState): String = (s.toArray map (_.toString)).mkString(",")
    def toDvd(s: DoubleState): DenseVector[Double] = s
  }

  // SPN class - concrete for now
  sealed trait Spn[S]{
    def species: List[String]
    def pre: DenseMatrix[Int]
    def post: DenseMatrix[Int]
    def h: (S, Time) => HazardVec
  }
  case class UnmarkedSpn[S: State](
    species: List[String],
    pre: DenseMatrix[Int],
    post: DenseMatrix[Int],
    h: (S, Time) => HazardVec
  ) extends Spn[S]
  case class MarkedSpn[S: State](
    species: List[String],
    m: S,
    pre: DenseMatrix[Int],
    post: DenseMatrix[Int],
    h: (S, Time) => HazardVec
  ) extends Spn[S]


  // Data set type class, for ABC methods
  trait DataSet[D] {
  }
  implicit val tsisDs = new DataSet[Ts[IntState]] {
  }
  implicit val tsdsDs = new DataSet[Ts[DoubleState]] {
  }

  // Now type classes for inferential methods
  // Observation type class, with implementations for Ints and Doubles
  trait Observation[O] extends CsvRow[O] {
  }
  implicit val dviObs = new Observation[IntState] {
    def toCsv(s: IntState): String = (s.toArray map (_.toString)).mkString(",")
    def toDvd(s: IntState): DenseVector[Double] = s.map(_*1.0)
  }
  implicit val dvdObs = new Observation[DoubleState] {
    def toCsv(s: DoubleState): String = (s.toArray map (_.toString)).mkString(",")
    def toDvd(s: DoubleState): DenseVector[Double] = s
  }

  // Add a .thin method to Stream

  // First define a Thinnable typeclass
  trait Thinnable[F[_]] {
    def thin[T](f: F[T], th: Int): F[T]
  }
  implicit class ThinnableSyntax[T,F[T]](value: F[T]) {
    def thin(th: Int)(implicit inst: Thinnable[F]): F[T] =
      inst.thin(value,th)
  }
  // A thinnable instance for Stream
  implicit val streamThinnable: Thinnable[Stream] =
    new Thinnable[Stream] {
      def thin[T](s: Stream[T],th: Int): Stream[T] = {
        val ss = s.drop(th-1)
        if (ss.isEmpty) Stream.empty else
          ss.head #:: thin(ss.tail, th)
      }
    }





}

/* eof */
