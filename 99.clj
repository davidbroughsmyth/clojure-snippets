; Run length encoding
(defn last-in-list [x]
  ((fn [x last]
     (if (nil? x)
       last
       (recur (rest x) (first x)))) x nil))

(defn last-but-one-in-list [x]
  ((fn [x last]
     (if (nil? (rest (rest x)))
       last
       (recur (rest x) (first (rest x))))) x nil))

(defn element-at [x n]
  (if (= n 0)
    (first x)
    (recur (rest x) (dec n))))

(defn length [x]
  ((fn [x acc]
     (if (nil? x)
       acc
       (recur (rest x) (inc acc)))) x 0))

(defn my-reverse [x]
  ((fn [list acc]
     (if (nil? list)
       acc
       (recur (rest list) (cons (first list) acc)))) x nil))

(defn palindrome? [x]
  (= x (reverse x)))

(defn atom? [x]
  (or (nil? x) (not (seq? x))))

(defn my-flatten [list]
  (if (atom? list)
    list
    (if (atom? (first list))
      (cons (first list) (my-flatten (rest list)))
      (concat (my-flatten (first list)) (my-flatten (rest list))))))
   
(defn my-flatten2 [x]
  (if (atom? x)
    (list x)
    (mapcat my-flatten2 x)))

;; P08 eliminate consecutive duplicates of list elements
;; Ugly style
(defn eliminate-dupes [lst]
 ((fn [n last accum]
    (if (= nil n)
      accum
      (if (= (first n) last)
        (recur (rest n) last accum)
        (recur (rest n) (first n) (concat accum (list (first n))))))) lst nil '()))

;; Nicer functional style
(defn eliminate-dupes2 [lst]
 ((fn [n accum]
    (if (= nil n)
      accum
      (recur (drop-while (fn [x] (= x (first n))) n) 
	     (concat accum (list (first n)))))) lst nil))

;; P09 - pack consecutive duplicates of list elements into sublists
;; TODO should relaly use an accumulator
(defn pack-list [lst]
  (if (= lst nil)
    nil
    (cons (take-while (fn [x] (= x (first lst))) lst) 
	  (pack-list (drop-while (fn [x] (= x (first lst))) lst)))))

(defn pack-list2 [lst]
  ((fn [xs accum]
     (if (= xs nil)
       accum
       (recur (drop-while (fn [x] (= x (first xs))) xs) 
	      (concat accum (list (take-while (fn [x] (= x (first xs))) xs)))))) lst nil))


;; P10 - Run length encoding of sublists
(defn encode [lst]
  ((fn [xs accum]
     (if (= nil xs)
       accum
       (recur (rest xs) (concat accum (list (list (count (first xs)) (ffirst xs))))))) (pack-list lst) nil))

;; P11 - Modified run length encoding
(defn encode-modified [lst]
  ((fn [xs accum]
     (if (= nil xs)
       accum
       (recur (rest xs) 
	      (concat accum 
		      (list 
		       (if (= (count (first xs)) 1)
			 (ffirst xs)
			 (list (count (first xs)) (ffirst xs)))))))) (pack-list lst) nil))

;; P12 - Decode a runlength coded list
(defn decode [lst]
  ((fn [xs accum]
     (if (= nil xs)
       accum
       (recur (rest xs)
	      (if (list? (first xs))
		(concat accum (replicate (ffirst xs) (first (rfirst xs))))
		(concat accum (list (first xs))))))) lst nil))

;; P13 encode it directly
(defn encode-direct [lst]
  ((fn [xs accum]
     (if (= nil xs)
       accum
       (recur (drop-while (fn [x] (= x (first xs))) xs)
	      (let [items (take-while (fn [x] (= x (first xs))) xs)]
		(if (= 1 (count items))
		  (concat accum items)
		  (concat accum (list (list (count items) (first items))))))))) lst nil))

;; P14 Duplicate the elements of a list
(defn dupli [lst]
  (mapcat (fn [x] (list x x)) lst))

;; P15 Replicate the elements of a list a given number of times
(defn repli [lst n]
  (mapcat (fn [x] (replicate n x)) lst))

;; P16 Drop every nth element from a list
(defn drop-nth [lst n]
  ((fn [xs i accum]
     (if (= nil xs)
       accum
       (if (= 0 (rem i n))
	 (recur (rest xs) (inc i) accum)
	 (recur (rest xs) (inc i) (concat accum (list (first xs))))))) lst 1 nil))

;; P17 split a list into two parts
(defn split [lst n]
  (list (take n lst) (drop n lst)))

;; P18 extract a slice from a list
(defn slice [lst i n]
  (take (inc (- n i)) (drop (dec i) lst)))

;; P19 Rotate a list N places to the left
(defn rotate [lst n]
  (if (> n 0)
    (take (count lst) (drop n (cycle lst)))
    (take (count lst) (drop (- (count lst) (Math/abs n)) (cycle lst)))))

;; P20 Remove the kth element from the list
(defn remove-at [lst n]
  (concat (take (dec n) lst) (drop n lst)))

;; P21 - Insert an element at a given position into a list
(defn insert-at [lst elt n]
  (concat (take (dec n) lst) (list elt) (drop (dec n) lst)))

;; P22 - Create a list containing all integers within a given range
(defn my-range-lazy [start end]
  (when (< start end)
    (lazy-cons start (my-range-lazy (inc start) end))))

(defn my-range [start end]
  (if (> start end)
    (reverse (my-range-lazy end start))
    (my-range-lazy start end)))

;; P23 - Extract a given number of randomly selected elements from a list
;; Annoying inc / dec because remove-at is 1 based!
(defn rnd-select [lst n]
  (when (> n 0)
    (let [x (rand-int (count lst))]
      (lazy-cons (nth lst x) (rnd-select (remove-at lst (inc x)) (dec n))))))

;; P24 Select N different frombers from the set 1..m
(defn lotto-select [n rng]
  (rnd-select (range 1 rng) n))

;; P25 permute a list
(defn rnd-permu [lst]
  (let [length (count lst)]
    (when (> length 0)
      (let [x (rand-int length)]
	(lazy-cons (nth lst x) (rnd-permu (remove-at lst (inc x))))))))
     
;; P26 - Generate the combinations of K distinct objects chosen from N
;; Define recursively
(defn append-prefix [prefix lst-elements]
  (mapcat (fn [x] (list (concat prefix (list x)))) lst-elements))

(defn combination [n lst]
  (if (> n (count lst))
    nil
    (let [elem-list (split lst (dec n)) rlist (nthrest lst (dec n))]
      (concat (append-prefix (first elem-list) rlist) (combination n (rest lst))))))

;; P27 Group elements into disjoint subsets
;; Multinomial Coefficients
(defn group3 [lst sizes]
  (prn "Come back to this one!"))

;; P28 Sorting a list of lists according to length of sublists
(defn lsort [lst]
  (sort (fn [x y] (< (count x) (count y))) lst))

;; No idea where 29-3o are!

;; P31 Simple test for prime numbers
(defn isprime [p]
  (if (= p 2)
    true
    (and (> p 1)
	 (every? (fn [x] (not (zero? (rem p x)))) (range 2 (inc (Math/sqrt p)))))))

(defn sieve [x]
  (filter isprime (range 1 x)))

;; P32 Write the GCD algorithm for two positive numbers
(defn gcd [a b]
  (if (zero? b)
    a
    (recur b (rem a b))))

;; P33
(defn coprime [a b]
  (= (gcd a b) 1))

;; P34
(defn totient [x]
  (if (= x 1)
    1
    (count (filter (partial coprime x) (range 1 x)))))

;; P35 prime factors of a given positive integer
(defn prime-factors-helper [n]
  (reverse (filter (fn [x] (zero? (rem n x))) (sieve n))))

(defn prime-factors-fn [x nums]
  (if (nil? nums)
    nil
    (if (zero? (rem x (first nums)))
      (lazy-cons (first nums) (prime-factors-fn (/ x (first nums)) nums))
      (prime-factors-fn x (rest nums)))))

(defn prime-factors [x]
  (prime-factors-fn x (prime-factors-helper x)))	     

;; P36 Prime factors of a given positive integer + multiplicands
(defn prime-factors-mult [x]
  (let [y (prime-factors x)]
    (map (fn [x] (if (list? x) x (list x 1))) (encode-direct y))))

;; P37 Improved totient function
(defn totient-improved [m]
  (let [factors (prime-factors-mult m)]
    (reduce + (map 
	       (fn [x] 
		 (let [p (first x) m (second x)]
		   (* (dec p) (Math/pow p (dec m)))))
	       factors))))

;; P38

;; P39 A list of prime numbers
(defn primes [x y]
  (filter (fn [z] (< x z)) (sieve y)))

;; P40 Goldbach's conjecture


    
