/*
Abc.scala

Functions for ABC and ABC-SMC

 */

package smfsb

/** Functions for parameter inference using ABC (and ABC-SMC) methods
  */
object Abc {

  // import scala.collection.GenSeq
  import scala.collection.parallel.immutable.ParSeq
  import scala.collection.parallel.CollectionConverters._

  import breeze.linalg._
  import breeze.numerics._
  import breeze.stats.DescriptiveStats._

  /** Function for running ABC simulations in parallel on all cores. This
    * function simply carries out the runs. No post-processing (including
    * rejection) is carried out.
    *
    * @param n
    *   The number of ABC simulations to be carried out
    * @param rprior
    *   Function returing a single simulated parameter value from a prior
    * @param dist
    *   Function that takes a single parameter value and runs a forward
    *   simulation from the model, then computes distance of output from a
    *   target data set
    *
    * @return
    *   The collection of generated parameters together with their simulated
    *   distances from a target data set
    */
  def run[P, D](n: Int, rprior: => P, dist: P => D): ParSeq[(P, D)] = {
    (1 to n).par.map(i => {
      val p = rprior
      (p, dist(p))
    })
  }

  // One step of an ABC-SMC algorithm - called by "smc"
  private def smcStep[P](
      dprior: P => LogLik,
      priorSample: ParSeq[P],
      priorLW: ParSeq[LogLik],
      rdist: P => Double,
      rperturb: P => P,
      dperturb: (P, P) => LogLik,
      factor: Int = 10
  ): (ParSeq[P], ParSeq[LogLik]) = {
    val n = priorSample.length
    val mx = priorLW reduce (math.max(_, _))
    val rw = priorLW map (w => math.exp(w - mx))
    val priorIdx = Mll.sample(n * factor, DenseVector(rw.toArray))
    val prior =
      (priorIdx map (priorSample(_))).par // TODO: replace with flatMap
    val prop = prior map (rperturb)
    val dist = prop map (rdist)
    val qCut = percentile(dist.seq, 1.0 / factor)
    val newP = ((prop zip dist) filter (_._2 < qCut)) map (_._1)
    val priorTup = priorSample zip priorLW
    val lw = newP map (th => {
      val terms = priorTup map { case (p, w) => w + dperturb(th, p) }
      val mt = terms reduce (math.max(_, _))
      val denom = mt + math.log(
        (terms map (t => math.exp(t - mt))).sum
      )
      dprior(th) - denom
    })
    val nmx = lw reduce (math.max(_, _))
    val lwn = lw map (_ - nmx)
    val swn = (lwn map (math.exp(_))).sum
    val nlw = lwn map (w => math.log(math.exp(w) / swn))
    (newP, nlw)
  }

  /** Function for running an ABC-SMC algorithm in parallel on all available
    * cores.
    *
    * @param N
    *   Number of samples to be propagated forward at each sweep
    * @param rprior
    *   Function for simulating from a prior on the parameters
    * @param dprior
    *   Function returning the log-density of a parameter under the prior
    * @param rdist
    *   Function which takes a parameter, runs a forward simultation, and then
    *   calculates a distance (as a scalar `Double`) from a target data set
    * @param rperturb
    *   Function to perturb a parameter vector (perturbation kernel, in ABC-SMC
    *   speak)
    * @param dperturb
    *   Function for evaluating the log-density of a perturbed parameter
    *   relative to an original parameter. In the case of a non-symmetric
    *   perturbation kernel, it is the new, perturbed value that is the first
    *   element of the tuple.
    * @param factor
    *   The acceptance rate at each sweep is `1/factor`
    * @param steps
    *   The number of sweeps of the ABC-SMC algorithm to perform
    * @param verb
    *   Print progress to the console?
    *
    * @return
    *   An equally weighted sample from the ABC-SMC parameter posterior,
    *   approximately of size `N`
    */
  def smc[P](
      N: Int,
      rprior: => P,
      dprior: P => LogLik,
      rdist: P => Double,
      rperturb: P => P,
      dperturb: (P, P) => LogLik,
      factor: Int = 10,
      steps: Int = 15,
      verb: Boolean = false
  ): ParSeq[P] = {
    val priorLW = collection.immutable.Vector.fill(N)(math.log(1.0 / N)).par
    val priorSample = priorLW map (w => rprior)
    @annotation.tailrec
    def go(samp: ParSeq[P], lw: ParSeq[LogLik], n: Int): ParSeq[P] = {
      if (verb) println(n)
      if (n == 0) {
        val idx = Mll.sample(N, exp(DenseVector(lw.toArray))).par
        idx map (samp(_))
      } else {
        val out = smcStep(dprior, samp, lw, rdist, rperturb, dperturb, factor)
        go(out._1, out._2, n - 1)
      }
    }
    go(priorSample, priorLW, steps)
  }

  /** Generate some basic diagnostics associated with an ABC run. Called purely
    * for the side effect of generating output on the console.
    *
    * @param output
    *   Collection of runs (and distances) such as generated from `run`
    */
  def summary[P: CsvRow, D](output: ParSeq[(P, D)]): Unit = {
    Mcmc.summary(Mcmc.toDMD(output.seq map (_._1)))
  }

}

// eof
