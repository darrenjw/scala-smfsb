/*
package.scala

Package object

Mainly types and type classes used throughout the package

 */

// package smfsb


/**
  * Object containing basic types used throughout the library.
  */
package object smfsb {

  import breeze.linalg._

  /**
    * Type representing time, but just an alias for `Double`
    */
  type Time = Double

  /**
    * The main time series class, for representing output from simulation functions, and for observed time course data
    */
  type Ts[S] = List[(Time, S)]

  /**
    * Type representing log-likelihoods - just an alias for `Double`.
    * All likelihoods in this library are on a log scale.
    * There should be no raw likelihoods passed into or out of any user-facing function.
    */
  type LogLik = Double

  // TODO: Make HazardVec a type class?
  /**
    * Type for a SPN hazard vector
    */
  type HazardVec = DenseVector[Double]

  // TODO: Clean up by switching to Simulacrum

  // Now type classes:

  /**
    * Type class for vectors that can be rendered to a CSV string (and a Breeze DenseVector[Double]), extended by `State`
    */
  trait CsvRow[T] {
    def toCsv(value: T): String
    def toDvd(value: T): DenseVector[Double]
  }

  /**
    * Syntax for `CsvRow`
    */
  implicit class CsvRowSyntax[T](value: T) {
    def toCsv(implicit inst: CsvRow[T]): String = inst.toCsv(value)
    def toDvd(implicit inst: CsvRow[T]): DenseVector[Double] = inst.toDvd(value)
  }

  /**
    * State type class, with implementations for Breeze `DenseVector` `Ints` and `Doubles`
    */
  trait State[S] extends CsvRow[S] {
  }

  /**
    * Alias for a Breeze `DenseVector[Int]`
    */
  type IntState = DenseVector[Int]

  /**
    * Evidence that `IntState` belongs to the `State` type class
    */
  implicit val dviState = new State[IntState] {
    def toCsv(s: IntState): String = (s.toArray map (_.toString)).mkString(",")
    def toDvd(s: IntState): DenseVector[Double] = s.map(_*1.0)
  }

  /**
    * Alias for a Breeze `DenseVector[Double]`
    */
  type DoubleState = DenseVector[Double]

  /**
    * Evidence that `DoubleState` belongs to the `State` type class
    */
  implicit val dvdState = new State[DoubleState] {
    def toCsv(s: DoubleState): String = (s.toArray map (_.toString)).mkString(",")
    def toDvd(s: DoubleState): DenseVector[Double] = s
  }

  /**
    * Main trait for stochastic Petri nets (SPNs)
    */
  sealed trait Spn[S]{
    def species: List[String]
    def pre: DenseMatrix[Int]
    def post: DenseMatrix[Int]
    def h: (S, Time) => HazardVec
  }
  /**
    * Case class for SPNs without an initial marking
    */
  case class UnmarkedSpn[S: State](
    species: List[String],
    pre: DenseMatrix[Int],
    post: DenseMatrix[Int],
    h: (S, Time) => HazardVec
  ) extends Spn[S]
  /**
    * Case class for SPNs that include an initial marking
    */
  case class MarkedSpn[S: State](
    species: List[String],
    m: S,
    pre: DenseMatrix[Int],
    post: DenseMatrix[Int],
    h: (S, Time) => HazardVec
  ) extends Spn[S]


  /** 
    * Data set type class, for ABC methods
    */
  trait DataSet[D] {
  }

  /**
    * Evidence that `Ts[IntState]` is a `DataSet`
    */
  implicit val tsisDs = new DataSet[Ts[IntState]] {
  }

  /**
    * Evidence that `Ts[DoubleState]` is a `DataSet`
    */
  implicit val tsdsDs = new DataSet[Ts[DoubleState]] {
  }

  /**
    * A type class for things which can be "thinned", with an implementation for `Stream`.
    * Useful for MCMC algorithms.
    */
  trait Thinnable[F[_]] {
    def thin[T](f: F[T], th: Int): F[T]
  }

  /**
    * Provision of the `.thin` syntax for `Thinnable` things
    */
  implicit class ThinnableSyntax[T,F[T]](value: F[T]) {
    def thin(th: Int)(implicit inst: Thinnable[F]): F[T] =
      inst.thin(value,th)
  }

  /**
    * A `Thinnable` instance for `Stream`
    */
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
