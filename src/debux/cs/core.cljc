(ns debux.cs.core
  #?(:cljs (:require-macros debux.core
                            debux.dbg
                            debux.dbgn
                            debux.cs.clog
                            debux.cs.clogn
                            debux.cs.macro-types))
  (:require [debux.common.util :as ut]
            [debux.cs.util :as cs.ut] ))

#?(:cljs (enable-console-print!))

(def set-print-length! ut/set-print-length!)

(defmacro set-debug-mode! [val]
  `(ut/set-debug-mode! ~val))

(defmacro set-ns-whitelist! [whitelist]
  `(ut/set-ns-whitelist! ~whitelist))

(defmacro set-ns-blacklist! [blacklist]
  `(ut/set-ns-blacklist! ~blacklist))


;;; debugging APIs
(defmacro dbg [form & opts]
  (let [ns (str *ns*)
        line (:line (meta &form))
        local-ks (if (ut/cljs-env? &env)
                   (keys (:locals &env))
                   (keys &env))
        opts' (ut/prepend-src-info opts ns line)]
    `(if (ut/debug-enabled? ~ns)
       (debux.dbg/dbg ~form
                      (zipmap '~local-ks [~@local-ks])
                      ~(ut/parse-opts opts'))
       ~form)))

(defmacro dbgn [form & opts]
  (let [ns (str *ns*)
        line (:line (meta &form))
        local-ks (if (ut/cljs-env? &env)
                   (keys (:locals &env))
                   (keys &env))
        opts' (ut/prepend-src-info opts ns line)]
   `(if (ut/debug-enabled? ~ns)
      (debux.dbgn/dbgn ~form
                       (zipmap '~local-ks [~@local-ks])
                       ~(ut/parse-opts opts'))
      ~form)))

(defmacro clog [form & opts]
  (let [ns (str *ns*)
        line (:line (meta &form))
        local-ks (keys (:locals &env))
        opts' (ut/prepend-src-info opts ns line)]
    `(if (ut/debug-enabled? ~ns)
       (debux.cs.clog/clog ~form
                           (zipmap '~local-ks [~@local-ks])
                           ~(ut/parse-opts opts'))
       ~form)))

(defmacro clogn [form & opts]
  (let [ns (str *ns*)
        line (:line (meta &form))
        local-ks (keys (:locals &env))
        opts' (ut/prepend-src-info opts ns line)]
    `(if (ut/debug-enabled? ~ns)
       (debux.cs.clogn/clogn ~form
                             (zipmap '~local-ks [~@local-ks])
                             ~(ut/parse-opts opts'))
       ~form)))

(defmacro dbg-last
  [& args]
  (let [form (last args)
        opts (butlast args)]
    `(dbg ~form ~@opts)))

(defmacro clog-last
  [& args]
  (let [form (last args)
        opts (butlast args)]
    `(clog ~form ~@opts)))


;;; tag literals #d/dbg, #d/dbgn, #d/clog and #d/clogn
(defmacro dbg* [form meta]
  (let [ns (str *ns*)
        line (:line meta)
        opts [:ns ns :line line]]
    `(if (ut/debug-enabled? ~ns)
       (debux.dbg/dbg ~form {} ~(ut/parse-opts opts))
       ~form)))

(defmacro dbgn* [form meta]
  (let [ns (str *ns*)
        line (:line meta)
        opts [:ns ns :line line]]
    `(if (ut/debug-enabled? ~ns)
       (debux.dbgn/dbgn ~form {} ~(ut/parse-opts opts))
       ~form)))

(defmacro clog* [form meta]
  (let [ns (str *ns*)
        line (:line meta)
        opts [:ns ns :line line]]
    `(if (ut/debug-enabled? ~ns)
       (debux.cs.clog/clog ~form {} ~(ut/parse-opts opts))
       ~form)))

(defmacro clogn* [form meta]
  (let [ns (str *ns*)
        line (:line meta)
        opts [:ns ns :line line]]
    `(if (ut/debug-enabled? ~ns)
       (debux.cs.clogn/clogn ~form {} ~(ut/parse-opts opts))
       ~form)))

(defn clog-tag [form]
  `(clog* ~form ~(meta form)))

(defn clogn-tag [form]
  `(clogn* ~form ~(meta form)))


(defmacro break [& opts]
  (let [ns (str *ns*)]
    `(when (ut/debug-enabled? ~ns)
       (debux.cs.clogn/break  ~(ut/parse-opts opts)))))

;;; turn-off versions
(defmacro dbg_ [form & opts] form)
(defmacro dbgn_ [form & opts] form)
(defmacro dbg-last_ [& args] (last args))

(defmacro clog_ [form & opts] form)
(defmacro clogn_ [form & opts] form)
(defmacro clog-last_ [& args] (last args))

(defmacro break_ [& opts])


;;; macro registering APIs
(defmacro register-macros! [macro-type symbols]
  `(debux.cs.macro-types/register-macros! ~macro-type ~symbols))

(defmacro show-macros
  ([] `(debux.cs.macro-types/show-macros))
  ([macro-type] `(debux.cs.macro-types/show-macros ~macro-type)))


;;; style option API
(def merge-styles cs.ut/merge-styles)
