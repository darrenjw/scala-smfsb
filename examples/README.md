# scala-smfsb Examples directory

### Directory containing runnable examples illustrating use of the scala-smfsb library

Just doing `sbt run` from **this** directory should build and run the examples. That is (assuming you have `sbt` installed and working), you should clone or fork the repo and then execute `sbt run` from your OS command line, with the current working directory set to *inside* the `examples` directory:
```bash
scala-smfsb/examples$ sbt run
```
Then just select the number of the example you want to run.

## Examples

### Basics

* [Tutorial](src/main/scala/Tutorial.scala) - Examples from the [tutorial](../docs/Tutorial.md)

### Spatial reaction-diffusion simulation

* [Gillespie1d](src/main/scala/Gillespie1d.scala) - 1d exact spatial Gillespie simulation
* [Cle1d](src/main/scala/Cle1d.scala) - 1d spatial CLE simulation
* [Gillespie2d](src/main/scala/Gillespie2d.scala) - 2d exact spatial Gillespie simulation
* [Cle2d](src/main/scala/Cle2d.scala) - 2d spatial CLE simulation

### Bayesian parameter inference using time course data

* [PMCMC](src/main/scala/PMCMC.scala) - Parameter estimation from data using a PMMH particle MCMC algorithm (applied to a LV model)
* [AbcLv](src/main/scala/AbcLv.scala) - A naive rejection ABC algorithm for the LV model based on Euclidean distance between trajectories
* [AbcSsLv](src/main/scala/AbcSsLv.scala) - A rejection ABC algorithm using summary statistics
* [AbcSmcLv](src/main/scala/AbcSmcLv.scala) - An ABC-SMC algorithm using summary statistics



#### eof

