#  PeekInterpreter



## Grammar
````
block: { statement }
       | statement

statement: IF (expr) block ELSE block
           | expr

expr: comp-expr ((AND|OR) comp-expr)*
      | comp-expr ? block : block
            
    comp-expr: NOT comp-expr 
               | arith-expr (GT|LT|NE|EE|GTE|LTE) arith-expr

    arith-expr: term ((ADD|MIN) term)*

term: factor ((MULT|DIV) factor)*

factor: (PLUS|MINUS)* power

power: modulus (POW factor)*

modulus: call (MODULUS factor)*

call: atom (LPAREN (expr (COMMA exr)*)? RPAREN)?
        
atom: INT | DOUBLE | STRING | IDENTIFIER
      | LPARN expr RPAREN
      | (INCR | DECR)* IDENTIFIER (INCR | DECR)*
      | WHILE (expr) { block }
      | FOR (expr; expr; expr) { block } 
      | TYPE IDENITIFER EQ expr
      | IDENTIFIER EQ expr
      | FUNCTION TYPE IDENTIFIER 
        LPAREN (TYPE IDENTIFIER (COMMA TYPE IDENTIFIER)*)
        LBRACE block RBRACE 
````