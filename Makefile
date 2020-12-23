# Makefile


FORCE:
	sbt test package

docs: FORCE
	make api
	make mdoc

api:
	sbt doc
	cp -r target/scala-2.13/api/* docs/api/
	git add docs/api

mdoc:
	sbt mdoc
	git add docs/*.md

todo:
	grep "TODO:" src/main/scala/smfsb/*.scala

edit:
	emacs src/main/scala/smfsb/*.scala src/test/scala/*.scala examples/src/main/scala/*.scala &



