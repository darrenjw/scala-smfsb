/*
package.scala

Package object

Mainly types and type classes used throughout the package

 */

// package smfsb

import collection.GenSeq
import scala.collection.parallel.immutable.ParSeq
import scala.collection.parallel.CollectionConverters._

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
  implicit val dviState: State[IntState] = new State[IntState] {
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
  implicit val dvdState: State[DoubleState] = new State[DoubleState] {
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
  implicit val tsisDs: DataSet[Ts[IntState]] = new DataSet[Ts[IntState]] {
  }

  /**
    * Evidence that `Ts[DoubleState]` is a `DataSet`
    */
  implicit val tsdsDs: DataSet[Ts[DoubleState]] = new DataSet[Ts[DoubleState]] {
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
  implicit val streamThinnable: Thinnable[LazyList] =
    new Thinnable[LazyList] {
      def thin[T](s: LazyList[T],th: Int): LazyList[T] = {
        val ss = s.drop(th-1)
        if (ss.isEmpty) LazyList.empty else
          ss.head #:: thin(ss.tail, th)
      }
    }



  import collection.parallel.immutable.ParVector

  /**
    * Comonadic pointed vector type (parallel implementation), used by 
    * the spatial simulation functions.
    */
  case class PVector[T](cur: Int, vec: ParVector[T]) {
    def apply(x: Int): T = vec(x) // TODO: bounds check?
    def map[S](f: T => S): PVector[S] = PVector(cur, vec map f)
    def updated(x: Int, value: T): PVector[T] =
      PVector(cur, vec.updated(x, value)) // TODO: bounds check?
    def zip[S](y: PVector[S]): PVector[(T,S)] = PVector(cur, vec zip y.vec)
    val length: Int = vec.length
    def extract: T = vec(cur)
    def coflatMap[S](f: PVector[T] => S): PVector[S] =
      PVector(cur,
        (0 until vec.length).toVector.par.map(i =>
          f(PVector(i, vec))))
    def forward: PVector[T] = {
      val newc = if (cur < vec.length - 1) (cur + 1) else 0
      PVector(newc, vec)
    }
    def back: PVector[T] = {
      val newc = if (cur > 0) (cur - 1) else (vec.length - 1)
      PVector(newc, vec)
    }
  }

  /**
    * Comonadic pointed 2d image/matrix type (parallel implementation), used by 
    * the spatial simulation functions.
    */
  case class PMatrix[T](x: Int, y: Int, r: Int, c: Int, data: ParVector[T]) {
    assert(r*c == data.length)
    def apply(x: Int, y: Int): T = {
      assert((x >= 0)&(x < c)&(y >= 0)&(y < r))
      data(x*r+y)
    }
    def map[S](f: T => S): PMatrix[S] =
      PMatrix(x, y, r, c, data map f)
    def updated(x: Int, y: Int, value: T): PMatrix[T] = {
      assert((x >= 0)&(x < c)&(y >= 0)&(y < r))
      PMatrix(x, y, r, c, data.updated(x*r+y, value))
    }
    def zip[S](m: PMatrix[S]): PMatrix[(T,S)] =
      PMatrix(x, y, r, c, data zip m.data)
    def extract: T = data(x*r+y)
    def coflatMap[S](f: PMatrix[T] => S): PMatrix[S] =
      PMatrix(x, y, r, c,
        (0 until r*c).toVector.par.map(i => {
          val xx = i / r
          val yy = i % r
          f(PMatrix(xx, yy, r, c, data))
        }))
    def up: PMatrix[T] = {
      val newy = if (y > 0) (y - 1) else (r - 1)
      PMatrix(x, newy, r, c, data)
    }
    def down: PMatrix[T] = {
      val newy = if (y < r - 1) (y + 1) else 0
      PMatrix(x, newy, r, c, data)
    }
    def left: PMatrix[T] = {
      val newx = if (x > 0) (x - 1) else (c - 1)
      PMatrix(newx, y, r, c, data)
    }
    def right: PMatrix[T] = {
      val newx = if (x < c - 1) (x + 1) else 0
      PMatrix(newx, y, r, c, data)
    }
  }

  import collection.GenSeq

  case object PMatrix {
    /**
      * Constructor for `PMatrix` objects. Used for initialising 2d spatial simulations.
      */
    def apply[T](r: Int, c: Int, data: Seq[T]): PMatrix[T] = {
      assert (r*c == data.length)
      PMatrix(0,0,r,c,data.toVector.par)
    }
    /**
      * Convert a PMatrix[Double] to a Breeze DenseMatrix
      */
    def toBDM(m: PMatrix[Double]): DenseMatrix[Double] =
      new DenseMatrix(m.r, m.c, m.data.toArray)
    /**
      * Convert a Breeze DenseMatrix to a PMatrix
      */
    def fromBDM[T](m: DenseMatrix[T]): PMatrix[T] =
      apply(m.rows, m.cols, m.data.toVector)
  }





}

/* eof */
