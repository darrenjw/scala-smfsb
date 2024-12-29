# Makefile

SCALA_VERSION=3.3.4

FORCE:
	sbt test package

docs: FORCE
	make api
	make mdoc

api:
	sbt doc
	cp -r target/scala-$(SCALA_VERSION)/api/* docs/
	git add docs/*

mdoc:
	sbt mdoc
	git add docs/*.md

format:
	scalafmt

todo:
	grep "TODO:" src/main/scala/smfsb/*.scala

edit:
	emacs src/main/scala/smfsb/*.scala src/test/scala/*.scala examples/src/main/scala/*.scala &



