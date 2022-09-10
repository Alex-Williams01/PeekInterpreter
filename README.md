#  PeekInterpreter



## Grammar
````
block: { statement }
       | statement

statement: TYPE IDENITIFER EQ expr
           | IF (expr) block ELSE block
           | expr

expr: comp-expr ((AND|OR) comp-expr)*
      | comp-expr ? block : block
            
    comp-expr: NOT comp-expr 
               | arith-expr (GT|LT|NE|EE|GTE|LTE) arith-expr

    arith-expr: term ((ADD|MIN) term)*

term: factor ((MULT|DIV) factor)*

factor: (PLUS|MINUS)* power

power: atom (POW factor)*
        
atom: INT | DOUBLE | STRING | IDENTIFIER
      | LPARN expr RPAREN
      | (INCR | DECR)* IDENTIFIER (INCR | DECR)*
````