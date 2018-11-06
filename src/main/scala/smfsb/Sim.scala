/*
Sim.scala

Simulation utilities

 */

package smfsb

import Types._
import annotation.tailrec

object Sim {

  def ts[S: State](
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
      if (t0 >= tt) list else {
        val t1 = t0 + dt
        val x1 = stepFun(x0, t0, dt)
        go((t1, x1) :: list, tt, dt, stepFun)
      }
    }
    go(List((t0, x0)), tt, dt, stepFun).reverse
  }

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
          val x1 = stepFun(x0, t0, t1-t0)
          go((t1, x1) :: list, timeList.tail, stepFun)
        }
      }
    }
    val t1=timeList.head
    val x1=stepFun(x0,t0,t1-t0)
    go(List((t1, x1)), timeList.tail, stepFun).reverse
  }

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
      if (n <= 0) list else {
        val x1 = stepFun(x0, t0, deltat)
        go(x1 :: list, n-1)
      }
    }
    go(Nil, n).reverse
  }

  // plot utilities

  def plotTs[S: State](ts: Ts[S]): Unit = {
    import breeze.plot._
    import breeze.linalg._
    val times = DenseVector((ts map (_._1)).toArray)
    val idx = 0 until ts(0)._2.toDvd.length
    val states = ts map (_._2)
    val f = Figure()
    val p = f.subplot(0)
    idx.foreach(i => p += plot(times, DenseVector((states map (_.toDvd.apply(i))).toArray)))
    p.xlabel = "Time"
    p.ylabel = "Species count"
    f.saveas("TsPlot.png")
  }

  def toCsv[S: State](ts: Ts[S]): String = {
    val ls = ts map { t => t._1.toString + "," + t._2.toCsv + "\n" }
    ls.foldLeft("")(_ + _)
  }



}


// eof
