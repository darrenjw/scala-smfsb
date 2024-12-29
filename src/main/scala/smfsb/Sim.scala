/*
Sim.scala

Simulation utilities

 */

package smfsb

import annotation.tailrec

/** Functions for simulating data associated with a Markov process given an
  * appropriate transition kernel.
  */
object Sim {

  /** Use a transition kernel to simulate states on a regular time grid
    *
    * @param x0
    *   Initial state
    * @param t0
    *   Initial time
    * @param tt
    *   The terminal time
    * @param dt
    *   The time step of the output time grid
    * @param stepFun
    *   The transition kernel, such as output by one of the functions in `Sim`
    *
    * @return
    *   A time series of simulated states corresponding to a single realisation
    *   of the underlying stochastic process
    */
  def ts[S](
      x0: S,
      t0: Time = 0.0,
      tt: Time = 100.0,
      dt: Time = 0.1,
      stepFun: (S, Time, Time) => S
  ): Ts[S] = {
    @tailrec
    def go(
        list: Ts[S],
        tt: Time,
        dt: Time,
        stepFun: (S, Time, Time) => S
    ): Ts[S] = {
      val (t0, x0) = list.head
      if (t0 >= tt) list
      else {
        val t1 = t0 + dt
        val x1 = stepFun(x0, t0, dt)
        go((t1, x1) :: list, tt, dt, stepFun)
      }
    }
    go(List((t0, x0)), tt, dt, stepFun).reverse
  }

  /** Use a transition kernel to simulate states on an irregular time grid
    *
    * @param x0
    *   Initial state
    * @param t0
    *   Initial time
    * @param timeList
    *   A list of times where the state of the process is required
    * @param stepFun
    *   The transition kernel, such as output by one of the functions in `Sim`
    *
    * @return
    *   A time series of simulated states at the required times corresponding to
    *   a single realisation of the underlying stochastic process
    */
  def times[S: State](
      x0: S,
      t0: Time = 0.0,
      timeList: List[Time],
      stepFun: (S, Time, Time) => S
  ): Ts[S] = {
    @tailrec
    def go(
        list: Ts[S],
        timeList: List[Time],
        stepFun: (S, Time, Time) => S
    ): Ts[S] = {
      val (t0, x0) = list.head
      timeList match {
        case Nil => list
        case (t :: ts) => {
          val t1 = timeList.head
          val x1 = stepFun(x0, t0, t1 - t0)
          go((t1, x1) :: list, timeList.tail, stepFun)
        }
      }
    }
    val t1 = timeList.head
    val x1 = stepFun(x0, t0, t1 - t0)
    go(List((t1, x1)), timeList.tail, stepFun).reverse
  }

  /** Simulate multiple independent realisations from a transition kernel
    *
    * @param n
    *   The number of realisations required
    * @param x0
    *   The initial state
    * @param t0
    *   The intial time
    * @param deltat
    *   The time interval over which to simulate the process
    * @param stepFun
    *   The transition kernel to use
    *
    * @return
    *   A `List` of realisations of the kernel at time `t0+deltat`
    */
  def sample[S: State](
      n: Int = 100,
      x0: S,
      t0: Time = 0.0,
      deltat: Time,
      stepFun: (S, Time, Time) => S
  ): List[S] = {
    @tailrec
    def go(
        list: List[S],
        n: Int
    ): List[S] = {
      if (n <= 0) list
      else {
        val x1 = stepFun(x0, t0, deltat)
        go(x1 :: list, n - 1)
      }
    }
    go(Nil, n).reverse
  }

  /** A function for producing a very simple plot of a time series, useful for a
    * quick eye-balling of simulation output. Called purely for the side-effect
    * of rendering a plot on the console.
    *
    * @param ts
    *   A time series of `States`
    * @param title
    *   Optional figure title
    */
  def plotTs[S: State](ts: Ts[S], title: String = ""): Unit = {
    import breeze.plot._
    import breeze.linalg._
    val times = DenseVector((ts map (_._1)).toArray)
    val idx = 0 until ts(0)._2.toDvd.length
    val states = ts map (_._2)
    val f = title match {
      case ""  => Figure()
      case ttl => Figure(ttl)
    }
    val p = f.subplot(0)
    idx.foreach(i =>
      p += plot(times, DenseVector((states map (_.toDvd.apply(i))).toArray))
    )
    p.xlabel = "Time"
    p.ylabel = "Species count"
    f.saveas("TsPlot.png")
  }

  /** Utility for converting a time series to a CSV string
    *
    * @param ts
    *   A time series of `States`
    *
    * @return
    *   A CSV string
    */
  def toCsv[S: State](ts: Ts[S]): String = {
    val ls = ts map { t => t._1.toString + "," + t._2.toCsv + "\n" }
    ls.foldLeft("")(_ + _)
  }

}

// eof
