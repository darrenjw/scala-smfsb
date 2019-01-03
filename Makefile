# Makefile


FORCE:
	sbt test package

docs: FORCE
	make api
	make tut

api:
	sbt doc
	cp -r target/scala-2.12/api/* docs/api/
	git add docs/api

tut:
	sbt tut
	cp target/scala-2.12/tut/*.md docs/api/
	git add docs/*.md

todo:
	grep "TODO:" src/main/scala/smfsb/*.scala

edit:
	emacs src/main/scala/smfsb/*.scala src/test/scala/*.scala examples/src/main/scala/*.scala &



