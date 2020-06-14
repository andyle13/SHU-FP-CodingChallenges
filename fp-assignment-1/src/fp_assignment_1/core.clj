(ns fp-assignment-1.core
  (:gen-class)
  (:require [clojure.spec.alpha :as s])
  ; Include [net.mikera/core.matrix "0.62.0"] in project.clj.
  ;   This project uses the following matrix functions
  ;     reshape       Segregates a vector into a [row columns] matrix
  ;     transpose     Transposes a matrix
  ;     row-count     Counts the rows of a matrix
  ;     column-count  Counts the column of a matrix
  ;     ecount        Counts every element inside a matrix
  ;     to-vector     Converts a matrix into a vector
  (:require [clojure.core.matrix :refer :all :as m])
  ; Include [cheshire "5.9.0"] in project.clj
  ;   This project uses the following cheshire functions
  ;     parse-string  Converts slurped dataset into a map or parses strings
  (:require [cheshire.core :refer :all :as c]))

(use 'clojure.test)

(comment "PREAMBLE - SEE COMMENT BELOW")
(comment "STRUCTURING
          The coding convention for this assignment follows a consistent structure across every task and their testing
          to ensure that the code is well-readable and well-structured. To accomplish this, each task contains let
          blocks for variables and functions that are primarily intended for solving each task. Prior to assigning
          values, each variable or function name may be followed by a comment describing the meaning of its intent. The
          value assignation as well as the comments of each variable and function is heightened on the same column-level
          to enhance visual readability. The reason as to why functions are let-based is that these functions private
          and only accessible within a contained task. It prevents global modularity across the code but therefore
          enables on a task-based level as certain functions should only be reusable from within with the goal of
          achieving its intended functionality. External libraries have been aliased with single letter so that each of
          their abstractions are denoted by (a/abstraction) for the sake of readability. That was crucial due to the
          fact that these abstractions have to be identified from the originating source in order to compartmentalise
          abstractions from open-source libraries from stock Clojure.

          PROGRAMMING STYLE
          The programming style applied for each task is goal-oriented based on the meaning of data with emphasis of
          functional consistency. Goal-orientation predicates a strategy laid out for solving a task on an abstraction
          level as to what data is required in order to produce the intended output.

          PROGRAMMING PHILOSOPHY
          Abstraction predicates the way of thinking abstract in which superfluous details are disregarded. The benefit
          of abstract thinking enhances the way of programming in a simple style by removing understanding on a
          technical level in favour of promoting a goal-oriented approach that is derived from a non-technical question:
          What must be accomplished in order to achieve the primary goal? Each problem may have subproblems that must be
          solved in order to solve the main problem. Putting abstraction before implementation enhances functional
          thinking due to the fact that it facilitates the segmentation of responsibilities by which functions follow a
          single-responsibility pattern that is reusable. Reusability is key in functional programming and it is vital
          to take advantage of defined abstractions in Clojure to reduce writing a plethora of lines that may reduceable
          to just a line. The style of abstract thinking was influence by Imperial College London Professor Jeff
          Kramer's article \"Is abstraction key to computing?\".
          (https://www.ics.uci.edu/~andre/informatics223s2007/kramer.pdf)

          PROGRAMMING STRATEGY
          The programming style applied follows a data-oriented strategy that complements the goal-oriented style
          as outlined in this preamble. It can be enhanced by applying a data science methodology in exploiting
          the meaning of data in order to produce the result based on what already exists. That means that a variety of
          operations for data preprocessing such as extracting, cleaning, transforming, and reducing have been carried
          out in order to locate the data as required for producing the output. This style of data preprocessing has
          been inspired by the R language library dplyr. This is widely noticeable in Tasks 2 (Matrix), 3 and 4 where
          subsets of an entire dataset have been used to iteratively preprocess them bulk-wise. For example, in Task 2
          where two Matrices have to be conjoined together but in a way in which their internal vectors are combined one
          by one, or in Task 3 where a string is used in order to determine the flower set of a child based on their
          initial which saw a character-based segregation of it for mapping each flower name into a list, or in Task 4
          where a dataset is pulled from the NASA database in order to answer a multitude of questions by reducing and
          splitting the data for producing the correct answer. List comprehension with the (for) functions has been
          sparingly used in Tasks 1 and 2 (Multy-arity) in order to demonstrate a means of transforming a dataset.
          However, preference-wise it is archaic when Clojure offers sophisticated functions such as (map), (reduce),
          (filter), or (transduce) where the use of list comprehension has become redundant across the entire code.
          To maximise the use of dataset transformations with reducer and transformer functions, threading macros
          (->> or ->) have been used in order to provide a sequentially logical order of execution. It would remove the
          chain rule of nested functions where the most-inner function will be executed first until it has reached the
          most outer one in favour of linear flow. This in turn will improve readability. However, the choice of
          predominantly using threading macros was essentially preference-based due to the data science methodology
          applied as threading macros provide a means of pipelining. Pipelining is a style of transforming a dataset
          based on a sequence of operations in which the result of each operation parameterises itself into the next
          one. In the R language, pipelining is an integral part of the dplyr library where it is predominantly used for
          transforming datasets with the macro (%>%). For producing the result for each task (except Task 1), each
          result has been aggregated in a map. Usually in API development, developers will receive a set of data
          tailored for their needs such as transforming, formatting, etc. However, the main intent behind the
          aggregation is reusing the data for formatting the result in a user-friendly way so that users will learn
          about what the data represents as a full sentence for UX purposes as formatting is carried in the -main
          function. To reiterate the core concepts behind the strategy used, programming should be simple and
          reusably compartmentalised by responsibility based on the meaning of data whilst putting the main emphasis
          on data fluency and abstractions.

          TESTING
          The testing of internal functions is disrupted from the most outer scope due to the internalisation of
          task-specified functions that are defined in let-blocks. Due to that limitation, only functions defined in
          global scope can be tested. Most functions have been functionally tested from a developer's point of view,
          therefore the only way of providing unit tests was task-oriented by testing each task function as an entire
          abstraction. Each testing is wrapped or grouped inside a (testing) function that is followed by a title
          specifying specific test cases in order to improve readability. To enhance the constraint of each function,
          \"specs\" have been used in order to embed assertions to ensure whether or not inputs and output conform
          with the specified data type. That is important as it must be guaranteed that non-conformative data types do
          not break the program as it is intended to output the correct result.")

(defn task_four
  [style]
  {:pre  [(s/valid? string? style)]
   :post [(or (s/valid? map? %)
              (s/valid? string? %))]}
  (let
    [dataset                         ; scrapes the y77d-th95 dataset from the NASA database.
                                     (c/parse-string
                                       (slurp "https://data.nasa.gov/resource/y77d-th95.json"))
     filter_by_fell                  ; filter_by_fell is a function that filters the dataset so that it contains
                                     ; entries where meteorites have already been fallen.
                                     (fn
                                       [ds]
                                       {:pre  [(s/valid? coll? ds)]
                                        :post [(s/valid? coll? %)]}
                                       (filter #(= (get % "fall") "Fell") ds))
     get_max_value                   ; get_max_value is a function that takes a dataset as parameter in order to
                                     ; determine its maximum value.
                                     (fn
                                       [ds]
                                       {:pre  [(s/valid? map? ds)]
                                        :post [(s/valid? integer? %)]}
                                       (->> ds
                                            (vals)
                                            (apply max)))
     get_max_val_key                 ; get_max_val_key is a function that takes a dataset and a value that predicates
                                     ; some value of the dataset in order to locate its corresponding key.
                                     (fn
                                       [ds v]
                                       {:pre  [(s/valid? map? ds)
                                               (s/valid? number? v)]
                                        :post [(s/valid? string? %)]}
                                       (->> ds
                                            (keys)
                                            (filter (comp #{v} ds))
                                            (apply str)))
     sum_up_mass                     ; sum_up_mass is a function that takes a vectorial subset of the dataset as input
                                     ; in order to aggregate the mass of every entry.
                                     (fn
                                       [v_ds]
                                       {:pre  [(s/valid? vector? v_ds)]
                                        :post [(s/valid? number? %)]}
                                       (->> v_ds
                                            (map #(parse-string (% "mass")))
                                            (#(filter number? %))
                                            (reduce +)))
     ; https://data.nasa.gov/resource/y77d-th95.json?$select=year,count(fall)&$group=year&$where=fall=%22Fell%22&$order=count_fall%20DESC
     get_year_highest_meteor_fall    (fn
                                       []
                                       {:post [(s/valid? map? %)]}
                                       (let
                                         [ds_filtered         ; filters the dataset by the frequency of years
                                                              ; and retnurns it as a map.
                                                              (->> dataset
                                                                   (filter_by_fell)
                                                                   (map #(get % "year"))
                                                                   (frequencies))
                                          max_count_fall      (get_max_value ds_filtered)
                                          year_max_count_fall (get_max_val_key ds_filtered max_count_fall)]
                                         { "max_fall_year" (subs year_max_count_fall 0 4) "max_fall" max_count_fall }))
     ; https://data.nasa.gov/resource/y77d-th95.json?$select=year,fall,sum(mass)&$where=fall=%22Fell%22&$group=year,fall&$order=sum_mass%20desc
     get_year_heaviest_meteor_fall   (fn
                                       []
                                       {:post [(s/valid? map? %)]}
                                       (let
                                         [ds_filtered   ; filters the dataset by year and mass and returns it as a
                                                        ; map. The map is grouped by year, so each key represents a
                                                        ; year where each entry by year is associated with it.
                                                        (->> dataset
                                                             (filter_by_fell)
                                                             (map #(select-keys % ["year" "mass"]))
                                                             (group-by #(% "year")))
                                          key_list      ; divides the dataset into keys of years as a lazy sequence.
                                                        (map #(key %) ds_filtered)
                                          value_list    ; divides the dataset into values where the values
                                                        ; of each mass has been aggregated.
                                                        (map #(sum_up_mass (val %)) ds_filtered)
                                          merged_list   ; merges the divided dataset as a wholly cleaned map.
                                                        (zipmap key_list value_list)
                                          heaviest      (get_max_value merged_list)
                                          year_heaviest (get_max_val_key merged_list heaviest)]
                                         { "heaviest_year" (subs year_heaviest 0 4) "heaviest" heaviest }))
     get_2000_plus_avg_meteor_falls  (fn
                                       []
                                       {:post [(s/valid? map? %)]}
                                       (let
                                         [ds_filtered         ; filters the dataset by mass and year starting with
                                                              ; 2 as in 2000 and later.
                                                              (->> dataset
                                                                   (filter_by_fell)
                                                                   (filter #(re-seq #"^2" (get % "year")))
                                                                   (map #(select-keys % ["year" "mass"])))
                                          count_distinct_year ; extracts all the years distinctly and aggregates the
                                                              ; the total number of years from the dataset.
                                                              (->> ds_filtered
                                                                   (map #(get % "year"))
                                                                   (distinct)
                                                                   (count))
                                          mass                ; groups the dataset by year and tranduces the total
                                                              ; amount of mass.
                                                              (->> ds_filtered
                                                                   (group-by #(% "year"))
                                                                   (transduce (map #(sum_up_mass (val %))) +))
                                          falls               ; counts the total amount of meteor falls.
                                                              (count ds_filtered)
                                          avg_mass            ; calculates the average mass per distinct year
                                                              (double (/ mass count_distinct_year))
                                          avg_falls           ; calculates the average frequency per distinct year
                                                              (double (/ falls count_distinct_year))]
                                         { "avg_mass" avg_mass "avg_falls" avg_falls }))
     get_computed_regions_meteor_falls (fn
                                         []
                                         {:post [(s/valid? map? %)]}
                                         (let
                                           [is_in_vector            ; is_in_vector is a function that returns a lazy
                                                                    ; sequence based on the condition whether or not the
                                                                    ; dataset contains a set of predicates (e.g. name of
                                                                    ; keys). distinct is used in this function for only
                                                                    ; taking taking predicates into consideration that
                                                                    ; "computed_region" since there are two types of
                                                                    ; computed regions in the NASA dataset.
                                                                    (fn
                                                                      [ds cond_vec]
                                                                      {:pre  [(s/valid? coll? ds)
                                                                              (s/valid? coll? cond_vec)]
                                                                       :post [(s/valid? vector? %)]}
                                                                      (->> cond_vec
                                                                           (map #(contains? ds %))
                                                                           (distinct)
                                                                           (vec)))
                                            ds_filtered             ; filters the dataset based on the condition whether
                                                                    ; or not meteorites have fallen.
                                                                    (filter_by_fell dataset)
                                            comp_region_predicates  ; filters the dataset by keys whose names contain
                                                                    ; the substring "computed_region". The map operation
                                                                    ; will return all the key names inside parenthesis,
                                                                    ; therefore using flatten is appropriate to have all
                                                                    ; the elements stored in one vector in order to
                                                                    ; perform distinct to filter each key uniquely. This
                                                                    ; enables the opportunity to retrieve the desired
                                                                    ; predicates distinctly.
                                                                    (->> ds_filtered
                                                                         (map #(keys %))
                                                                         (flatten)
                                                                         (distinct)
                                                                         (filter #(clojure.string/includes? % "computed_region")))
                                            ds_computed_regions     ; filters the dataset by the predicates identified
                                                                    ; inside the predicate vector. The flatten operation
                                                                    ; removes the parentheses around each Boolean value
                                                                    ; in order to filter them as a single vector by
                                                                    ; true. The count operation will return the amount
                                                                    ; of true values identified in the transformed
                                                                    ; dataset.
                                                                    (->> ds_filtered
                                                                         (map #(is_in_vector % comp_region_predicates))
                                                                         (flatten)
                                                                         (filter #(true? %))
                                                                         (count))]
                                           { "count_meteor" ds_computed_regions }))
     get_meteor_falls_near_paris       (fn
                                         []
                                         {:post [(s/valid? map? %)]}
                                         (let
                                           [la_longitude_de_paris ; The Parisian longitude adapted from
                                                                  ; https://www.latlong.net/place/paris-france-1666.html
                                                                  2.349014
                                            la_latitude_de_paris  ; The Parisian latitude adapted from
                                                                  ; https://www.latlong.net/place/paris-france-1666.html
                                                                  48.864716
                                            le_rayon_de_paris     ; The search radius from the Parisian coordinate
                                                                  4
                                            cest_pres_de_paris    ; cest_pres_de_paris is a function that determines,
                                                                  ; whether or not there are meteorites within the
                                                                  ; circumference of Paris. It compares the sum of the
                                                                  ; squared difference of the searching coordinate and
                                                                  ; the Parisian coordinate with the squared radius in
                                                                  ; order to measure whether or not the searched
                                                                  ; meteorite is within search distance. Takes an
                                                                  ; external coordinate as input.
                                                                  (fn
                                                                    [le_point_exterieur]
                                                                    (let
                                                                      [au_carre                ; au_carre is a function;
                                                                                               ; squares any number.
                                                                                               (fn
                                                                                                 [numero]
                                                                                                 {:pre  [(s/valid? number? numero)]
                                                                                                  :post [(s/valid? number? %)]}
                                                                                                 (Math/pow numero 2))
                                                                       la_longitude_exterieur  ; external longitude
                                                                                               (first le_point_exterieur)
                                                                       la_latitude_exterieur   ; external latitude
                                                                                               (last le_point_exterieur)
                                                                       difference_x            ; longitudinal difference
                                                                                               (- la_longitude_exterieur la_longitude_de_paris)
                                                                       difference_y            ; latitudinal difference
                                                                                               (- la_latitude_exterieur la_latitude_de_paris)
                                                                       diff_x_au_carre         ; difference_x squared
                                                                                               (au_carre difference_x)
                                                                       diff_y_au_carre         ; difference_y squared
                                                                                               (au_carre difference_y)
                                                                       rayon_au_carre          ; search radius squared
                                                                                               (au_carre le_rayon_de_paris)]
                                                                      (<= (+ diff_x_au_carre diff_y_au_carre) rayon_au_carre)))
                                            tous_les_meteorites   ; filters the dataset based on the conditions whether
                                                                  ; or not the meteorites have fallen and possess a
                                                                  ; geolocation. It parameterises the get-in operation
                                                                  ; which extracts the vectorial value coordinates from
                                                                  ; the geolocation key of the dataset as it is a nested
                                                                  ; map. Then it will count the amount of identified
                                                                  ; meteorites within the search distance specified.
                                                                  (->> dataset
                                                                       (filter_by_fell)
                                                                       (filter #(contains? % "geolocation"))
                                                                       (filter #(cest_pres_de_paris (get-in % ["geolocation" "coordinates"])))
                                                                       (count))]
                                           { "count_meteor" tous_les_meteorites "radius" le_rayon_de_paris }))]
   ; If the input style corresponds to any of the five codes, perform a query. Otherwise print error message.
   (cond
     ; Which year saw the most individual meteor falls?
     (= style "max_fall_year")     (get_year_highest_meteor_fall)
     ; Which year saw the heaviest collective meteor fall?
     (= style "heaviest_year")     (get_year_heaviest_meteor_fall)
     ; From the year 2000 and onwards, what is the average collective mass and frequency of meteor falls per year?
     (= style "avg_2000_plus")     (get_2000_plus_avg_meteor_falls)
     ; How many fallen meteorites have a computed region?
     (= style "computed_regions")  (get_computed_regions_meteor_falls)
     ; How many meteorites have fallen within a radius of 4 from Paris?
     (= style "pres_de_paris")     (get_meteor_falls_near_paris)
     :else                         "Please specify a style of execution that is either 'max_fall_year', 'heaviest_year', 'avg_2000_plus', 'computed_regions', or 'pres_de_paris'")))

(defn task_three
  [input]
  {:pre  [(s/valid? string? input)]
   :post [(or (s/valid? coll? %)
              (s/valid? string? %))]}
  (let
    [names                     ["Alice" "Bob" "Charlie" "David" "Eve" "Fred"
                                "Ginny" "Harriet" "Ileana" "Joseph" "Kincaid" "Larry"]
     flower_initials           { "V" "violets"
                                 "R" "radishes"
                                 "C" "clover"
                                 "G" "grass"   }
     kindergarten              { "windowsill" "[window][window][window}"
                                 "frontrow"   "VRCGVVRVCGGCCGVRGCVCGCGV"
                                 "backrow"    "VRCCCGCRRGVCGCRVVCVGCGCV" }
     get_size_by_name          ; get_size_by_name is a function that takes a name as parameter and determines its
                               ; numerical representation (e.g. "A" predicates 1, "B" 2, etc.). Then, it will multiply
                               ; the number by two, since each child is allocated two spaces per row from left to
                               ; right in alphabetical order.
                               (fn
                                 [name]
                                 {:pre  [(s/valid? string? name)]
                                  :post [(s/valid? number? %)]}
                                 (let
                                   [get_position_by_initial (fn
                                                              [i]
                                                              (- (int (.charAt (clojure.string/upper-case i) 0)) 64))
                                    alpha_pos               (get_position_by_initial (subs name 0))]
                                   (* alpha_pos 2)))
     get_flower_set            ; get_flower_set is a function that retrieves two flowers from a specific row. It is
                               ; using substring in order to filter the flowers based on the start and end values of
                               ; the child.
                               (fn
                                 [flower_row i j]
                                 {:pre  [(s/valid? string? flower_row)
                                         (s/valid? integer? i)
                                         (s/valid? integer? j)]
                                  :post [(s/valid? string? %)]}
                                 (subs (get kindergarten flower_row) i j))
     get_flower_names          ; get_flower_names is a function that takes a set of four flowers as a collection and
                               ; checks each letter against the map flower_initials in order to retrieve the full
                               ; name of each flower in a map.
                               (fn
                                 [flower_initials_str]
                                 {:pre  [(s/valid? coll? flower_initials_str)]
                                  :post [(s/valid? vector? %)]}
                                 (->> flower_initials_str
                                      (map str)
                                      (map #(get flower_initials %))
                                      (vec)))
     determine_flowers_by_name ; determine_flowers_by_name is a function that takes a name as parameter and performs
                               ; various operations based on string manipulation in order to retrieve the name of
                               ; certain flowers based on letters between A and L (range 12). Names going above that
                               ; range have no allocated space in the kindergarten.
                               (fn
                                 [name]
                                 {:pre  [(s/valid? string? name)]
                                  :post [(or (s/valid? map? %)
                                             (s/valid? string? %))]}
                                 (let
                                   [end (get_size_by_name name)]
                                   (if (<= end 24)
                                     (let
                                       [start (- end 2)
                                        front (get_flower_set "frontrow" start end)
                                        back  (get_flower_set "backrow" start end)
                                        list  (get_flower_names (concat front back))]
                                       {"name" name "flower_set" (str front back) "flower_names" list})
                                     (str "There is insufficient space for people whose initials "
                                          "do not start with letters within the range A-L"))))]
    ; If the string input contains no characters, parameterise each element in the names vector. Otherwise the input string.
    (if (clojure.string/blank? input)
      (map #(determine_flowers_by_name %) names)
      (determine_flowers_by_name input))))

(defn task_two
  [input style]
  {:pre  [(s/valid? integer? input)
          (s/valid? string? style)]
   :post [(or (s/valid? map? %)
              (s/valid? string? %))]}
  (let
    [coin_types                [25 10 5 1]
     get_coin_types_by_change  ; Filters the coin list to ensure it only contains
                               ; coins that are less than or equal the input value.
                               (fn
                                 [change]
                                 ; filtering could have been alternatively executed
                                 ; with (filter #(>= change %) coin_types)
                                 {:pre  [(s/valid? integer? change)]
                                  :post [(s/valid? vector? %)]}
                                 (->> (for
                                        [c coin_types :when (>= change c)]
                                        c)
                                      (vec)))
     count_change_marity       ; Multi-arity version of count change. Handles up to 120 coins performance-wise.
                               (fn
                                 [amount]
                                 {:pre  [(s/valid? integer? amount)]
                                  :post [(s/valid? map? %)]}
                                 (let
                                   [; memset is an in-function defined atom that stores all the coin combinations
                                    ; and their hashed values in memory. Required for condition checks to ensure
                                    ; no duplicates are considered for counting existing combinations.
                                    memset    (atom {})
                                    ; store_set is a function that associates a coin combination and its hash
                                    ; value inside memset. swap! persists changes to memset throughout runtime.
                                    store_set (fn
                                                [set]
                                                {:pre  [(s/valid? coll? set)]}
                                                (swap! memset assoc (hash set) set))
                                    ; add_ones is a function that duplicates 1 if the filtered coin types list
                                    ; only contains 1. Repeats 1 by the remaining amount and pushes them into
                                    ; the coin_set.
                                    add_ones  (fn
                                                [remainder coin_set]
                                                {:pre  [(s/valid? integer? remainder)
                                                        (s/valid? coll? coin_set)]
                                                 :post [(s/valid? coll? %)]}
                                                (->> (repeat remainder 1)
                                                     (into coin_set)))]
                                   ; letfn is a special function construct that enables multi-arity within a locally-
                                   ; bound block. It is beneficial for mutual recursion.
                                   (letfn
                                     [; branch_coin is a 1-arity function that takes any number as amount, a
                                      ; counter, and a coin_set. Its main objective is to overload parameters
                                      ; for triggering the 2-arity function count_by_conditions in order to
                                      ; reduce the amount of count recursively until it has reached 0.
                                      (branch_coin
                                        [amount counter coin_set]
                                        {:pre  [(s/valid? integer? amount)
                                                (s/valid? integer? counter)
                                                (s/valid? coll? coin_set)]
                                         :post [(s/valid? integer? %)]}
                                        (let
                                          [; xf is a transformer function that preemptively processes
                                           ; each coin inside coin_types by which it will be parameterised
                                           ; to the 2-arity function count_by_conditions alongside amount
                                           ; and counter for counting based on identified coin
                                           ; combinations.
                                           xf (map #(count_by_conditions amount % counter coin_set))]
                                          ; Retrieves the coin_set that is less than or equal the amount in
                                          ; order to execute count_by_conditions whereby each coin is counted
                                          ; against the amount. transduce will perform an addition operation to
                                          ; sum up each number from the lazy sequence as supplied by xf once its
                                          ; operation has been finalised.
                                          (->> (get_coin_types_by_change amount)
                                               (transduce xf +'))))
                                      ; count_by_conditions is a 2-arity function that determines whether or not
                                      ; the amount has reached zero. The coin_set variable contains the coins
                                      ; visited from previous iterations and stores the subtrahend of the
                                      ; minuend amount.
                                      (count_by_conditions
                                        [amount coin_type counter coin_set]
                                        {:pre  [(s/valid? integer? amount)
                                                (s/valid? integer? coin_type)
                                                (s/valid? integer? counter)
                                                (s/valid? coll? coin_set)]
                                         :post [(s/valid? integer? %)]}
                                        (let
                                          [is_coin_gt_one    (> coin_type 1)
                                           is_mod_eq_zero    (= (mod amount coin_type) 0)
                                           is_amount_gt_coin (> amount coin_type)
                                           sorted_map        ; If coin_type is greater than 1 then conjoin
                                                             ; it to the coin_set. Otherwise add the remaining
                                                             ; 1s to it.
                                                             (sort (if is_coin_gt_one
                                                                     (conj coin_set coin_type)
                                                                     (add_ones amount coin_set)))
                                           is_coin_set_nil   ; Perform a condition check on whether or not
                                                             ; the hash of the current sorted_map exists in
                                                             ; memory.
                                                             (nil? (get @memset (hash sorted_map)))
                                           condition_1       (and is_coin_gt_one is_amount_gt_coin)
                                           condition_2       (and is_mod_eq_zero is_coin_set_nil)]
                                          (cond
                                            ; If the amount is greater than coin_type and coin_type is greater
                                            ; than 1 then recurse by calling the 1-arity function branch_coin
                                            ; by parameterising the difference of amount and coin_type, counter,
                                            ; and sorted_map.
                                            condition_1 (branch_coin (- amount coin_type) counter sorted_map)
                                            ; If the amount modded is equal to 0 and the coin combination stored
                                            ; in sorted_map does not exist inside the atom memset then store it
                                            ; in memory and terminate this partial recursion by returning the
                                            ; counter incremented by one.
                                            condition_2 (do (store_set sorted_map) (inc counter))
                                            ; Terminate this partial recursion by returning the counter as it is
                                            ; if none of the above conditions have been met.
                                            :else       counter)))]
                                     { "changes" (branch_coin amount 0 []) "amount" amount "coins" (get_coin_types_by_change amount) })))
     count_change_matrices     ; Matrix version of count change. Handles up to 250 coins performance-wise.
                               (fn
                                 [amount]
                                 {:pre  [(s/valid? integer? amount)]
                                  :post [(s/valid? map? %)]}
                                 (let
                                   [combine_matrix      ; combine_matrix is a function that takes two vectors
                                                        ; that can be matrices of any dimension. Merges both
                                                        ; collections into one for further transformation.
                                                        (fn
                                                          [m1 m2]
                                                          {:pre  [(s/valid? coll? m1)
                                                                  (s/valid? coll? m2)]
                                                           :post [(s/valid? coll? %)]}
                                                          (-> m1
                                                              (m/transpose)
                                                              (into m2)))
                                    conjoin_matrix      ; conjoin_matrix is a function that merges two matrices
                                                        ; where nested vectors are conjoined together. In the
                                                        ; sense of counting coins, it is necessary for bulk
                                                        ; recording coin combinations.
                                                        (fn
                                                          [m1 m2]
                                                          {:pre  [(s/valid? coll? m1)
                                                                  (s/valid? coll? m2)]
                                                           :post [(s/valid? coll? %)]}
                                                          (-> m1
                                                              (combine_matrix m2)
                                                              (m/reshape [(inc (m/column-count m1)) (m/ecount m2)])
                                                              (m/transpose)))
                                    duplicate_carryover ; duplicate_carryover is a function that duplicates the
                                                        ; filtered result of the conjoined matrix by the amount of
                                                        ; coin_types for the next recursion. It is necessary to
                                                        ; clone them as they need to be transposed in order to
                                                        ; bulk record the next coins for identifying coin
                                                        ; combinations that are equal to the amount specified.
                                                        (fn
                                                          [m1 target_row]
                                                          {:pre  [(s/valid? coll? m1)
                                                                  (s/valid? integer? target_row)]
                                                           :post [(s/valid? coll? %)]}
                                                          (if (= target_row 1)
                                                            m1
                                                            (loop
                                                              [m2 m1]
                                                              (if (= (m/ecount m2) (* (m/ecount m1) target_row))
                                                                (m/reshape m2 [(* target_row (m/row-count m1)) (m/column-count m1)])
                                                                (->> m2
                                                                     (m/transpose)
                                                                     (combine_matrix m1)
                                                                     (m/transpose)
                                                                     (recur))))))]
                                   (loop
                                     [counter ; counter counts the amount of uniquely recorded coin combinations.
                                              (BigInteger/valueOf 0)
                                      changes ; changes is a matrix that carries nested vectors which represent
                                              ; a set of previously visited coins that have been conjoined by the
                                              ; coin_types in past recursions. Each vector contains a coin
                                              ; combination whose aim is to fill up until they are equal to the
                                              ; value specified in the amount.
                                              (conjoin_matrix [[]] [(get_coin_types_by_change amount)])]
                                     (let
                                       [increment ; increment is the incremented value of counter that transforms
                                                  ; changes by filtering the collection by the condition whether
                                                  ; or not the total of the nested vectors are equal to amount. If
                                                  ; the condition has been satisfied, it will be incremented.
                                                  (->> changes
                                                       (filter #(and (= (reduce + %) amount)))
                                                       (count)
                                                       (BigInteger/valueOf)
                                                       (.add counter))
                                        carryover ; carryover is a uniquely filtered matrix that does the opposite
                                                  ; of increment which is filtering the collection by the
                                                  ; condition whether or not the total of the nested vectors are
                                                  ; less than the amount specified.
                                                  (->> changes
                                                       (filter #(< (reduce + %) amount)))]
                                       (if-not (empty? carryover)
                                         (let
                                           [coin_types_cloned ; coin_types_cloned uniquely filters carryover so
                                                              ; that there are only distinct coins types.
                                                              (->> carryover
                                                                   (m/to-vector)
                                                                   (distinct)
                                                                   (repeat (count carryover)))]
                                           ; executes a set of vectorial transformations where the carryover sets
                                           ; will be duplicated by the amount of coin_types_cloned and transposed so
                                           ; that it can be conjoined for identifying new coin combinations in the
                                           ; next recursion as a distinctly sorted matrix.
                                           (->> coin_types_cloned
                                                (conjoin_matrix (->> coin_types_cloned
                                                                     (m/column-count)
                                                                     (duplicate_carryover carryover)))
                                                (pmap sort)
                                                (distinct)
                                                (recur increment)))
                                         { "changes" increment "amount" amount "coins" (get_coin_types_by_change amount) })))))
     count_change_upwards      ; Counting up version of count change. Handles n coins performance-wise.
                               (fn
                                 [amount coins]
                                 {:pre  [(s/valid? integer? amount)
                                         (s/valid? vector? coins)]
                                  :post [(s/valid? map? %)]}
                                 (let
                                   [coins_scoped ; Transforms the input coins into a map.
                                                 (zipmap (range 1 (inc (count coins))) coins)]
                                   ; Main engine that performs the counting recursively. Defn- is used to denote that
                                   ; it is a privately defined function since it has to be used with memoize which only
                                   ; supports defn rather than let or letfn functions .
                                   (defn- count_recursor
                                     [amount coin_count]
                                     {:pre  [(s/valid? integer? amount)
                                             (s/valid? integer? coin_count)]
                                      :post [(s/valid? integer? %)]}
                                     (cond
                                       ; If the amount is negative or coin_count is empty, return 0.
                                       (or (< amount 0) (= coin_count 0)) 0
                                       ; If the amount is equal to 0, return 1.
                                       (= amount 0) 1
                                       ; Otherwise, perform an addition between two recursive calls
                                       ; where one parameterises the current amount and decrements
                                       ; the coin count by one whereas the other parameterises the
                                       ; difference between the amount and the current coin type
                                       ; stored at the position of coin count but maintains its
                                       ; current index.
                                       :else (+' (count_recursor amount (dec coin_count))
                                                 (count_recursor (-' amount (get coins_scoped coin_count))
                                                                 coin_count))))
                                   ; Memorises the function count_recursor in order to improve the performance
                                   ; by remembering previous values stored on the stack during runtime.
                                   (def count_recursor (memoize count_recursor))
                                   (let
                                     [changes ; Repetitively executes count_recursor by counting
                                              ; upwards with the guaranteed benefit of remembering
                                              ; results from previous executions on the stack
                                              ; during runtime by preventing StackOverflowErrors
                                              ; as usually encountered without memorisation.
                                              (->> (range (inc amount))
                                                   (map #(count_recursor % (count coins_scoped)))
                                                   (last))]
                                     { "changes" changes "amount" amount "coins" coins })))]
      ; If the input style corresponds to any of the four codes, perform an algorithm. Otherwise print error message.
      (cond
        (= style "marity")      (count_change_marity input)
        (= style "matrix")      (count_change_matrices input)
        (= style "upcount")     (count_change_upwards input [25 10 5 1])
        (= style "upcount100")  (count_change_upwards input [100 50 25 10 5 1])
        :else                   "Please specify a style of execution that is either 'marity', 'matrix', 'upcount', or 'upcount100'")))

(defn task_one
  [input]
  ; Spec: Input can either be a Vector or any numeric value
  {:pre [(or (s/valid? vector? input)
             (s/valid? number? input))]
   :post [(s/valid? coll? %)]}
  (let
    [to_square   ; to_square is a function that takes a number as parameter and returns it squared.
                 (fn
                   [number]
                   {:pre  [(s/valid? number? number)]
                    :post [(s/valid? number? %)]}
                   (* number number))
     lazy_square ; lazy_square takes a list as parameter and uses list comprehension to
                 ; filter it by numeric values to ensure that non-compliant datatypes
                 ; are skipped. Then it will return a lazy sequence of the filtered input
                 ; raised to the power of 2.
                 (fn
                   [list]
                   {:pre  [(s/valid? vector? list)]
                    :post [(s/valid? coll? %)]}
                   (for [el list
                         :when (number? el)
                         :let [sqr (to_square el)]]
                     sqr))]
    ; Performs preliminary data type checks for choosing the appropriate
    ; mode of data transformation by type and returns a lazy sequence
    (if (number? input)
      (lazy-seq [(to_square input)])
      (lazy_square input))))

(deftest test_task_one
  (testing "existential values"
    (testing "numeric values with singular input?"
      (is (some #(number? %) (task_one 13))))
    (testing "numeric values with vectorial input?"
      (is (some #(number? %) (task_one [1 5 "Chris" "Bates" 13]))))
    (testing "string values with singular input?"
      (is (some #(string? %) (task_one "Chris"))))
    (testing "string values with vectorial?"
      (is (some #(string? %) (task_one [1 5 "Chris" "Bates" 13]))))
    (testing "13 squared?"
      (is (some #(= (* 13 13) %) (task_one [1 5 "Chris" "Bates" 13]))))
    (testing "string squared?"
      (is (some #(= "Chris²" %) (task_one [1 5 "Chris" "Bates" 13])))))

  (testing "removed datatypes"
    (testing "string only with singular input?"
      (is (empty? (task_one "Chris"))))
    (testing "string only with vectorial input?"
      (is (empty? (task_one ["Chris" "Bates" "Fundamentals" "of" "Functional" "Programming" "Languages"]))))
    (testing "numeric only with singular input?"
      (is (empty? (task_one 13))))
    (testing "numeric only with vectorial input?"
      (is (empty? (task_one [1 2 3 4 5 6 7]))))
    (testing "boolean only with singular input?"
      (is (empty? (task_one true))))
    (testing "boolean only with vectorial input?"
      (is (empty? (task_one [true false])))))

  (testing "error/exception handling"
    (testing "error with non-vectorial input?"
      (is (thrown? Error (task_one 12))))
    (testing "error with non-vectorial input?"
      (is (thrown? Error (task_one "Chris"))))
    (testing "error with non-vectorial input?"
      (is (thrown? Error (task_one true))))
    (testing "error with 2D-vectorial input?"
      (is (thrown? Error (task_one [[12 13] [14 15]]))))
    (testing "exception with 2D-vectorial input?"
      (is (thrown? Exception (task_one [[12 13] [14 15]]))))
    (testing "exception with non-vectorial input?"
      (is (thrown? Exception (task_one 12))))
    (testing "exception with non-vectorial input?"
      (is (thrown? Exception (task_one "Chris"))))
    (testing "exception with non-vectorial input?"
      (is (thrown? Exception (task_one true))))))

(deftest test_task_two
  (testing "result validity check"
    (testing "is positive?"
      (is (pos? (get (task_two 100 "marity") "changes"))))
    (testing "is expected value?"
      (is (= 100 (get (task_two 100 "marity") "changes")))
      (is (= 13 (get (task_two 25 "matrix") "changes")))
      (is (= 1333983445341383545001N (get (task_two 1000000 "upcount100") "changes"))))
    (testing "is negative?"
      (is (neg? (get (task_two -25 "matrix") "changes"))))
    (testing "is string?"
      (is (string? (get (task_two 100 "marity") "changes")))))

  (testing "algorithm validity check"
    (testing "are results of multi-arity and matrix algorithms equal?"
      (is (= (get (task_two 100 "marity") "changes") (get (task_two 100 "matrix") "changes"))))
    (testing "are results of matrix and count-up algorithms equal?"
      (is (= (get (task_two 100 "matrix") "changes") (get (task_two 100 "upcount") "changes"))))
    (testing "are results of basic count-up and extended count-up equal?"
      (is (= (get (task_two 100 "upcount") "changes") (get (task_two 100 "upcount100") "changes")))))

  (testing "error/exception handling"
    (testing "multi-arity algorithm"
      (is (thrown? Error (task_two [100] "marity")))
      (is (thrown? Exception (task_two [100] "marity")))
      (is (thrown? Error (task_two 1000000 "marity")))
      (is (thrown? Exception (task_two 1000000 "marity"))))
    (testing "matrix algorithm"
      (is (thrown? Error (task_two [100] "matrix")))
      (is (thrown? Exception (task_two [100] "matrix")))
      (comment
        "sealed away with comment as calculation time is too expensive"
        (is (thrown? Error (task_two 1000000 "matrix")))
        (is (thrown? Exception (task_two 1000000 "matrix")))))
    (testing "basic count-up algorithm"
      (is (thrown? Error (task_two [100] "upcount")))
      (is (thrown? Exception (task_two [100] "upcount")))
      (is (thrown? Error (task_two 1000000 "upcount")))
      (is (thrown? Exception (task_two 1000000 "upcount"))))
    (testing "extended count-up algorithm"
      (is (thrown? Error (task_two [100] "upcount100")))
      (is (thrown? Exception (task_two [100] "upcount100")))
      (is (thrown? Error (task_two 1000000 "upcount100")))
      (is (thrown? Exception (task_two 1000000 "upcount100"))))
    (testing "non-conformative inputs"
      (is (thrown? Error (task_two "Chris Bates" 13)))
      (is (thrown? Exception (task_two "Chris Bates" 13))))))

(deftest test_task_three
  (testing "validity check"
    (testing "is expected result?"
      (is (= 12 (count (task_three "")))))
    (testing "is result a map?"
      (is (map? (task_three 13)))
      (is (map? (task_three "Sylvain")))
      (is (map? (task_three "Andy"))))
    (testing "is result a string?"
      (is (string? (task_three 13)))
      (is (string? (task_three "Sylvain")))
      (is (string? (task_three "Andy"))))
    (testing "is nested result the expected flower set?"
      (is (= "VRVR" (get (task_three "Andy") "flower_set")))
      (is (= (get (first (task_three "")) "flower_set") (get (task_three "Andy") "flower_set"))))
    (testing "is result empty?"
      (is (empty? (task_three "Simon Polovina")))))

  (testing "error/exception handling"
    (is (thrown? Error (task_three true)))
    (is (thrown? Exception (task_three [false])))
    (is (thrown? Error (task_three 13)))
    (is (thrown? Exception (task_three 13)))))

(deftest test_task_four
  (testing "validity checks"
    (testing "is result a string?"
      (is (string? (task_four 23))))
    (testing "is result a map?"
      (is (map? (task_four "max_fall_year")))
      (is (map? (task_four "heaviest_year")))
      (is (map? (task_four "avg_2000_plus")))
      (is (map? (task_four "computed_regions")))
      (is (map? (task_four "pres_de_paris")))
      (is (map? (task_four "Chris Bates"))))
    (testing "is result empty?"
      (is (empty? (task_four "max_fall_year")))
      (is (empty? (task_four "heaviest_year")))
      (is (empty? (task_four "avg_2000_plus")))
      (is (empty? (task_four "computed_regions")))
      (is (empty? (task_four "pres_de_paris")))
      (is (empty? (task_four "Chris Bates"))))
    (testing "is nested result numeric?"
      (is (number? (get (task_four "max_fall_year") "max_fall")))
      (is (number? (get (task_four "max_fall_year") "max_fall_year")))
      (is (number? (get (task_four "heaviest_year") "heaviest")))
      (is (number? (get (task_four "heaviest_year") "heaviest_year")))
      (is (number? (get (task_four "avg_2000_plus") "avg_mass")))
      (is (number? (get (task_four "avg_2000_plus") "avg_falls")))
      (is (number? (get (task_four "computed_regions") "count_meteor")))
      (is (number? (get (task_four "pres_de_paris") "count_meteor"))))
    (testing "is result complying with conditions?"
      (is (< 12 (get (task_four "max_fall_year") "max_fall")))
      (is (= 1945 (get (task_four "max_fall_year") "max_fall_year")))
      (is (< 2000000 (get (task_four "heaviest_year") "heaviest")))
      (is (= "1947" (get (task_four "heaviest_year") "heaviest_year")))
      (is (< 13000 (get (task_four "avg_2000_plus") "avg_mass")))
      (is (> 3 (get (task_four "avg_2000_plus") "avg_falls")))
      (is (< 100 (get (task_four "computed_regions") "count_meteor")))
      (is (= 30 (get (task_four "pres_de_paris") "count_meteor")))))

  (testing "error/exception handling"
    (testing "is singular input erroneous?"
      (is (thrown? Error (task_four 13)))
      (is (thrown? Exception (task_four false)))
      (is (thrown? Error (task_four "pres_de_paris")))
      (is (thrown? Exception (task_four "pres_de_paris"))))
    (testing "is vectorial input erroneous?"
      (is (thrown? Error (task_four ["max_fall_year" "heaviest_year" "avg_2000_plus" "computed_regions" "pres_de_paris"])))
      (is (thrown? Exception (task_four ["max_fall_year" "heaviest_year" "avg_2000_plus" "computed_regions" "pres_de_paris"]))))))

(defn -main
  []
  (let
    [q1_answer_formatter           (fn
                                     [q1 initial]
                                     (str "Before: " initial "\nAfter: " (vec q1)))
     q2_answer_formatter           (fn
                                     [q2 style]
                                     (str "In the " style " version, there are " (get q2 "changes")
                                          " ways of changing " (get q2 "amount") " USD based on "
                                          (get q2 "coins") " coins"))
     q3_answer_formatter           (fn
                                     [q3]
                                     (str (get q3 "name") " has the following flowers: "
                                          (get q3 "flower_names")))
     q1_input_vector               [1 5 "Chris" "Bates" 13]
     q1_input_number               7
     q1_task_vector                (task_one q1_input_vector)
     q1_task_number                (task_one q1_input_number)
     q1_answer_vector              (q1_answer_formatter q1_task_vector q1_input_vector)
     q1_answer_number              (q1_answer_formatter q1_task_number q1_input_number)

     q2_input                      100
     q2_task_marity                (task_two q2_input "marity")
     q2_task_matrix                (task_two q2_input "matrix")
     q2_task_upcount               (task_two q2_input "upcount")
     q2_task_upcount_100           (task_two 1000000 "upcount100")
     q2_task_nostyle               (task_two 0 "")
     q2_answer_marity              (q2_answer_formatter q2_task_marity "multi-arity")
     q2_answer_matrix              (q2_answer_formatter q2_task_matrix "matrix")
     q2_answer_upcount             (q2_answer_formatter q2_task_upcount "count-up")
     q2_answer_upcount_100         (q2_answer_formatter q2_task_upcount_100 "count-up")
     q2_answer_nostyle             q2_task_nostyle

     q3_task_kindergarten          (task_three "")
     q3_task_name                  (task_three "Chris Bates")
     q3_task_offbounds             (task_three "Remy")
     q3_answer_kindergarten        (map #(q3_answer_formatter %) q3_task_kindergarten)
     q3_answer_name                (q3_answer_formatter q3_task_name)
     q3_answer_offbounds           q3_task_offbounds

     q4_task_max_fall_year         (task_four "max_fall_year")
     q4_task_heaviest_year         (task_four "heaviest_year")
     q4_task_avg_2000_plus         (task_four "avg_2000_plus")
     q4_task_computed_regions      (task_four "computed_regions")
     q4_task_near_paris            (task_four "pres_de_paris")
     q4_task_nostyle               (task_four "")

     q4_answer_max_fall_year       (str "The highest amount of meteor falls took place in "
                                        (get q4_task_max_fall_year "max_fall_year") " with a total amount of "
                                        (get q4_task_max_fall_year "max_fall"))
     q4_answer_heaviest_year       (str "The heaviest collective meteor fall took place in "
                                        (get q4_task_heaviest_year "heaviest_year") " with a total mass of "
                                        (get q4_task_heaviest_year "heaviest"))
     q4_answer_avg_2000_plus       (str "As of 2000, there was an average collective mass of "
                                        (get q4_task_avg_2000_plus "avg_mass") " and an average frequency of "
                                        (get q4_task_avg_2000_plus "avg_falls") " meteor falls per year")
     q4_answer_computed_regions    (str "There are " (get q4_task_computed_regions "count_meteor")
                                        " computed regions that have exprienced meteor falls")
     q4_answer_near_paris          (str "Il y a " (get q4_task_near_paris "count_meteor")
                                        " meteorites qu'ils sont prés de Paris avec un rayon de "
                                        (get q4_task_near_paris "radius"))
     q4_answer_nostyle             q4_task_nostyle]
    (println "Question 1:")
    (println q1_answer_vector)
    (println q1_answer_number)
    (newline)
    (println "Question 2:")
    (println q2_answer_marity)
    (println q2_answer_matrix)
    (println q2_answer_upcount)
    (println q2_answer_upcount_100)
    (println q2_answer_nostyle)
    (newline)
    (println "Question 3:")
    (run! println q3_answer_kindergarten)
    (println q3_answer_name)
    (println q3_answer_offbounds)
    (newline)
    (println "Question 4:")
    (println q4_answer_max_fall_year)
    (println q4_answer_heaviest_year)
    (println q4_answer_avg_2000_plus)
    (println q4_answer_computed_regions)
    (println q4_answer_near_paris)
    (println q4_answer_nostyle)))

(run-tests)
(newline)
(-main)