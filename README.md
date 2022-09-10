#  PeekInterpreter



## Grammar
````
block: statement

statement: expr

expr: TYPE IDENITIFER EQ expr
      | comp-expr ((AND|OR) comp-expr)*
            
    comp-expr: NOT comp-expr 
               | arith-expr (GT|LT|NE|EE|GTE|LTE) arith-expr

    arith-expr: term ((ADD|MIN) term)*

term: factor ((MULT|DIV) factor)*

factor: (PLUS|MINUS)* power

power: atom (POW factor)*
        
atom: INT | DOUBLE | STRING | IDENTIFIER
      | LPARN expr RPAREN
````