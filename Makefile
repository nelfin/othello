ANT_BUILD := ./build
SUBPROJECTS := othello_reference

OTHELLO_DIR := othello-reference/
PROGS := jafar

default: $(PROGS);

jafar: $(SUBPROJECTS)
	$(ANT_BUILD)

othello_reference:
	cd $(OTHELLO_DIR) && make

clean:
	-cd $(OTHELLO_DIR) && make clean
	$(ANT_BUILD) clean

.PHONY: default clean $(PROGS) $(SUBPROJECTS)
