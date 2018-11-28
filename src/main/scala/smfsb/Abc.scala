/*
Abc.scala

Functions for ABC and ABC-SMC

*/

package smfsb

/**
  * Functions for parameter inference using ABC (and ABC-SMC) methods
  */
object Abc {

  import scala.collection.GenSeq
  import breeze.linalg._
  import breeze.numerics._
  import breeze.stats.DescriptiveStats._

  /**
    * Function for running ABC simulations in parallel on all cores. This
    * function simply carries out the runs. No post-processing (including
    * rejection) is carried out.
    * 
    * @param n The number of ABC simulations to be carried out
    * @param rprior Function returing a single simulated parameter value 
    * from a prior
    * @param dist Function that takes a single parameter value and runs a
    * forward simulation from the model, then computes distance of output from a
    * target data set
    * 
    * @return The collection of generated parameters together with their
    * simulated distances from a target data set
    */
  def run[P, D](n: Int, rprior: => P, dist: P => D): GenSeq[(P, D)] = {
    (1 to n).par.map(i => {
      val p = rprior
      (p,dist(p))
    })
  }

  private def smcStep[P](
    dprior: P => LogLik, priorSample: GenSeq[P],
    priorLW: GenSeq[LogLik], rdist: P => Double,
    rperturb: P => P, dperturb: (P,P) => LogLik,
    factor: Int = 10
  ): (GenSeq[P], GenSeq[LogLik]) = {
    val n = priorSample.length
    val mx = priorLW reduce (math.max(_,_))
    val rw = priorLW map (w => math.exp(w - mx))
    val priorIdx = Mll.sample(n*factor, DenseVector(rw.toArray))
    val prior = (priorIdx map (priorSample(_))).par //TODO: replace with flatMap
    val prop = prior map (rperturb)
    val dist = prop map (rdist)
    val qCut = percentile(dist.seq, 1.0/factor)
    val newP = ((prop zip dist) filter (_._2 < qCut)) map (_._1)
    val priorTup = priorSample zip priorLW
    val lw = newP map ( th => {
      val terms = priorTup map {case (p,w) => w + dperturb(th,p)}
      val mt = terms reduce (math.max(_,_))
      val denom = mt + math.log(
        (terms map (t => math.exp(t-mt))).sum
      )
      dprior(th) - denom
    })
    val nmx = lw reduce (math.max(_,_))
    val lwn = lw map (_ - nmx)
    val swn = (lwn map (math.exp(_))).sum
    val nlw = lwn map (w => math.log(math.exp(w)/swn))
    (newP, nlw)
  }

  // TODO: ScalaDoc
  def smc[P](
    N: Int, rprior: => P, dprior: P => LogLik, rdist: P => Double,
    rperturb: P => P, dperturb: (P,P) => LogLik, factor: Int = 10,
    steps: Int = 15, verb: Boolean = false
  ): GenSeq[P] = {
    val priorLW = collection.immutable.Vector.fill(N)(math.log(1.0/N)).par
    val priorSample = priorLW map (w => rprior)
    @annotation.tailrec
    def go(samp: GenSeq[P], lw: GenSeq[LogLik], n: Int): GenSeq[P] = {
      if (n == 0) {
        val idx = Mll.sample(N, exp(DenseVector(lw.toArray))).par
        idx map (samp(_))
      } else {
        if (verb) println(n)
        val out = smcStep(dprior, samp, lw, rdist, rperturb, dperturb, factor)
        go(out._1, out._2, n-1)
      }
    }
    go(priorSample, priorLW, steps)
  }

  /**
    * Generate some basic diagnostics associated with an ABC run.
    * Called purely for the side effect of generating output on the
    * console. 
    * 
    * @param output Collection of runs (and distances) such as generated
    * from `run`
    */
  def summary[P: CsvRow, D](output: GenSeq[(P, D)]): Unit = {
    Mcmc.summary(Mcmc.toDMD(output map (_._1)))
  }


}

// eof

