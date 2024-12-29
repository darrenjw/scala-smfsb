pages = [{"l":"index.html#","e":false,"i":"","n":"scala-smfsb","t":"scala-smfsb","d":"","k":"static","x":""},
{"l":"smfsb.html#","e":false,"i":"","n":"smfsb","t":"smfsb","d":"","k":"package","x":""},
{"l":"smfsb.html#CsvRowSyntax-f10","e":false,"i":"","n":"CsvRowSyntax","t":"CsvRowSyntax[T](value: T): CsvRowSyntax[T]","d":"smfsb","k":"def","x":""},
{"l":"smfsb.html#DoubleState-0","e":false,"i":"","n":"DoubleState","t":"DoubleState = DenseVector[Double]","d":"smfsb","k":"type","x":""},
{"l":"smfsb.html#HazardVec-0","e":false,"i":"","n":"HazardVec","t":"HazardVec = DenseVector[Double]","d":"smfsb","k":"type","x":""},
{"l":"smfsb.html#IntState-0","e":false,"i":"","n":"IntState","t":"IntState = DenseVector[Int]","d":"smfsb","k":"type","x":""},
{"l":"smfsb.html#LogLik-0","e":false,"i":"","n":"LogLik","t":"LogLik = Double","d":"smfsb","k":"type","x":""},
{"l":"smfsb.html#ThinnableSyntax-fffff81a","e":false,"i":"","n":"ThinnableSyntax","t":"ThinnableSyntax[T, F[T]](value: F[T]): ThinnableSyntax[T, F]","d":"smfsb","k":"def","x":""},
{"l":"smfsb.html#Time-0","e":false,"i":"","n":"Time","t":"Time = Double","d":"smfsb","k":"type","x":""},
{"l":"smfsb.html#Ts-0","e":false,"i":"","n":"Ts","t":"Ts[S] = List[(Time, S)]","d":"smfsb","k":"type","x":""},
{"l":"smfsb.html#dvdState-0","e":false,"i":"","n":"dvdState","t":"dvdState: State[DoubleState]","d":"smfsb","k":"val","x":""},
{"l":"smfsb.html#dviState-0","e":false,"i":"","n":"dviState","t":"dviState: State[IntState]","d":"smfsb","k":"val","x":""},
{"l":"smfsb.html#streamThinnable-0","e":false,"i":"","n":"streamThinnable","t":"streamThinnable: Thinnable[LazyList]","d":"smfsb","k":"val","x":""},
{"l":"smfsb.html#tsdsDs-0","e":false,"i":"","n":"tsdsDs","t":"tsdsDs: DataSet[Ts[DoubleState]]","d":"smfsb","k":"val","x":""},
{"l":"smfsb.html#tsisDs-0","e":false,"i":"","n":"tsisDs","t":"tsisDs: DataSet[Ts[IntState]]","d":"smfsb","k":"val","x":""},
{"l":"smfsb/Abc$.html#","e":false,"i":"","n":"Abc","t":"Abc","d":"smfsb","k":"object","x":""},
{"l":"smfsb/Abc$.html#run-25f","e":false,"i":"","n":"run","t":"run[P, D](n: Int, rprior: => P, dist: P => D): ParSeq[(P, D)]","d":"smfsb.Abc","k":"def","x":""},
{"l":"smfsb/Abc$.html#smc-8ce","e":false,"i":"","n":"smc","t":"smc[P](N: Int, rprior: => P, dprior: P => LogLik, rdist: P => Double, rperturb: P => P, dperturb: (P, P) => LogLik, factor: Int, steps: Int, verb: Boolean): ParSeq[P]","d":"smfsb.Abc","k":"def","x":""},
{"l":"smfsb/Abc$.html#summary-1a2","e":false,"i":"","n":"summary","t":"summary[P : CsvRow, D](output: ParSeq[(P, D)]): Unit","d":"smfsb.Abc","k":"def","x":""},
{"l":"smfsb/CsvRow.html#","e":false,"i":"","n":"CsvRow","t":"CsvRow[T]","d":"smfsb","k":"trait","x":""},
{"l":"smfsb/CsvRow.html#toCsv-9f8","e":false,"i":"","n":"toCsv","t":"toCsv(value: T): String","d":"smfsb.CsvRow","k":"def","x":""},
{"l":"smfsb/CsvRow.html#toDvd-b9f","e":false,"i":"","n":"toDvd","t":"toDvd(value: T): DenseVector[Double]","d":"smfsb.CsvRow","k":"def","x":""},
{"l":"smfsb/CsvRowSyntax.html#","e":false,"i":"","n":"CsvRowSyntax","t":"CsvRowSyntax[T](value: T)","d":"smfsb","k":"class","x":""},
{"l":"smfsb/CsvRowSyntax.html#toCsv-e76","e":false,"i":"","n":"toCsv","t":"toCsv(implicit inst: CsvRow[T]): String","d":"smfsb.CsvRowSyntax","k":"def","x":""},
{"l":"smfsb/CsvRowSyntax.html#toDvd-4e1","e":false,"i":"","n":"toDvd","t":"toDvd(implicit inst: CsvRow[T]): DenseVector[Double]","d":"smfsb.CsvRowSyntax","k":"def","x":""},
{"l":"smfsb/DataSet.html#","e":false,"i":"","n":"DataSet","t":"DataSet[D]","d":"smfsb","k":"trait","x":""},
{"l":"smfsb/MarkedSpn.html#","e":false,"i":"","n":"MarkedSpn","t":"MarkedSpn[S](species: List[String], m: S, pre: DenseMatrix[Int], post: DenseMatrix[Int], h: (S, Time) => HazardVec)(implicit evidence$1: State[S]) extends Spn[S]","d":"smfsb","k":"class","x":""},
{"l":"smfsb/Mcmc$.html#","e":false,"i":"","n":"Mcmc","t":"Mcmc","d":"smfsb","k":"object","x":""},
{"l":"smfsb/Mcmc$.html#acf-3f6","e":false,"i":"","n":"acf","t":"acf(x: Seq[Double], lm: Int): DenseVector[Double]","d":"smfsb.Mcmc","k":"def","x":""},
{"l":"smfsb/Mcmc$.html#mhStream-fffff1e9","e":false,"i":"","n":"mhStream","t":"mhStream[P](init: P, logLik: P => LogLik, rprop: P => P, dprop: (P, P) => LogLik, dprior: P => LogLik, verb: Boolean): LazyList[P]","d":"smfsb.Mcmc","k":"def","x":""},
{"l":"smfsb/Mcmc$.html#nextValue-fffff674","e":false,"i":"","n":"nextValue","t":"nextValue[P](logLik: P => LogLik, rprop: P => P, dprop: (P, P) => LogLik, dprior: P => LogLik, verb: Boolean)(current: (P, LogLik)): (P, LogLik)","d":"smfsb.Mcmc","k":"def","x":""},
{"l":"smfsb/Mcmc$.html#summary-fffff0a3","e":false,"i":"","n":"summary","t":"summary(m: DenseMatrix[Double], plt: Boolean, lm: Int): Unit","d":"smfsb.Mcmc","k":"def","x":""},
{"l":"smfsb/Mcmc$.html#summary-fffff359","e":false,"i":"","n":"summary","t":"summary[P : CsvRow](s: Seq[P], plot: Boolean): Unit","d":"smfsb.Mcmc","k":"def","x":""},
{"l":"smfsb/Mcmc$.html#toDMD-8fb","e":false,"i":"","n":"toDMD","t":"toDMD[P : CsvRow](s: Seq[P]): DenseMatrix[Double]","d":"smfsb.Mcmc","k":"def","x":""},
{"l":"smfsb/Mll$.html#","e":false,"i":"","n":"Mll","t":"Mll","d":"smfsb","k":"object","x":""},
{"l":"smfsb/Mll$.html#bpf-fffff13c","e":false,"i":"","n":"bpf","t":"bpf[S : State, O](x0: Vector[S], t0: Time, data: Ts[O], dataLik: (S, O) => LogLik, stepFun: (S, Time, Time) => S): (LogLik, Time, Vector[S])","d":"smfsb.Mll","k":"def","x":""},
{"l":"smfsb/Mll$.html#pfMll-c22","e":false,"i":"","n":"pfMll","t":"pfMll[P, S : State, O](simX0: P => Vector[S], t0: Time, stepFun: P => (S, Time, Time) => S, dataLik: P => (S, O) => LogLik, data: Ts[O]): P => LogLik","d":"smfsb.Mll","k":"def","x":""},
{"l":"smfsb/Mll$.html#sample-8cc","e":false,"i":"","n":"sample","t":"sample(n: Int, prob: DenseVector[Double]): Vector[Int]","d":"smfsb.Mll","k":"def","x":""},
{"l":"smfsb/PMatrix.html#","e":false,"i":"","n":"PMatrix","t":"PMatrix[T](x: Int, y: Int, r: Int, c: Int, data: ParVector[T])","d":"smfsb","k":"class","x":""},
{"l":"smfsb/PMatrix.html#apply-573","e":false,"i":"","n":"apply","t":"apply(x: Int, y: Int): T","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#coflatMap-fffff014","e":false,"i":"","n":"coflatMap","t":"coflatMap[S](f: (PMatrix[T]) => S): PMatrix[S]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#down-0","e":false,"i":"","n":"down","t":"down: PMatrix[T]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#extract-0","e":false,"i":"","n":"extract","t":"extract: T","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#left-0","e":false,"i":"","n":"left","t":"left: PMatrix[T]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#map-fffff014","e":false,"i":"","n":"map","t":"map[S](f: T => S): PMatrix[S]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#right-0","e":false,"i":"","n":"right","t":"right: PMatrix[T]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#up-0","e":false,"i":"","n":"up","t":"up: PMatrix[T]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#updated-107","e":false,"i":"","n":"updated","t":"updated(x: Int, y: Int, value: T): PMatrix[T]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix.html#zip-fffffb59","e":false,"i":"","n":"zip","t":"zip[S](m: PMatrix[S]): PMatrix[(T, S)]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix$.html#","e":false,"i":"","n":"PMatrix","t":"PMatrix","d":"smfsb","k":"object","x":""},
{"l":"smfsb/PMatrix$.html#apply-710","e":false,"i":"","n":"apply","t":"apply[T](r: Int, c: Int, data: Seq[T]): PMatrix[T]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix$.html#fromBDM-3d3","e":false,"i":"","n":"fromBDM","t":"fromBDM[T](m: DenseMatrix[T]): PMatrix[T]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PMatrix$.html#toBDM-2fc","e":false,"i":"","n":"toBDM","t":"toBDM(m: PMatrix[Double]): DenseMatrix[Double]","d":"smfsb.PMatrix","k":"def","x":""},
{"l":"smfsb/PVector.html#","e":false,"i":"","n":"PVector","t":"PVector[T](cur: Int, vec: ParVector[T])","d":"smfsb","k":"class","x":""},
{"l":"smfsb/PVector.html#apply-aba","e":false,"i":"","n":"apply","t":"apply(x: Int): T","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/PVector.html#back-0","e":false,"i":"","n":"back","t":"back: PVector[T]","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/PVector.html#coflatMap-fffff996","e":false,"i":"","n":"coflatMap","t":"coflatMap[S](f: (PVector[T]) => S): PVector[S]","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/PVector.html#extract-0","e":false,"i":"","n":"extract","t":"extract: T","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/PVector.html#forward-0","e":false,"i":"","n":"forward","t":"forward: PVector[T]","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/PVector.html#length-0","e":false,"i":"","n":"length","t":"length: Int","d":"smfsb.PVector","k":"val","x":""},
{"l":"smfsb/PVector.html#map-fffff996","e":false,"i":"","n":"map","t":"map[S](f: T => S): PVector[S]","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/PVector.html#updated-fffff090","e":false,"i":"","n":"updated","t":"updated(x: Int, value: T): PVector[T]","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/PVector.html#zip-dd","e":false,"i":"","n":"zip","t":"zip[S](y: PVector[S]): PVector[(T, S)]","d":"smfsb.PVector","k":"def","x":""},
{"l":"smfsb/Sim$.html#","e":false,"i":"","n":"Sim","t":"Sim","d":"smfsb","k":"object","x":""},
{"l":"smfsb/Sim$.html#plotTs-fffff106","e":false,"i":"","n":"plotTs","t":"plotTs[S : State](ts: Ts[S], title: String): Unit","d":"smfsb.Sim","k":"def","x":""},
{"l":"smfsb/Sim$.html#sample-fffff126","e":false,"i":"","n":"sample","t":"sample[S : State](n: Int, x0: S, t0: Time, deltat: Time, stepFun: (S, Time, Time) => S): List[S]","d":"smfsb.Sim","k":"def","x":""},
{"l":"smfsb/Sim$.html#times-fffff1a0","e":false,"i":"","n":"times","t":"times[S : State](x0: S, t0: Time, timeList: List[Time], stepFun: (S, Time, Time) => S): Ts[S]","d":"smfsb.Sim","k":"def","x":""},
{"l":"smfsb/Sim$.html#toCsv-fffff4ba","e":false,"i":"","n":"toCsv","t":"toCsv[S : State](ts: Ts[S]): String","d":"smfsb.Sim","k":"def","x":""},
{"l":"smfsb/Sim$.html#ts-d2e","e":false,"i":"","n":"ts","t":"ts[S](x0: S, t0: Time, tt: Time, dt: Time, stepFun: (S, Time, Time) => S): Ts[S]","d":"smfsb.Sim","k":"def","x":""},
{"l":"smfsb/Spatial$.html#","e":false,"i":"","n":"Spatial","t":"Spatial","d":"smfsb","k":"object","x":""},
{"l":"smfsb/Spatial$.html#cle1d-17c","e":false,"i":"","n":"cle1d","t":"cle1d(n: Spn[DoubleState], d: DoubleState, dt: Double): (Seq[DoubleState], Time, Time) => Seq[DoubleState]","d":"smfsb.Spatial","k":"def","x":""},
{"l":"smfsb/Spatial$.html#cle2d-17c","e":false,"i":"","n":"cle2d","t":"cle2d(n: Spn[DoubleState], d: DoubleState, dt: Double): (PMatrix[DoubleState], Time, Time) => PMatrix[DoubleState]","d":"smfsb.Spatial","k":"def","x":""},
{"l":"smfsb/Spatial$.html#euler1d-17c","e":false,"i":"","n":"euler1d","t":"euler1d(n: Spn[DoubleState], d: DoubleState, dt: Double): (Seq[DoubleState], Time, Time) => Seq[DoubleState]","d":"smfsb.Spatial","k":"def","x":""},
{"l":"smfsb/Spatial$.html#euler2d-17c","e":false,"i":"","n":"euler2d","t":"euler2d(n: Spn[DoubleState], d: DoubleState, dt: Double): (PMatrix[DoubleState], Time, Time) => PMatrix[DoubleState]","d":"smfsb.Spatial","k":"def","x":""},
{"l":"smfsb/Spatial$.html#gillespie1d-fffffd43","e":false,"i":"","n":"gillespie1d","t":"gillespie1d(n: Spn[IntState], d: DoubleState, minH: Double, maxH: Double): (Seq[IntState], Time, Time) => Seq[IntState]","d":"smfsb.Spatial","k":"def","x":""},
{"l":"smfsb/Spatial$.html#gillespie2d-fffffd43","e":false,"i":"","n":"gillespie2d","t":"gillespie2d(n: Spn[IntState], d: DoubleState, minH: Double, maxH: Double): (PMatrix[IntState], Time, Time) => PMatrix[IntState]","d":"smfsb.Spatial","k":"def","x":""},
{"l":"smfsb/Spatial$.html#plotTs1d-b61","e":false,"i":"","n":"plotTs1d","t":"plotTs1d[S : State](ts: Ts[Seq[S]]): Unit","d":"smfsb.Spatial","k":"def","x":""},
{"l":"smfsb/Spn.html#","e":false,"i":"","n":"Spn","t":"Spn[S]","d":"smfsb","k":"trait","x":""},
{"l":"smfsb/Spn.html#h-0","e":false,"i":"","n":"h","t":"h: (S, Time) => HazardVec","d":"smfsb.Spn","k":"def","x":""},
{"l":"smfsb/Spn.html#post-0","e":false,"i":"","n":"post","t":"post: DenseMatrix[Int]","d":"smfsb.Spn","k":"def","x":""},
{"l":"smfsb/Spn.html#pre-0","e":false,"i":"","n":"pre","t":"pre: DenseMatrix[Int]","d":"smfsb.Spn","k":"def","x":""},
{"l":"smfsb/Spn.html#species-0","e":false,"i":"","n":"species","t":"species: List[String]","d":"smfsb.Spn","k":"def","x":""},
{"l":"smfsb/SpnModels$.html#","e":false,"i":"","n":"SpnModels","t":"SpnModels","d":"smfsb","k":"object","x":""},
{"l":"smfsb/SpnModels$.html#ar-489","e":false,"i":"","n":"ar","t":"ar[S : State](c: DenseVector[Double]): Spn[S]","d":"smfsb.SpnModels","k":"def","x":""},
{"l":"smfsb/SpnModels$.html#id-489","e":false,"i":"","n":"id","t":"id[S : State](p: DenseVector[Double]): Spn[S]","d":"smfsb.SpnModels","k":"def","x":""},
{"l":"smfsb/SpnModels$.html#lv-489","e":false,"i":"","n":"lv","t":"lv[S : State](p: DenseVector[Double]): Spn[S]","d":"smfsb.SpnModels","k":"def","x":""},
{"l":"smfsb/SpnModels$.html#lv4-489","e":false,"i":"","n":"lv4","t":"lv4[S : State](p: DenseVector[Double]): Spn[S]","d":"smfsb.SpnModels","k":"def","x":""},
{"l":"smfsb/SpnModels$.html#mm-489","e":false,"i":"","n":"mm","t":"mm[S : State](p: DenseVector[Double]): Spn[S]","d":"smfsb.SpnModels","k":"def","x":""},
{"l":"smfsb/SpnModels$.html#seir-489","e":false,"i":"","n":"seir","t":"seir[S : State](p: DenseVector[Double]): Spn[S]","d":"smfsb.SpnModels","k":"def","x":""},
{"l":"smfsb/SpnModels$.html#sir-489","e":false,"i":"","n":"sir","t":"sir[S : State](p: DenseVector[Double]): Spn[S]","d":"smfsb.SpnModels","k":"def","x":""},
{"l":"smfsb/State.html#","e":false,"i":"","n":"State","t":"State[S] extends CsvRow[S]","d":"smfsb","k":"trait","x":""},
{"l":"smfsb/Step$.html#","e":false,"i":"","n":"Step","t":"Step","d":"smfsb","k":"object","x":""},
{"l":"smfsb/Step$.html#cle-fffffaa6","e":false,"i":"","n":"cle","t":"cle(n: Spn[DoubleState], dt: Double): (DoubleState, Time, Time) => DoubleState","d":"smfsb.Step","k":"def","x":""},
{"l":"smfsb/Step$.html#euler-fffffaa6","e":false,"i":"","n":"euler","t":"euler(n: Spn[DoubleState], dt: Double): (DoubleState, Time, Time) => DoubleState","d":"smfsb.Step","k":"def","x":""},
{"l":"smfsb/Step$.html#gillespie-76d","e":false,"i":"","n":"gillespie","t":"gillespie(n: Spn[IntState], minH: Double, maxH: Double): (IntState, Time, Time) => IntState","d":"smfsb.Step","k":"def","x":""},
{"l":"smfsb/Step$.html#pts-fffffaa6","e":false,"i":"","n":"pts","t":"pts(n: Spn[IntState], dt: Double): (IntState, Time, Time) => IntState","d":"smfsb.Step","k":"def","x":""},
{"l":"smfsb/Thinnable.html#","e":false,"i":"","n":"Thinnable","t":"Thinnable[F[_]]","d":"smfsb","k":"trait","x":""},
{"l":"smfsb/Thinnable.html#thin-fffff776","e":false,"i":"","n":"thin","t":"thin[T](f: F[T], th: Int): F[T]","d":"smfsb.Thinnable","k":"def","x":""},
{"l":"smfsb/ThinnableSyntax.html#","e":false,"i":"","n":"ThinnableSyntax","t":"ThinnableSyntax[T, F[T]](value: F[T])","d":"smfsb","k":"class","x":""},
{"l":"smfsb/ThinnableSyntax.html#thin-84c","e":false,"i":"","n":"thin","t":"thin(th: Int)(implicit inst: Thinnable[F]): F[T]","d":"smfsb.ThinnableSyntax","k":"def","x":""},
{"l":"smfsb/UnmarkedSpn.html#","e":false,"i":"","n":"UnmarkedSpn","t":"UnmarkedSpn[S](species: List[String], pre: DenseMatrix[Int], post: DenseMatrix[Int], h: (S, Time) => HazardVec)(implicit evidence$1: State[S]) extends Spn[S]","d":"smfsb","k":"class","x":""}];