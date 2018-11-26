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
  def run[P](n: Int, rprior: => P, dist: P => Double): GenSeq[(P, Double)] = {
    (1 to n).par.map(i => {
      val p = rprior
      (p,dist(p))
    })
  }

  /**
    * Generate some basic diagnostics associated with an ABC run.
    * Called purely for the side effect of generating output on the
    * console. 
    * 
    * @param output Collection of runs (and distances) such as generated
    * from `run`
    */
  def summary[P: CsvRow](output: GenSeq[(P, Double)]): Unit = {
    Mcmc.summary(Mcmc.toDMD(output map (_._1)))
  }


}

// eof

