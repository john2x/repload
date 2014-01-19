(ns repload
  (:require [clojure.string :as string]
            [clojure.string :refer [split join]]))

(def ^:dynamic **exclude-prefixes** (atom ["clojure" "repload"]))

(declare get-refers-map rerefer realias)

(defn repload
  []
  (let [aliases      (apply hash-map (->> (ns-aliases *ns*)
                                          (map (fn [x] [(first x) (ns-name (second x))]))
                                          flatten))
        refers       (get-refers-map)]
    (doseq [refer-map refers]
      (rerefer refer-map))
    (doseq [alias-ns aliases]
      (apply realias alias-ns))))

(defn add-exclude-prefix!
  [prefix]
  (swap! **exclude-prefixes** conj prefix))

(defn set-exclude-prefixes!
  [prefixes]
  (reset! **exclude-prefixes** prefixes))

(defn- re-exclude-prefixes
  []
  (re-pattern (str "^(" (join "|" @**exclude-prefixes**) ")")))

(defn- var->str
  [v]
  (subs (str v) 2))

(defn- rerefer
  [refer-map]
  (let [ns   (symbol (first refer-map))
        vars (->> (second refer-map)
                  (map #(string/replace % #"^.*/" ""))
                  (map symbol)
                  vec)]
  (println "Reloading" (str ns) (str vars))
  (doseq [v vars]
    (try
      (require [ns :refer [v]] :reload)
      (catch IllegalAccessError e
        (println (str v " is no longer public. Not reloaded.")))))))

(defn- realias
  [alias ns]
  (println "Reloading" (str ns) "with alias" (str alias))
  (require [ns :as alias] :reload))

(defn- get-refers-map
  []
  (let [grouped-refers (->> (ns-refers *ns*) vals
                            (map var->str)
                            (remove #(re-find (re-exclude-prefixes) %))
                            (group-by #(first (split % #"/"))))]
    grouped-refers))

