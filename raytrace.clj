;;; Implementation of ray tracing algorithm from ANSI Common Lisp
(import '(javax.swing JFrame JPanel)
        '(java.awt Color))

;; Bits for the modelling


;; Math Utility functions
(defn square [x] (* x x))

(defstruct point :x :y :z)

(defn magnitude [p]
  (Math/sqrt (+ (square (:x p)) (square (:y p)) (square (:z p)))))

(defn unit-vector [p]
  (let [d (magnitude p)]
    (struct point (/ (:x p) d) (/ (:y p) d) (/ (:z p) d))))

(defn point-subtract [p1 p2]
  (struct point 
	  (- (:x p1) (:x p2))
	  (- (:y p1) (:y p2))
	  (- (:z p1) (:z p2))))

(defn distance [p1 p2]
  (magnitude (point-subtract p1 p2)))


(defn minroot [a b c]
  (if (zero? a)
    (/ (- c) b)
    (let [disc (- (square b) (* 4 a c))]
      (if (> disc 0)
	(let [discroot (Math/sqrt disc)]
	  (min (/ (+ (- b) discroot) (* 2 a))
	       (/ (- (- b) discroot) (* 2 a))))))))

;; Ray tracing bits
(def eye (struct point 0 0 200))

(defstruct surface :color)

(defstruct sphere :color :radius :centre) ;; Clojure doesn't appear to support include?

(defn defsphere [point r c]
  (struct sphere c r point))

(defn sphere-normal [s pt]
  (let [c (:centre s)]
    (unit-vector (point-subtract c pt))))


;; UI
(def canvas (proxy [JPanel] []
  (paintComponent [g]
    (proxy-super paintComponent g)		  
    (.setColor g Color/RED)
    (.fillRect g 0 0 300 300))))

(defn raytraceapp []
  (let [frame (JFrame. "Ray Tracing")]
    (doto frame
      (.add canvas)
      (.setSize 300 300)
      (.setVisible true))))