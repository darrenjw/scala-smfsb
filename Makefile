# Makefile


FORCE:
	sbt test package

docs: FORCE
	sbt doc
	cp -r target/scala-2.12/api/* docs/api/
	git add docs/api

todo:
	grep "TODO:" src/main/scala/smfsb/*.scala

edit:
	emacs src/main/scala/smfsb/*.scala src/test/scala/*.scala examples/src/main/scala/*.scala &



