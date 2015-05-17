# scheme_in_ruby.rb (2015-05-08 version 1.0.1)からテストコード部分を抽出し一部修正。
# SchemeInRubyTest.java から読み込みれて実行される。

#$programs_expects =
  [
   # test environment
   [[[:lambda, [:x],
      [:+, 
       [[:lambda, [:x], :x], 2],
       :x]], 
     1],
    3],
   # test let
   [[:let, [[:x, 2], [:y, 3]], [:+, :x, :y]],
    5],
   [[:let, [[:x, 2] , [:y, 3]], [[:lambda, [:x, :y], [:+, :x, :y]], :x, :y]],
     5],
   [[:let, [[:add, [:lambda, [:x, :y], [:+, :x, :y]]]], [:add, 2, 3]],
    5],
   # test if
   [[:if, [:>, 3, 2], 1, 0],
    1],
   # test letrec
   [[:letrec, 
     [[:fact,
       [:lambda, [:n], [:if, [:<, :n, 1], 1, [:*, :n, [:fact, [:-, :n, 1]]]]]]], 
     [:fact, 3]], 
    6],
   # test cond 
   [[:cond, 
    [[:<, 2, 1], 0],
    [[:<, 2, 1], 1],
    [:else, 1]], 
    1],
   # test define
   [[:define, [:length, :list], 
    [:if, [:null?, :list], 0, 
     [:+, [:length, [:cdr, :list]], 1]]], 
    :length], # nil],
   [[:length, [:list, 1, 2]],
    2],
   [[:define, [:id, :x], :x],
    :id], # nil],
   [[:id, 3],
    3],
   [[:define, :x, [:lambda, [:x], :x]],
    :x], # nil],
   [[:x, 3],
    3],
   [[:define, :x, 5],
    :x], # nil],
   [:x,
    5],
   # test set!
   [[:let, [[:x, 1]],
     [:let, [[:dummy, [:set!, :x, 2]]],
      :x]], 2],
   # test list
   [[:list, 1],
    [1]],
   # test repl
#   [parse('(define (length list) (if  (null? list) 0 (+ (length (cdr list)) 1)))'),
#    nil],
#   [parse('(length (list 1 2 3))'), 
#    3],
#   [parse('(letrec ((fact (lambda (n) (if (< n 1) 1 (* n (fact (- n 1))))))) (fact 3))'),
#    6],
#   [parse('(let ((x 1)) (let ((dummy (set! x 2))) x))'),
#    2],
   # test fixed point
   # fact(0) = 1
   [[:let, 
     [[:fact,
       [:lambda, [:n], [:if, [:<, :n, 1], 1, [:*, :n, [:fact, [:-, :n, 1]]]]]]], 
     [:fact, 0]], 1],
   # fact(1) = 1
   [[:let, 
     [[:fact,
       [:lambda, [:n], 
        [:if, [:<, :n, 1], 1, 
         [:*, :n, 
          [:let, 
           [[:fact,
             [:lambda, [:n], [:if, [:<, :n, 1], 1, [:*, :n, [:fact, [:-, :n, 1]]]]]]],
           [:fact, [:-, :n, 1]]]]]]]], 
     [:fact, 1]], 1],
   # fact(2) = 2
   [[:let, 
     [[:fact,
       [:lambda, [:n], 
        [:if, [:<, :n, 1], 1, 
         [:*, :n, 
          [:let, 
           [[:fact,
             [:lambda, [:n], [:if, [:<, :n, 1], 1, [:*, :n, [:fact, [:-, :n, 1]]]]]]],
           [:let, 
            [[:fact,
              [:lambda, [:n], [:if, [:<, :n, 1], 1, [:*, :n, [:fact, [:-, :n, 1]]]]]]],
            
            [:fact, [:-, :n, 1]]]]]]]]], 
     [:fact, 2]], 2],
   # closure
#   [parse(
#<<EOS
#(define (makecounter)
#  (let ((count 0))
#    (lambda ()
#      (let ((dummy (set! count (+ count 1))))
#	count))))
#EOS
#), nil],
#   [parse('(define inc (makecounter))'), nil],
#   [parse('(inc)'), 1],
#   [parse('(inc)'), 2],
  ]
